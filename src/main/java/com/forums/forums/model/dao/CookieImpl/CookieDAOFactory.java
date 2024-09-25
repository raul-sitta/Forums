package com.forums.forums.model.dao.CookieImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

import com.forums.forums.model.dao.*;

public class CookieDAOFactory extends DAOFactory {

    private Map factoryParameters;

    private HttpServletRequest request;
    private HttpServletResponse response;

    public CookieDAOFactory(Map factoryParameters) {
        this.factoryParameters=factoryParameters;
    }

    @Override
    public void beginTransaction() {

        try {
            this.request=(HttpServletRequest) factoryParameters.get("request");
            this.response=(HttpServletResponse) factoryParameters.get("response");;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void commitTransaction() {}

    @Override
    public void rollbackTransaction() {}

    @Override
    public void closeTransaction() {}

    @Override
    public CategoryDAO getCategoryDAO() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FAQDAO getFAQDAO() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MediaDAO getMediaDAO() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NavigationStateDAO getNavigationStateDAO() {
        return new NavigationStateDAOCookieImpl(request, response);
    }

    @Override
    public PostDAO getPostDAO() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TopicDAO getTopicDAO() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TopicSearchFilterDAO getTopicSearchFilterDAO() {
        return new TopicSearchFilterDAOCookieImpl(request,response);
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDAOCookieImpl(request,response);
    }

}