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
        FileSystemService fs = new FileSystemService();

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

                //Elimino la directory della foto profilo dell'utente (insieme al suo contenuto)
                fs.deleteDirectory(fs.getUserProfilePicDirectoryPath(user.getUserID()));

                //Conservo i media dell'utente eliminato
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
        User user;
        User loggedUser;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();
        FileSystemService fs = new FileSystemService();

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
                user = userDAO.create(
                        null,
                        username,
                        request.getParameter("password"),
                        request.getParameter("firstname"),
                        request.getParameter("surname"),
                        request.getParameter("email"),
                        Date.valueOf(request.getParameter("birthDate")),
                        request.getParameter("role")
                );

                loggedUser = sessionUserDAO.create(user.getUserID(),username, null, request.getParameter("firstname"), request.getParameter("surname"), null, null, request.getParameter("role"));

                //Creo le directory dell'utente e salvo la foto profilo
                fs.createDirectory(fs.getUserProfilePicDirectoryPath(user.getUserID()));
                fs.createDirectory(fs.getUserMediaDirectoryPath(user.getUserID()));
                Part filePart = request.getPart("image");
                if (filePart != null) fs.createFile(filePart, fs.getUserProfilePicPath(user.getUserID()));
                
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
        String fullProfilePicPath = null, profilePicPath = null;

        Logger logger = LogService.getApplicationLogger();
        FileSystemService fs = new FileSystemService();
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

            fullProfilePicPath = fs.getUserProfilePicPath(user.getUserID());
            profilePicPath = fullProfilePicPath.substring(fullProfilePicPath.indexOf("/Uploads"));

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("profilePicPath", profilePicPath);
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
        FileSystemService fs = new FileSystemService();

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

                Part filePart = request.getPart("image");
                if (filePart != null) fs.createFile(filePart, fs.getUserProfilePicPath(user.getUserID()));

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
        FileSystemService fs = new FileSystemService();

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

                    //Elimino la directory della foto profilo dell'utente (insieme al suo contenuto)
                    fs.deleteDirectory(fs.getUserProfilePicDirectoryPath(bannedUser.getUserID()));
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
}
