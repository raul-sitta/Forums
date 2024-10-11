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
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

public class PostManagement {
    private PostManagement() {
    }

    // A differenza di quanto fatto per i topic, per le pagine dei post è stato implementato un
    // unico metodo di visualizzazione "view", chiamato per visualizzare sia la prima pagina dei post che
    // le successive o le precedenti a seconda della pagina in cui si vuole navigare.
    public static void view(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        String applicationMessage = null;
        Topic topic;
        NavigationState navigationState;
        Long postsPageCount;
        Long postsCurrentPageIndex;

        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();

            loggedUser = sessionUserDAO.findLoggedUser();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();
            if (request.getParameter("topicID") != null) {
                navigationState.setTopicID(Long.parseLong(request.getParameter("topicID")));
            }
            if (request.getParameter("postsCurrentPageIndex") != null) {
                postsCurrentPageIndex = Long.parseLong(request.getParameter("postsCurrentPageIndex"));
                navigationState.setPostsCurrentPageIndex(postsCurrentPageIndex);
            }
            navigationStateDAO.update(navigationState);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            TopicDAO topicDAO = daoFactory.getTopicDAO();

            topic = topicDAO.findByIDWithPosts(navigationState.getPostsCurrentPageIndex(), navigationState.getTopicID());
            postsPageCount = topicDAO.countPostPagesByTopicID(navigationState.getTopicID());

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState",navigationState);
            request.setAttribute("postsPageCount",postsPageCount);
            request.setAttribute("topic",topic);
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("applicationMessage",applicationMessage);
            request.setAttribute("viewUrl","postManagement/view");

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

    public static void insertView(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
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

            request.setAttribute("action", "insert");
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("viewUrl","postManagement/insModView");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Controller / UserManagement / modifyView", e);
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
        Topic topic;
        Long postsPageCount;
        NavigationState navigationState;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            sessionDAOFactory.beginTransaction();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            PostDAO postDAO = daoFactory.getPostDAO();

            TopicDAO topicDAO = daoFactory.getTopicDAO();

            try {
                Topic topicIDContainer = new Topic();
                topicIDContainer.setTopicID(navigationState.getTopicID());
                postDAO.create(
                        request.getParameter("content"),
                        Timestamp.valueOf(request.getParameter("creationTimestamp")),
                        loggedUser,
                        topicIDContainer
                );

            }catch (Exception e){
                logger.log(Level.SEVERE, "Errore nella creazione del post: " + e);
            }

            postsPageCount = topicDAO.countPostPagesByTopicID(navigationState.getTopicID());
            navigationState.setPostsCurrentPageIndex(postsPageCount);
            navigationStateDAO.update(navigationState);
            topic = topicDAO.findByIDWithPosts(navigationState.getPostsCurrentPageIndex(), navigationState.getTopicID());

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState", navigationState);
            request.setAttribute("postsPageCount", postsPageCount);
            request.setAttribute("topic", topic);
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("applicationMessage",applicationMessage);
            request.setAttribute("viewUrl","postManagement/view");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Controller / TopicManagement / insert", e);
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

    public static void modifyView(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        Long postID;
        Post post;

        Logger logger = LogService.getApplicationLogger();
        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            sessionDAOFactory.beginTransaction();

            postID = Long.parseLong(request.getParameter("postID"));

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            PostDAO postDAO = daoFactory.getPostDAO();
            post = postDAO.findByID(postID);

            daoFactory.commitTransaction();

            sessionDAOFactory.commitTransaction();

            request.setAttribute("post",post);
            request.setAttribute("action", "modify");
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("viewUrl","postManagement/insModView");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Controller / UserManagement / modifyView", e);
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

    public static void modify(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        Topic topic;
        Post post;
        Long postID;
        Long postsPageCount;
        NavigationState navigationState;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            postID = Long.parseLong(request.getParameter("postID"));

            sessionDAOFactory.beginTransaction();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            PostDAO postDAO = daoFactory.getPostDAO();
            post = postDAO.findByID(postID);

            post.setContent(request.getParameter("content"));

            try {
                postDAO.update(post);
            }catch (Exception e){
                logger.log(Level.SEVERE, "Errore nella modifica del post: " + e);
            }

            TopicDAO topicDAO = daoFactory.getTopicDAO();

            postsPageCount = topicDAO.countPostPagesByTopicID(navigationState.getTopicID());
            topic = topicDAO.findByIDWithPosts(navigationState.getPostsCurrentPageIndex(), navigationState.getTopicID());

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState", navigationState);
            request.setAttribute("postsPageCount", postsPageCount);
            request.setAttribute("topic", topic);
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("applicationMessage",applicationMessage);
            request.setAttribute("viewUrl","postManagement/view");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Controller / TopicManagement / insert", e);
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
        String applicationMessage = null;
        Topic topic;
        Post post;
        Long postID;
        NavigationState navigationState;
        Long postsPageCount;
        Long postsCurrentPageIndex;

        Logger logger = LogService.getApplicationLogger();

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            postID = Long.parseLong(request.getParameter("postID"));

            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();

            loggedUser = sessionUserDAO.findLoggedUser();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            PostDAO postDAO = daoFactory.getPostDAO();

            post = postDAO.findByID(postID);

            try {
                postDAO.delete(post);
            }
            catch (Exception e){
                logger.log(Level.SEVERE, "Errore di cancellazione del post " + postID + ": " + e);
            }

            TopicDAO topicDAO = daoFactory.getTopicDAO();

            postsPageCount = topicDAO.countPostPagesByTopicID(navigationState.getTopicID());

            // Fix nel caso si stia eliminando l'ultimo post dell'ultima pagina
            if (navigationState.getPostsCurrentPageIndex() > postsPageCount) {
                navigationState.setPostsCurrentPageIndex(postsPageCount);
                navigationStateDAO.update(navigationState);
            }

            topic = topicDAO.findByIDWithPosts(navigationState.getPostsCurrentPageIndex(), navigationState.getTopicID());

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState",navigationState);
            request.setAttribute("postsPageCount",postsPageCount);
            request.setAttribute("topic",topic);
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("applicationMessage","Post eliminato correttamente!");
            request.setAttribute("viewUrl","postManagement/view");

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
