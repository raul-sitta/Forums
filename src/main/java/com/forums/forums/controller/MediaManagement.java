package com.forums.forums.controller;

import com.forums.forums.model.dao.*;
import com.forums.forums.model.dao.exception.DuplicatedObjectException;
import com.forums.forums.model.mo.*;
import com.forums.forums.services.config.Configuration;
import com.forums.forums.services.logservice.LogService;
import com.forums.forums.services.filesystemservice.FileSystemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MediaManagement {
    private MediaManagement() {
    }

    public static void view(HttpServletRequest request, HttpServletResponse response) {
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        Long postID;
        Post post;
        String applicationMessage = null;
        NavigationState navigationState;

        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request", request);
            sessionFactoryParameters.put("response", response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            sessionDAOFactory.beginTransaction();

            postID = Long.parseLong(request.getParameter("postID"));

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();

            loggedUser = sessionUserDAO.findLoggedUser();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            PostDAO postDAO = daoFactory.getPostDAO();

            post = postDAO.findByIDWithMedias(postID);

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState", navigationState);
            request.setAttribute("post", post);
            request.setAttribute("loggedOn", loggedUser != null);
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("applicationMessage", applicationMessage);
            request.setAttribute("viewUrl", "mediaManagement/view");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Media Controller Error / view", e);
            try {
                if (daoFactory != null) daoFactory.rollbackTransaction();
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            } catch (Throwable t) {
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (daoFactory != null) daoFactory.closeTransaction();
                if (sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            } catch (Throwable t) {
            }
        }
    }

    public static void imageView(HttpServletRequest request, HttpServletResponse response) {
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        Long postID;
        Long mediaID;
        Media media;
        String applicationMessage = null;
        NavigationState navigationState;

        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request", request);
            sessionFactoryParameters.put("response", response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            sessionDAOFactory.beginTransaction();

            postID = Long.parseLong(request.getParameter("postID"));

            mediaID = Long.parseLong(request.getParameter("mediaID"));

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();

            loggedUser = sessionUserDAO.findLoggedUser();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            MediaDAO mediaDAO = daoFactory.getMediaDAO();

            media = mediaDAO.findByMediaID(mediaID);

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState", navigationState);
            request.setAttribute("media", media);
            request.setAttribute("postID", postID);
            request.setAttribute("loggedOn", loggedUser != null);
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("applicationMessage", applicationMessage);
            request.setAttribute("viewUrl", "mediaManagement/imageView");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Media Controller Error / view", e);
            try {
                if (daoFactory != null) daoFactory.rollbackTransaction();
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            } catch (Throwable t) {
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (daoFactory != null) daoFactory.closeTransaction();
                if (sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            } catch (Throwable t) {
            }
        }
    }

    public static void insertView(HttpServletRequest request, HttpServletResponse response) {
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        Long postID;
        Post post;
        String applicationMessage = null;
        NavigationState navigationState;

        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request", request);
            sessionFactoryParameters.put("response", response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            sessionDAOFactory.beginTransaction();

            postID = Long.parseLong(request.getParameter("postID"));

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();

            loggedUser = sessionUserDAO.findLoggedUser();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            PostDAO postDAO = daoFactory.getPostDAO();

            post = postDAO.findByID(postID);

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState", navigationState);
            request.setAttribute("post", post);
            request.setAttribute("loggedOn", loggedUser != null);
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("applicationMessage", applicationMessage);
            request.setAttribute("viewUrl", "mediaManagement/insView");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Media Controller Error / view", e);
            try {
                if (daoFactory != null) daoFactory.rollbackTransaction();
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            } catch (Throwable t) {
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (daoFactory != null) daoFactory.closeTransaction();
                if (sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            } catch (Throwable t) {
            }
        }
    }

    public static void insert(HttpServletRequest request, HttpServletResponse response) {
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        Long postID;
        Post post;
        Long uploaderID;
        Timestamp creationTimestamp;
        List<Part> fileParts;
        List<Media> oldMedias, newMedias;
        String applicationMessage = null;
        NavigationState navigationState;

        Logger logger = LogService.getApplicationLogger();
        FileSystemService fs = new FileSystemService();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request", request);
            sessionFactoryParameters.put("response", response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            sessionDAOFactory.beginTransaction();

            postID = Long.parseLong(request.getParameter("postID"));

            uploaderID = Long.parseLong(request.getParameter("uploaderID"));

            creationTimestamp = Timestamp.valueOf(request.getParameter("creationTimestamp"));

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();

            loggedUser = sessionUserDAO.findLoggedUser();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            MediaDAO mediaDAO = daoFactory.getMediaDAO();

            PostDAO postDAO = daoFactory.getPostDAO();

            post = postDAO.findByIDWithMedias(postID);

            if (post.getMedias() == null) post.setMedias(new ArrayList<>());

            oldMedias = post.getMedias();

            // Recupero i file dei media dalla request

            fileParts = new ArrayList<>();
            Map<String, Integer> fileNameMap = new HashMap<>();

            // Inizializza la mappa con i nomi dei file già presenti
            for (Media media : oldMedias) {
                String existingFileName = media.getPath().substring(media.getPath().lastIndexOf("/") + 1);
                fileNameMap.put(existingFileName, fileNameMap.getOrDefault(existingFileName, 0) + 1);
            }

            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                if (part.getName().equals("files[]") && part.getSize() > 0) {
                    String fileName = getSubmittedFileName(part);

                    // Rinomina il file se esiste già un file con lo stesso nome
                    while (fileNameMap.containsKey(fileName)) {
                        int count = fileNameMap.get(fileName);
                        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                        String extension = fileName.substring(fileName.lastIndexOf('.'));
                        fileName = baseName + "_" + count + extension;
                    }

                    // Aggiorna la mappa e la lista con il nuovo nome del file
                    fileNameMap.put(fileName, 1);
                    fileParts.add(part);
                }
            }

            newMedias = new ArrayList<>();
            User uploader = new User();
            uploader.setUserID(uploaderID);

            try {

                fs.createDirectory(FileSystemService.getUserMediaPostPath(uploaderID,postID));

                for (Part part : fileParts) {
                    String path = FileSystemService.getUserRelativeMediaDirectoryPath(uploaderID) + postID + File.separator + getSubmittedFileName(part);
                    Media media = mediaDAO.create(  path,
                                                    creationTimestamp,
                                                    uploader,
                                                    post);
                    newMedias.add(media);
                    fs.createFile(part, FileSystemService.BASE_DIR_PATH + File.separator + path);
                }

            }catch (Exception e){
                logger.log(Level.SEVERE, "Errore nella creazione dei media: " + e);
            }

            post.getMedias().addAll(0, newMedias);

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState", navigationState);
            request.setAttribute("post", post);
            request.setAttribute("loggedOn", loggedUser != null);
            request.setAttribute("loggedUser", loggedUser);
            request.setAttribute("applicationMessage", applicationMessage);
            request.setAttribute("viewUrl", "mediaManagement/view");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Media Controller Error / insert", e);
            try {
                if (daoFactory != null) daoFactory.rollbackTransaction();
                if (sessionDAOFactory != null) sessionDAOFactory.rollbackTransaction();
            } catch (Throwable t) {
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (daoFactory != null) daoFactory.closeTransaction();
                if (sessionDAOFactory != null) sessionDAOFactory.closeTransaction();
            } catch (Throwable t) {
            }
        }
    }

    private static String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}