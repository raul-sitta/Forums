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
        TopicSearchFilter topicSearchFilter;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();
        List<Topic> topics;
        Long currentPageIndex = 1L;
        Long pageCount;

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            UserDAO sessionUserDAO = sessionDAOFactory.getUserDAO();
            TopicSearchFilterDAO topicSearchFilterDAO = sessionDAOFactory.getTopicSearchFilterDAO();

            loggedUser = sessionUserDAO.findLoggedUser();
            topicSearchFilter = topicSearchFilterDAO.findTopicSearchFilter();
            if (topicSearchFilter != null) {
                topicSearchFilterDAO.delete(topicSearchFilter);
            }
            topicSearchFilter = topicSearchFilterDAO.create(null, null,null, null, null, null, true);

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            TopicDAO topicDAO = daoFactory.getTopicDAO();

            topics = topicDAO.findByParameters(currentPageIndex,topicSearchFilter);
            pageCount = topicDAO.countTopicPagesByParameters(topicSearchFilter);
            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("currentPageIndex", currentPageIndex);
            request.setAttribute("topics",topics);
            request.setAttribute("pageCount",pageCount);
            request.setAttribute("searchResultFlag",false);
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
        TopicSearchFilter topicSearchFilter;
        String applicationMessage = null;
        Logger logger = LogService.getApplicationLogger();
        List<Topic> topics;
        Long pageCount;
        String searchResultFlagStr;
        Boolean searchResultFlag;
        String currentPageIndexStr;
        Long currentPageIndex = 1L;

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);
            sessionDAOFactory.beginTransaction();

            currentPageIndexStr = request.getParameter("currentPageIndex");
            if (currentPageIndexStr != null) {
                try {
                    currentPageIndex = Long.parseLong(currentPageIndexStr);
                } catch (NumberFormatException e) {
                    currentPageIndex = 1L;
                }
            }

            searchResultFlagStr = request.getParameter("searchResultFlag");
            if (searchResultFlagStr == null || searchResultFlagStr.isEmpty()) {
                searchResultFlag = Boolean.FALSE;
            } else {
                searchResultFlag = Boolean.valueOf(searchResultFlagStr);
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

            topics = topicDAO.findByParameters(currentPageIndex,topicSearchFilter);
            pageCount = topicDAO.countTopicPagesByParameters(topicSearchFilter);
            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("currentPageIndex", currentPageIndex);
            request.setAttribute("topics",topics);
            request.setAttribute("pageCount",pageCount);
            request.setAttribute("searchResultFlag",searchResultFlag);
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
        Long pageCount;

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

        String currentPageIndexStr;
        Long currentPageIndex = 1L;

        try {
            Map sessionFactoryParameters = new HashMap<String, Object>();
            sessionFactoryParameters.put("request",request);
            sessionFactoryParameters.put("response",response);
            sessionDAOFactory = DAOFactory.getDAOFactory(Configuration.COOKIE_IMPL, sessionFactoryParameters);

            currentPageIndexStr = request.getParameter("currentPageIndex");

            if (currentPageIndexStr != null) {
                try {
                    currentPageIndex = Long.parseLong(currentPageIndexStr);
                } catch (NumberFormatException e) {
                    currentPageIndex = 1L;
                }
            }

            sessionDAOFactory.beginTransaction();

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

            topics = topicDAO.findByParameters(currentPageIndex,topicSearchFilter);
            pageCount = topicDAO.countTopicPagesByParameters(topicSearchFilter);
            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("currentPageIndex", currentPageIndex);
            request.setAttribute("topics",topics);
            request.setAttribute("pageCount",pageCount);
            request.setAttribute("searchResultFlag",true);
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

            request.setAttribute("topic", topic);
            request.setAttribute("profilePicPaths", null);
            request.setAttribute("currentPageIndex", 1L);
            request.setAttribute("pageCount", 1L);
            request.setAttribute("topicsCurrentPageIndex", null);
            request.setAttribute("topicsSearchResultFlag", null);
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
        String currentPageIndexStr;
        Long currentPageIndex = 1L;
        String topicsCurrentPageIndexStr;
        String topicsSearchResultFlagStr;
        Long topicsCurrentPageIndex = null;
        Boolean topicsSearchResultFlag = null;
        List<Category> categories;

        Logger logger = LogService.getApplicationLogger();
        FileSystemService fs = new FileSystemService();
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

            Enumeration<String> parameterNames = request.getParameterNames();

            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);

                for (String paramValue : paramValues) {
                    logger.log(Level.INFO,paramName + ": " + paramValue);
                }
            }

            topicsCurrentPageIndexStr = request.getParameter("topicsCurrentPageIndex");
            topicsSearchResultFlagStr = request.getParameter("topicsSearchResultFlag");

            if (topicsCurrentPageIndexStr != null) {
                topicsCurrentPageIndex = Long.parseLong(topicsCurrentPageIndexStr);
            } else {
                topicsCurrentPageIndex = 1L;
            }

            if (topicsSearchResultFlagStr != null) {
                topicsSearchResultFlag = Boolean.parseBoolean(topicsSearchResultFlagStr);
            } else {
                topicsSearchResultFlag = false;
            }

            currentPageIndexStr = request.getParameter("currentPageIndex");
            currentPageIndex = Long.parseLong(currentPageIndexStr);

            topicID = Long.parseLong(request.getParameter("topicID"));
            TopicDAO topicDAO = daoFactory.getTopicDAO();
            topic = topicDAO.findByID(topicID);

            CategoryDAO categoryDAO = daoFactory.getCategoryDAO();
            categories = categoryDAO.getAll();

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("currentPageIndex", currentPageIndex);
            request.setAttribute("topicsCurrentPageIndex", topicsCurrentPageIndex);
            request.setAttribute("topicsSearchResultFlag", topicsSearchResultFlag);
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
        Topic topic = null;
        String title;
        Category category;
        String categoryName;
        String applicationMessage = null;
        String currentPageIndexStr;
        Long currentPageIndex = 1L;
        Long pageCount;
        String topicsCurrentPageIndexStr;
        String topicsSearchResultFlagStr;
        Long topicsCurrentPageIndex = null;
        Boolean topicsSearchResultFlag = null;
        List<String> profilePicPaths = new ArrayList<>();
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

            topicsCurrentPageIndexStr = request.getParameter("topicsCurrentPageIndex");
            topicsSearchResultFlagStr = request.getParameter("topicsSearchResultFlag");

            if (topicsCurrentPageIndexStr != null) {
                topicsCurrentPageIndex = Long.parseLong(topicsCurrentPageIndexStr);
            } else {
                topicsCurrentPageIndex = 1L;
            }

            if (topicsSearchResultFlagStr != null) {
                topicsSearchResultFlag = Boolean.parseBoolean(topicsSearchResultFlagStr);
            } else {
                topicsSearchResultFlag = false;
            }

            currentPageIndexStr = request.getParameter("currentPageIndex");
            currentPageIndex = Long.parseLong(currentPageIndexStr);

            Long topicID = Long.parseLong(request.getParameter("topicID"));

            if (currentPageIndexStr != null) {
                try {
                    currentPageIndex = Long.parseLong(currentPageIndexStr);
                } catch (NumberFormatException e) {
                    currentPageIndex = 1L;
                }
            }

            daoFactory = DAOFactory.getDAOFactory(Configuration.DAO_IMPL, null);
            daoFactory.beginTransaction();

            TopicDAO topicDAO = daoFactory.getTopicDAO();
            topic = topicDAO.findByIDWithPosts(currentPageIndex,topicID);
            pageCount = topicDAO.countPostPagesByTopicID(topicID);

            if (topic.getPosts() != null) {
                for (int i = 0; i < topic.getPosts().size(); i++) {
                    profilePicPaths.add(fs.getActualProfilePicPath(topic.getPosts().get(i).getAuthor()));
                }
            }

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
            request.setAttribute("profilePicPaths", profilePicPaths);
            request.setAttribute("currentPageIndex", currentPageIndex);
            request.setAttribute("pageCount", pageCount);
            request.setAttribute("topicsCurrentPageIndex", topicsCurrentPageIndex);
            request.setAttribute("topicsSearchResultFlag", topicsSearchResultFlag);
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