package com.forums.forums.services.filesystemservice;

import com.forums.forums.model.mo.User;
import com.forums.forums.services.logservice.LogService;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeType;

/**
 * Struttura del Filesystem:
 *
 * /opt/tomcat/webapps/Uploads/forums/users/
 *   ├─ userID/                  // Directory unica per ogni utente, nominata con il suo userID
 *   │   ├─ profilePic/          // Cartella per l'immagine del profilo
 *   │   │   └─ profilePic.png   // Immagine del profilo dell'utente
 *   │   └─ medias/              // Cartella per i media caricati dall'utente
 *   │       ├─ postID/          // Sottocartella specifica per un post, nominata con il suo postID
 *   │       │   ├─ file1.jpg    // Primo file multimediale specifico del post
 *   │       │   └─ file2.mp4    // Secondo file multimediale specifico del post
 *   │       └─ ...              // Altri file o cartelle multimediali caricati dall'utente
 *   └─ ...                     // Altre cartelle per altri utenti, ciascuna nominata con il loro userID
 *
 * Nota: La struttura sopra rappresenta la disposizione delle directory e dei file all'interno del sistema
 * di archiviazione del server. Le foto profilo degli utenti eliminati vengono rimosse per rispettare la
 * privacy e la gestione dei dati.
 */



public class FileSystemService {

    // Cartella di base del filesystem dentro alla quale vengono salvate le foto
    // profilo degli utenti ed i media da loro caricati
    public static final String BASE_DIR_PATH = File.separator + "opt" +
                                                File.separator + "tomcat" +
                                                File.separator + "webapps";

    public static final String DEFAULT_PROFILE_PIC_PATH = File.separator + "images" +
                                                    File.separator + "defaultProfilePic.png";

    public static final String DELETED_PROFILE_PIC_PATH = File.separator + "images" +
                                                    File.separator + "deletedProfilePic.png";

    public FileSystemService() {

    }

    public void createFile(Part filePart, String path) throws IOException {
        try (InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void deleteFile(String path) throws IOException {
        Path fileToDelete = Paths.get(path);
        if (Files.exists(fileToDelete)) {
            Files.delete(fileToDelete);
        } else {
            throw new IOException("File non trovato: " + path);
        }
    }

    public boolean fileExists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isRegularFile(path);
    }

    public boolean createDirectory(String path) {
        Logger logger = LogService.getApplicationLogger();
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Errore nella creazione della cartella " + path + ": ", e);
            return false;
        }
        return true;
    }

    public void deleteDirectory(String path) throws IOException {
        // Convalida del percorso per evitare eliminazioni pericolose
        if (path == null || path.trim().isEmpty() || path.equals("/") || path.equals("C:\\") || path.equals("/root")) {
            throw new IllegalArgumentException("Percorso non eliminabile: " + path);
        }

        Path directory = Paths.get(path);
        if (Files.exists(directory) && Files.isDirectory(directory)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        deleteDirectory(entry.toString());
                    } else {
                        Files.delete(entry);
                    }
                }
            }
            Files.delete(directory);
        } else {
            throw new IllegalArgumentException("Path does not exist or is not a directory: " + path);
        }
    }

    public boolean directoryExists(String directoryPath) {
        Path path = Paths.get(directoryPath);
        return Files.exists(path) && Files.isDirectory(path);
    }

    public String getUserDirectoryPath(Long userID) {

        // Costruzione del percorso dell'immagine del profilo
        String imagePath = BASE_DIR_PATH + File.separator + "Uploads" + File.separator + "forums" + File.separator + "users" + File.separator + userID;

        return imagePath;
    }

    public String getUserMediaDirectoryPath(Long userID) {
        return (getUserDirectoryPath(userID) + File.separator + "medias");
    }

    public String getUserProfilePicDirectoryPath(Long userID) {
        return (getUserDirectoryPath(userID) + File.separator + "profilePic");
    }

    public String getUserProfilePicPath(Long userID) {
        return (getUserProfilePicDirectoryPath(userID) + File.separator + "profilePic.png");
    }

    public static String getUserRelativeProfilePicPath(Long userID) {

        String profilePicPath = File.separator +
                           "Uploads" + File.separator +
                           "forums" + File.separator +
                           "users" + File.separator +
                           userID + File.separator +
                           "profilePic" + File.separator +
                           "profilePic.png";

        return profilePicPath;
    }

    public static String getUserRelativeMediaDirectoryPath(Long userID) {

        String profilePicPath = File.separator +
                "Uploads" + File.separator +
                "forums" + File.separator +
                "users" + File.separator +
                userID + File.separator +
                "medias" + File.separator;

        return profilePicPath;
    }

    public static String getUserMediaPostPath(Long userID, Long postID) {
        String path = BASE_DIR_PATH +
                        File.separator +
                        getUserRelativeMediaDirectoryPath(userID) + postID;
        return path;
    }

    public String getActualProfilePicPath(User user) {
        if (user == null) {
            return DEFAULT_PROFILE_PIC_PATH;
        }

        String profilePicPath = getUserProfilePicPath(user.getUserID());
        if (fileExists(profilePicPath)) {
            return profilePicPath.substring(profilePicPath.indexOf("/Uploads"));
        } else {
            return DEFAULT_PROFILE_PIC_PATH;
        }
    }

    // Metodo utilizzato per ottenere la descrizione del tipo di file a partire
    // dalla sua estensione
    public static String getFileDescription(String fileExtension) {
        try {
            TikaConfig tikaConfig = TikaConfig.getDefaultConfig();
            MimeTypes allTypes = tikaConfig.getMimeRepository();

            MimeType mimeType = allTypes.forName(allTypes.getMimeType("file." + fileExtension).getName());

            String fileDescription = mimeType.getDescription();

            if (fileDescription == null || fileDescription.isEmpty()) {
                throw new Exception();
            }

            return fileDescription;
        } catch (Exception e) {
            return "Formato Sconosciuto";
        }
    }

}