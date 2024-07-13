package com.forums.forums.model.dao;

import com.forums.forums.model.dao.mySQLJDBCImpl.MySQLJDBCDAOFactory;
import com.forums.forums.model.dao.CookieImpl.CookieDAOFactory;

import java.util.Map;

public abstract class DAOFactory {

    public static final String MYSQLJDBCIMPL = "MySQLJDBCImpl";
    public static final String COOKIEIMPL= "CookieImpl";

    public abstract void beginTransaction();
    public abstract void commitTransaction();
    public abstract void rollbackTransaction();
    public abstract void closeTransaction();

    public abstract CategoryDAO getCategoryDAO();
    public abstract MediaDAO getMediaDAO();
    public abstract PostDAO getPostDAO();
    public abstract ReportDAO getReportDAO();
    public abstract TopicDAO getTopicDAO();
    public abstract TopicSearchFilterDAO getTopicSearchFilterDAO();
    public abstract UserDAO getUserDAO();

    public static DAOFactory getDAOFactory(String whichFactory,Map factoryParameters) {

        if (whichFactory.equals(MYSQLJDBCIMPL)) {
            return new MySQLJDBCDAOFactory(factoryParameters);
        } else if (whichFactory.equals(COOKIEIMPL)) {
            return new CookieDAOFactory(factoryParameters);
        } else {
            return null;
        }
    }
}

