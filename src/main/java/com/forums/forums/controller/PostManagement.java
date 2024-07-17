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

    public static void view(HttpServletRequest request, HttpServletResponse response){
        DAOFactory sessionDAOFactory = null;
        DAOFactory daoFactory = null;
        User loggedUser;
        TopicSearchFilter topicSearchFilter;
        String applicationMessage = null;
        String topicsCurrentPageIndexStr = request.getParameter("topicsCurrentPageIndex");
        String topicsSearchResultFlagStr = request.getParameter("topicsSearchResultFlag");
        String topicIDStr = request.getParameter("topicID");
        Long topicsCurrentPageIndex = null;
        Boolean topicsSearchResultFlag = null;
        Long topicID = null;
        Topic topic;
        Long postsCurrentPageIndex;
        Long postsPageCount;

        Logger logger = LogService.getApplicationLogger();
        FileSystemService fs = new FileSystemService();

        String postsCurrentPageIndexStr = request.getParameter("postsCurrentPageIndex");
        postsCurrentPageIndex = 1L;

        if (postsCurrentPageIndexStr != null) {
            try {
                postsCurrentPageIndex = Long.parseLong(postsCurrentPageIndexStr);
            } catch (NumberFormatException e) {
                postsCurrentPageIndex = 1L;
            }
        }

        try {
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

            if (topicIDStr != null) {
                topicID = Long.parseLong(topicIDStr);
            }
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Topic Controller Error / view", e);
        }


        if (topicsCurrentPageIndex == null) {
            topicsCurrentPageIndex = 1L;
        }

        if (topicsSearchResultFlag == null) {
            topicsSearchResultFlag = false;
        }

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

            TopicDAO topicDAO = daoFactory.getTopicDAO();

            topic = topicDAO.findByIDWithPosts(postsCurrentPageIndex,topicID);
            postsPageCount = topicDAO.countPostPagesByTopicID(topicID);

            /*
            logger.log(Level.SEVERE, "PostID: {0}", topic.getPosts().get(0).getPostID());
            logger.log(Level.SEVERE, "Content: {0}", topic.getPosts().get(0).getContent());
            logger.log(Level.SEVERE, "CreationTimestamp: {0}", topic.getPosts().get(0).getCreationTimestamp());
            logger.log(Level.SEVERE, "Author UserID: {0}", topic.getPosts().get(0).getAuthor().getUserID());
            logger.log(Level.SEVERE, "TopicID: {0}", topic.getPosts().get(0).getTopic().getTopicID());
            logger.log(Level.SEVERE, "Deleted: {0}", topic.getPosts().get(0).getDeleted());
             */

            daoFactory.commitTransaction();
            sessionDAOFactory.commitTransaction();

            request.setAttribute("topicsCurrentPageIndex",topicsCurrentPageIndex);
            request.setAttribute("topicsSearchResultFlag",topicsSearchResultFlag);
            request.setAttribute("postsCurrentPageIndex", postsCurrentPageIndex);
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
}
