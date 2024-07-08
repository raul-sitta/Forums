package com.forums.forums.controller;

import com.forums.forums.model.dao.*;
import com.forums.forums.model.dao.exception.DuplicatedObjectException;
import com.forums.forums.model.mo.*;
import com.forums.forums.services.config.Configuration;
import com.forums.forums.services.logservice.LogService;
import com.forums.forums.services.profilepicpath.ProfilePicPath;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

public class UserManagement {
    private UserManagement(){
    }

    public static void view(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("applicationMessage",applicationMessage);
            request.setAttribute("viewUrl","userManagement/view");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "User Controller Error / view", e);
            try {
                if(daoFactory != null) daoFactory.rollbackTransaction();
                if(sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            }
            catch (Throwable t){}
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(daoFactory != null) daoFactory.closeTransaction();
                if(sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            }
            catch (Throwable t){}
        }
    }
    public static void delete(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        User user;
        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            UserDAO userDAO = daoFactory.getUserDAO();
            user = userDAO.findByUsername(loggedUser.getUsername());

            try {
                sessionUserDAO.delete(loggedUser);
                userDAO.delete(user);

                //Elimino la foto profilo dell'utente
                deleteProfilePicInFileSystem(user);
            }
            catch (Exception e){
                logger.log(Level.SEVERE, "Errore di cancellazione dell'utente!" + e);
            }

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedOn",false);
            request.setAttribute("loggedUser",null);
            request.setAttribute("applicationMessage","Account eliminato correttamente!");
            request.setAttribute("viewUrl","homeManagement/view");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Controller / UserManagement / delete", e);
            try {
                if(daoFactory != null) daoFactory.rollbackTransaction();
                if(sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            }
            catch (Throwable t){}
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(daoFactory != null) daoFactory.closeTransaction();
                if(sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            }
            catch (Throwable t){}
        }
    }
    public static void insert(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            UserDAO userDAO = daoFactory.getUserDAO();

            String username = request.getParameter("username").toLowerCase();
            
            try {
                userDAO.create(
                        username,
                        request.getParameter("password"),
                        request.getParameter("firstname"),
                        request.getParameter("surname"),
                        request.getParameter("email"),
                        Date.valueOf(request.getParameter("birthDate")),
                        request.getParameter("role")
                );

                loggedUser = sessionUserDAO.create(username, null, request.getParameter("firstname"), request.getParameter("surname"), null, null, request.getParameter("role"));

                saveProfilePicInFileSystem(request.getPart("image"),username);
                
                applicationMessage = "Account creato correttamente!";
                
            }catch (DuplicatedObjectException de){
                // Scopro se l'attributo duplicato è l'username o l'email
                String duplicatedAttribute = de.getDuplicatedAttribute();
                if (de.getDuplicatedAttribute() != null) {
                    // Stampa in maiuscolo dell'iniziale rispettivamente di Username o Email seguita dall'attributo
                    applicationMessage = duplicatedAttribute.substring(0, 1).toUpperCase() + duplicatedAttribute.substring(1) + " " + request.getParameter(duplicatedAttribute) + " già in uso!";
                    logger.log(Level.SEVERE, "Errore nella creazione dell'utente:" + request.getParameter(duplicatedAttribute) + de);
                }
                else {
                    applicationMessage = "Utente non creato!";
                    logger.log(Level.SEVERE, "Errore generico nella creazione dell'utente:" + de);
                }
            }catch (Exception e){
                logger.log(Level.SEVERE, "Errore nella creazione dell'utente @" + username + ": " + e);
            }
            
            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("applicationMessage",applicationMessage);
            request.setAttribute("viewUrl","homeManagement/view");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Controller / UserManagement / insert", e);
            try {
                if(daoFactory != null) daoFactory.rollbackTransaction();
                if(sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            }
            catch (Throwable t){}
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(daoFactory != null) daoFactory.closeTransaction();
                if(sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            }
            catch (Throwable t){}
        }
    }
    public static void insertView(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        User loggedUser;

        Logger logger = LogService.getApplicationLogger();
        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("viewUrl","userManagement/insModView");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Controller / UserManagement / insertView", e);
            try {
                if(sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            }
            catch (Throwable t){}
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            }
            catch (Throwable t){}
        }
    }
    public static void modifyView(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        User user;
        String fullPath = null, imagePath = null;

        Logger logger = LogService.getApplicationLogger();
        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            UserDAO userDAO = daoFactory.getUserDAO();
            user = userDAO.findByUsername(loggedUser.getUsername());

            fullPath = ProfilePicPath.profilePicPath(user.getUsername(), false);
            imagePath = fullPath.substring(fullPath.indexOf("/Uploads"));

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("imagePath", imagePath);
            request.setAttribute("loggedOn", loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("user",user);
            request.setAttribute("viewUrl","userManagement/insModView");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Controller / UserManagement / modifyView", e);
            try {
                if(sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            }
            catch (Throwable t){}
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            }
            catch (Throwable t){}
        }
    }
    public static void modify(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request", request);
            sessionFactoryParameters.put("response", response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            UserDAO userDAO = daoFactory.getUserDAO();
            User user = userDAO.findByUsername(loggedUser.getUsername());

            String oldUsername = user.getUsername();
            String username = request.getParameter("username").toLowerCase();
            user.setUsername(username);
            user.setPassword(request.getParameter("password"));
            user.setFirstname(request.getParameter("firstname"));
            user.setSurname(request.getParameter("surname"));
            user.setEmail(request.getParameter("email"));
            user.setBirthDate(Date.valueOf(request.getParameter("birthDate")));
            user.setRole(request.getParameter("role"));
            loggedUser.setUsername(request.getParameter("username"));
            loggedUser.setFirstname(request.getParameter("firstname"));
            loggedUser.setSurname(request.getParameter("surname"));
            loggedUser.setRole(request.getParameter("role"));

            try {
                userDAO.update(user);

                sessionUserDAO.update(loggedUser);

                // Controllo se l'utente ha cambiato username per rinominare il path della directory della foto profilo
                if (!oldUsername.equals(username)) {
                    String oldProfilePicDirectoryPath = ProfilePicPath.profilePicPath(oldUsername, true);
                    String newProfilePicDirectoryPath = ProfilePicPath.profilePicPath(user.getUsername(), true);
                    Path sourcePath = Paths.get(oldProfilePicDirectoryPath);
                    Path targetPath = Paths.get(newProfilePicDirectoryPath);

                    try {
                        // Rinomino la directory
                        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        logger.log(Level.INFO,"Directory rinominata con successo (" + oldProfilePicDirectoryPath + " -> " + newProfilePicDirectoryPath +").");
                    } catch (Exception e) {
                        logger.log(Level.SEVERE,"Errore nella rinomina della cartella (" + oldProfilePicDirectoryPath + " -> " + newProfilePicDirectoryPath +"): " + e);
                    }
                }

                saveProfilePicInFileSystem(request.getPart("image"),username);

            }
            catch (DuplicatedObjectException de){
                // Scopro se l'attributo duplicato è l'username o l'email
                String duplicatedAttribute = de.getDuplicatedAttribute();
                if (de.getDuplicatedAttribute() != null) {
                    // Stampa in maiuscolo dell'iniziale rispettivamente di Username o Email seguita dall'attributo
                    applicationMessage = duplicatedAttribute.substring(0, 1).toUpperCase() + duplicatedAttribute.substring(1) + " " + request.getParameter(duplicatedAttribute) + " già in uso!";
                    logger.log(Level.SEVERE, "Errore nella creazione dell'utente:" + request.getParameter(duplicatedAttribute) + de);
                }
                else {
                    applicationMessage = "Utente non creato!";
                    logger.log(Level.SEVERE, "Errore generico nella creazione dell'utente:" + de);
                }
            }catch (Exception e){
                logger.log(Level.SEVERE, "Errore nella creazione dell'utente @" + request.getParameter("username") + ": " + e);
            }

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedOn", loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("user",user);
            request.setAttribute("applicationMessage",applicationMessage);
            request.setAttribute("viewUrl","userManagement/view");

        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Controller / UserManagement / modify", e);
            try {
                if(daoFactory != null) daoFactory.rollbackTransaction();
                if(sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            }
            catch (Throwable t){}
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(daoFactory != null) daoFactory.closeTransaction();
                if(sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            }
            catch (Throwable t){}
        }
    }
    public static void banView(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        User user;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request", request);
            sessionFactoryParameters.put("response", response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            UserDAO userDAO = daoFactory.getUserDAO();
            user = userDAO.findByUsername(loggedUser.getUsername());

            //Cerco tutti gli utenti eccetto l'utente loggato e gli utenti eliminati
            List<User> users = userDAO.findByParameters(null,null,null,null,false, user);

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedOn", loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("users",users);
            request.setAttribute("applicationMessage",applicationMessage);
            request.setAttribute("viewUrl","userManagement/banView");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Controller / UserManagement / banView", e);
            try {
                if(daoFactory != null) daoFactory.rollbackTransaction();
                if(sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            }
            catch (Throwable t){}
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(daoFactory != null) daoFactory.closeTransaction();
                if(sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            }
            catch (Throwable t){}
        }
    }
    public static void ban(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            UserDAO userDAO = daoFactory.getUserDAO();

            User bannedUser = userDAO.findByUsername(request.getParameter("bannedUser"));

            if(bannedUser.getUserID() != null){
                try {
                    userDAO.delete(bannedUser);

                    //Elimino la foto profilo dell'utente
                    deleteProfilePicInFileSystem(bannedUser);
                }
                catch (Exception e){
                    logger.log(Level.SEVERE, "Errore di cancellazione dell'utente!" + e);
                    throw new RuntimeException(e);
                }

                applicationMessage = "Utente bannato correttamente";
            }
            else {
                applicationMessage = "Username inserito non trovato!";
            }

            //Cerco tutti gli utenti eccetto l'utente loggato e gli utenti eliminati
            List<User> users = userDAO.findByParameters(null,null,null,null,false,loggedUser);

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("users",users);
            request.setAttribute("applicationMessage",applicationMessage);
            request.setAttribute("viewUrl","userManagement/banView");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "User Controller Error / ban", e);
            try {
                if(daoFactory != null) daoFactory.rollbackTransaction();
                if(sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            }
            catch (Throwable t){}
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(daoFactory != null) daoFactory.closeTransaction();
                if(sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            }
            catch (Throwable t){}
        }
    }
    
    private static void saveProfilePicInFileSystem(Part filePart, String username) throws IOException {
        String profilePicDirectoryPath = ProfilePicPath.profilePicPath(username, true);
        File uploadDir = new File(profilePicDirectoryPath);
        
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String profilePicPath = ProfilePicPath.profilePicPath(username, false);
        try (InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, Paths.get(profilePicPath), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteProfilePicInFileSystem(User user) {
        String directoryPath = ProfilePicPath.profilePicPath(user.getUsername(), true);
        File directory = new File(directoryPath);

        if (directory.isDirectory()) {
            String[] entries = directory.list();
            for(String s: entries){
                File currentFile = new File(directory.getPath(),s);
                currentFile.delete();
            }
            directory.delete();
        }
        else {
            System.out.println("Il path " + directoryPath + " non è una directory!");
        }
    }

}
