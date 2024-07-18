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
import java.util.Enumeration;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

public class TopicManagement {
    private TopicManagement() {
    }

    public static void view(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        NavigationState navigationState;
        TopicSearchFilter topicSearchFilter;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();
        List<Topic> topics;
        Long topicsCurrentPageIndex;
        Long topicsPageCount;

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            TopicSearchFilterDAO topicSearchFilterDAO = sessionDAOFactory.getTopicSearchFilterDAO();
            topicSearchFilter = topicSearchFilterDAO.findTopicSearchFilter();
            if (topicSearchFilter != null) {
                topicSearchFilterDAO.delete(topicSearchFilter);
            }
            topicSearchFilter = topicSearchFilterDAO.create(null, null,null, null, null, null, true);

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findNavigationState();
            topicsCurrentPageIndex = 1L;
            if (navigationState != null) {
                navigationStateDAO.delete(navigationState);
            }
            navigationState = navigationStateDAO.create(null,topicsCurrentPageIndex,false,null);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            TopicDAO topicDAO = daoFactory.getTopicDAO();

            topics = topicDAO.findByParameters(navigationState.getTopicsCurrentPageIndex(),topicSearchFilter);
            topicsPageCount = topicDAO.countTopicPagesByParameters(topicSearchFilter);
            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState", navigationState);
            request.setAttribute("topics",topics);
            request.setAttribute("topicsPageCount",topicsPageCount);
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("applicationMessage",applicationMessage);
            request.setAttribute("viewUrl","topicManagement/view");

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

    public static void changePageView(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        NavigationState navigationState;
        TopicSearchFilter topicSearchFilter;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();
        List<Topic> topics;
        Long topicsCurrentPageIndex;
        Long topicsPageCount;

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();

            if (request.getParameter("topicsCurrentPageIndex") != null) {
                topicsCurrentPageIndex = Long.parseLong(request.getParameter("topicsCurrentPageIndex"));
                navigationState.setTopicsCurrentPageIndex(topicsCurrentPageIndex);
                navigationStateDAO.update(navigationState);
            }

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            TopicSearchFilterDAO topicSearchFilterDAO = sessionDAOFactory.getTopicSearchFilterDAO();

            loggedUser = sessionUserDAO.findLoggedUser();
            topicSearchFilter = topicSearchFilterDAO.findTopicSearchFilter();
            if (topicSearchFilter == null) {
                topicSearchFilter = topicSearchFilterDAO.create(null, null,null, null, null, null, true);
            }

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            TopicDAO topicDAO = daoFactory.getTopicDAO();

            topics = topicDAO.findByParameters(navigationState.getTopicsCurrentPageIndex(), topicSearchFilter);
            topicsPageCount = topicDAO.countTopicPagesByParameters(topicSearchFilter);
            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState", navigationState);
            request.setAttribute("topics",topics);
            request.setAttribute("topicsPageCount",topicsPageCount);
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("applicationMessage",applicationMessage);
            request.setAttribute("viewUrl","topicManagement/view");

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

    public static void searchView(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        TopicSearchFilter topicSearchFilter;
        List<Category> categories;

        Logger logger = LogService.getApplicationLogger();
        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            TopicSearchFilterDAO topicSearchFilterDAO = sessionDAOFactory.getTopicSearchFilterDAO();
            topicSearchFilter = topicSearchFilterDAO.findTopicSearchFilter();

            if (topicSearchFilter == null) {
                topicSearchFilter = topicSearchFilterDAO.create(null, null,null, null, null, null, true);
            }

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            CategoryDAO categoryDAO = daoFactory.getCategoryDAO();
            categories = categoryDAO.getAll();

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("categories",categories);
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("topicSearchFilter", topicSearchFilter);
            request.setAttribute("viewUrl","topicManagement/searchView");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Controller / TopicManagement / searchView", e);
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

    public static void search(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        TopicSearchFilter topicSearchFilter;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();
        List<Topic> topics;
        Long topicsPageCount;

        // Parametri del filtro
        String title;
        String authorName;
        String categoryName;
        String moreRecentThanStr;
        String olderThanStr;
        Timestamp moreRecentThan;
        Timestamp olderThan;
        String anonymousStr;
        String nonAnonymousStr;
        String sortOrderStr;
        Boolean sortNewestFirst;
        NavigationState navigationState;

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            sessionDAOFactory.beginTransaction();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();
            navigationState.setTopicsCurrentPageIndex(1L);
            navigationState.setTopicsSearchResultFlag(true);
            navigationStateDAO.update(navigationState);

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            TopicSearchFilterDAO topicSearchFilterDAO = sessionDAOFactory.getTopicSearchFilterDAO();

            loggedUser = sessionUserDAO.findLoggedUser();
            topicSearchFilter = topicSearchFilterDAO.findTopicSearchFilter();
            topicSearchFilterDAO.delete(topicSearchFilter);
            topicSearchFilter = new TopicSearchFilter();

            // Assegnazione dei valori dai parametri della richiesta
            title = request.getParameter("title");
            if (title != null && !title.isEmpty()) {
                topicSearchFilter.setTitle(title);
            } else {
                topicSearchFilter.setTitle(null);
            }

            authorName = request.getParameter("authorName");
            if (authorName != null && !authorName.isEmpty()) {
                topicSearchFilter.setAuthorName(authorName);
            } else {
                topicSearchFilter.setAuthorName(null);
            }

            categoryName = request.getParameter("categoryName");
            if (categoryName != null && !categoryName.isEmpty()) {
                topicSearchFilter.setCategoryName(categoryName);
            } else {
                topicSearchFilter.setCategoryName(null);
            }

            moreRecentThanStr = request.getParameter("moreRecentThan");
            if (moreRecentThanStr != null && !moreRecentThanStr.isEmpty()) {
                try {
                    moreRecentThan = Timestamp.valueOf(moreRecentThanStr.replace("T", " ") + ":00");
                    topicSearchFilter.setMoreRecentThan(moreRecentThan);
                } catch (IllegalArgumentException e) {
                    topicSearchFilter.setMoreRecentThan(null);
                }
            } else {
                topicSearchFilter.setMoreRecentThan(null);
            }

            olderThanStr = request.getParameter("olderThan");
            if (olderThanStr != null && !olderThanStr.isEmpty()) {
                try {
                    olderThan = Timestamp.valueOf(olderThanStr.replace("T", " ") + ":00");
                    topicSearchFilter.setOlderThan(olderThan);
                } catch (IllegalArgumentException e) {
                    topicSearchFilter.setOlderThan(null);
                }
            } else {
                topicSearchFilter.setOlderThan(null);
            }

            anonymousStr = request.getParameter("showAnonymous");
            nonAnonymousStr = request.getParameter("showNonAnonymous");
            if (anonymousStr != null && nonAnonymousStr != null) {
                topicSearchFilter.setAnonymous(null);
            } else if (anonymousStr != null) {
                topicSearchFilter.setAnonymous(Boolean.TRUE);
            } else if (nonAnonymousStr != null) {
                topicSearchFilter.setAnonymous(Boolean.FALSE);
            } else {
                topicSearchFilter.setAnonymous(null);
            }

            sortOrderStr = request.getParameter("sortOrder");
            if (sortOrderStr != null) {
                sortNewestFirst = Boolean.parseBoolean(sortOrderStr);
            } else {
                sortNewestFirst = Boolean.TRUE;
            }

            topicSearchFilter.setSortNewestFirst(sortNewestFirst);

            topicSearchFilterDAO.create(topicSearchFilter.getTitle(),
                                        topicSearchFilter.getAuthorName(),
                                        topicSearchFilter.getCategoryName(),
                                        topicSearchFilter.getMoreRecentThan(),
                                        topicSearchFilter.getOlderThan(),
                                        topicSearchFilter.getAnonymous(),
                                        topicSearchFilter.getSortNewestFirst());

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            TopicDAO topicDAO = daoFactory.getTopicDAO();

            topics = topicDAO.findByParameters(1L,topicSearchFilter);
            topicsPageCount = topicDAO.countTopicPagesByParameters(topicSearchFilter);
            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState", navigationState);
            request.setAttribute("topics",topics);
            request.setAttribute("topicsPageCount",topicsPageCount);
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("applicationMessage",applicationMessage);
            request.setAttribute("viewUrl","topicManagement/view");

        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Topic Controller Error / search", e);
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
        List<Category> categories;

        Logger logger = LogService.getApplicationLogger();
        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            CategoryDAO categoryDAO = daoFactory.getCategoryDAO();
            categories = categoryDAO.getAll();

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("categories", categories);
            request.setAttribute("action", "insert");
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("viewUrl","topicManagement/insModView");
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
        Topic topic = null;
        Category category;
        String isAnonymousStr;
        Boolean isAnonymous = false;
        String categoryName;
        String applicationMessage = null;
        NavigationState navigationState;
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
            navigationState.setPostsCurrentPageIndex(1L);
            navigationStateDAO.update(navigationState);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            CategoryDAO categoryDAO = daoFactory.getCategoryDAO();
            categoryName = request.getParameter("categoryName");
            category = categoryDAO.findByName(categoryName);

            isAnonymousStr = request.getParameter("isAnonymous");
            if (isAnonymousStr != null) {
                isAnonymous = Boolean.valueOf(isAnonymousStr);
            }

            TopicDAO topicDAO = daoFactory.getTopicDAO();

            try {
                topic = topicDAO.create(
                        request.getParameter("title"),
                        Timestamp.valueOf(request.getParameter("creationTimestamp")),
                        loggedUser,
                        category,
                        isAnonymous
                );

            }catch (Exception e){
                logger.log(Level.SEVERE, "Errore nella creazione del topic: " + e);
            }

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("navigationState", navigationState);
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
        Topic topic;
        Long topicID;
        List<Category> categories;
        NavigationState navigationState;

        Logger logger = LogService.getApplicationLogger();
        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            NavigationStateDAO navigationStateDAO = sessionDAOFactory.getNavigationStateDAO();
            navigationState = navigationStateDAO.findOrCreateNavigationState();

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            loggedUser = sessionUserDAO.findLoggedUser();

            TopicDAO topicDAO = daoFactory.getTopicDAO();
            topic = topicDAO.findByID(navigationState.getTopicID());

            CategoryDAO categoryDAO = daoFactory.getCategoryDAO();
            categories = categoryDAO.getAll();

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("topic", topic);
            request.setAttribute("categories", categories);
            request.setAttribute("action", "modify");
            request.setAttribute("loggedOn",loggedUser!=null);
            request.setAttribute("loggedUser",loggedUser);
            request.setAttribute("viewUrl","topicManagement/insModView");
        }
        catch (Exception e){
            logger.log(Level.SEVERE, "Controller / UserManagement / insertView", e);
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
        Topic topic = null;
        String title;
        Category category;
        String categoryName;
        String applicationMessage = null;
        Long postsPageCount;
        NavigationState navigationState;
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

            Long topicID = Long.parseLong(request.getParameter("topicID"));

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            TopicDAO topicDAO = daoFactory.getTopicDAO();
            topic = topicDAO.findByIDWithPosts(navigationState.getPostsCurrentPageIndex(), topicID);
            postsPageCount = topicDAO.countPostPagesByTopicID(topicID);

            title = request.getParameter("title");

            CategoryDAO categoryDAO = daoFactory.getCategoryDAO();
            categoryName = request.getParameter("categoryName");
            category = categoryDAO.findByName(categoryName);

            topic.setTitle(title);
            topic.setCategory(category);

            try {
                topicDAO.update(topic);
            }catch (Exception e){
                logger.log(Level.SEVERE, "Errore nella modifica del topic: " + e);
            }

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("topic", topic);
            request.setAttribute("navigationState", navigationState);
            request.setAttribute("postsPageCount", postsPageCount);
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
}