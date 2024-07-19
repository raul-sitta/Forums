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

import java.sql.*;
import java.util.ArrayList;
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
        FileSystemService fs = new FileSystemService();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

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

            String username = request.getParameter("username");
            String firstname = request.getParameter("firstname");
            String surname = request.getParameter("surname");
            
            try {
                Boolean updateImage = Boolean.parseBoolean(request.getParameter("updateImage"));

                user = userDAO.create(
                        null,
                        username,
                        request.getParameter("password"),
                        firstname,
                        surname,
                        request.getParameter("email"),
                        Date.valueOf(request.getParameter("birthDate")),
                        Timestamp.valueOf(request.getParameter("registrationTimestamp")),
                        request.getParameter("role"),
                        updateImage
                );

                loggedUser = sessionUserDAO.create(user.getUserID(),username, null, firstname, surname, null, null,null, request.getParameter("role"), updateImage);

                //Creo le directory dell'utente e salvo la foto profilo
                fs.createDirectory(fs.getUserMediaDirectoryPath(user.getUserID()));

                // Creo la foto profilo (nel caso in cui sia da creare)
                if (updateImage) {
                    fs.createDirectory(fs.getUserProfilePicDirectoryPath(user.getUserID()));
                    fs.createFile(request.getPart("image"), fs.getUserProfilePicPath(user.getUserID()));
                }
                
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

            sessionDAOFactory.commitTransaction();

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

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

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

            String username = request.getParameter("username");
            String firstname = request.getParameter("firstname");
            String surname = request.getParameter("surname");

            user.setUsername(username);
            user.setPassword(request.getParameter("password"));
            user.setFirstname(firstname);
            user.setSurname(surname);
            user.setEmail(request.getParameter("email"));
            user.setBirthDate(Date.valueOf(request.getParameter("birthDate")));
            user.setRole(request.getParameter("role"));
            loggedUser.setUsername(username);
            loggedUser.setFirstname(firstname);
            loggedUser.setSurname(surname);
            loggedUser.setRole(request.getParameter("role"));

            try {
                Boolean updateImage = Boolean.parseBoolean(request.getParameter("updateImage"));
                Boolean deleteImage = Boolean.parseBoolean(request.getParameter("deleteImage"));

                if (deleteImage) {
                    user.setProfilePicPath(FileSystemService.DEFAULT_PROFILE_PIC_PATH);
                    loggedUser.setProfilePicPath(FileSystemService.DEFAULT_PROFILE_PIC_PATH);
                }

                if (updateImage) {
                    user.setProfilePicPath(FileSystemService.getUserRelativeProfilePicPath(user.getUserID()));
                    loggedUser.setProfilePicPath(FileSystemService.getUserRelativeProfilePicPath(user.getUserID()));
                }

                userDAO.update(user);
                sessionUserDAO.update(loggedUser);

                if (updateImage) {
                    fs.createDirectory(fs.getUserProfilePicDirectoryPath(user.getUserID()));
                    fs.createFile(request.getPart("image"), fs.getUserProfilePicPath(user.getUserID()));
                } else if (deleteImage) {
                    fs.deleteDirectory(fs.getUserProfilePicDirectoryPath(user.getUserID()));
                }
            }
            catch (DuplicatedObjectException de){
                // Scopro se l'attributo duplicato è l'username o l'email
                String duplicatedAttribute = de.getDuplicatedAttribute();
                if (de.getDuplicatedAttribute() != null) {
                    // Stampa in maiuscolo dell'iniziale rispettivamente di Username o Email seguita dall'attributo
                    applicationMessage = duplicatedAttribute.substring(0, 1).toUpperCase() + duplicatedAttribute.substring(1) + " " + request.getParameter(duplicatedAttribute) + " già in uso!";
                    logger.log(Level.SEVERE, "Errore nella modifica dell'utente:" + request.getParameter(duplicatedAttribute) + de);
                }
                else {
                    applicationMessage = "Utente non creato!";
                    logger.log(Level.SEVERE, "Errore generico nella modifica dell'utente:" + de);
                }
            }catch (Exception e){
                logger.log(Level.SEVERE, "Errore nella modifica dell'utente @" + request.getParameter("username") + ": " + e);
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

    public static void ban(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        User toBanUser;
        Long userID;
        List<Long> userStats = new ArrayList<>();
        NavigationState navigationState;
        Logger logger = LogService.getApplicationLogger();
        FileSystemService fs = new FileSystemService();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            userID = Long.parseLong(request.getParameter("userID"));

            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            UserDAO userDAO = daoFactory.getUserDAO();
            toBanUser = userDAO.findByUserIDWithStats(userID,userStats);

            try {
                userDAO.delete(toBanUser);

                //Elimino la directory della foto profilo dell'utente (insieme al suo contenuto)
                fs.deleteDirectory(fs.getUserProfilePicDirectoryPath(userID));

            }
            catch (Exception e){
                logger.log(Level.SEVERE, "Errore di cancellazione dell'utente!" + e);
            }

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("loggedOn",loggedUser != null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("user",toBanUser);
            request.setAttribute("userStats",userStats);
            request.setAttribute("navigationState",navigationState);
            request.setAttribute("applicationMessage","Utente @" + toBanUser.getUsername() + " bannato!");
            request.setAttribute("viewUrl","userManagement/profileView");
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

    public static void profileView(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        User user;
        Long userID;
        List<Long> userStats = new ArrayList<>();
        NavigationState navigationState;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            userID = Long.parseLong(request.getParameter("userID"));

            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            UserDAO userDAO = daoFactory.getUserDAO();
            user = userDAO.findByUserIDWithStats(userID,userStats);

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState", navigationState);
            request.setAttribute("user",user);
            request.setAttribute("userStats",userStats);
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("applicationMessage",applicationMessage);
            request.setAttribute("viewUrl","userManagement/profileView");

        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Topic Controller Error / view", e);
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
