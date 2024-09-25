package com.forums.forums.model.dao.mySQLJDBCImpl;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.forums.forums.model.mo.TopicSearchFilter;
import com.forums.forums.services.config.Configuration;

import com.forums.forums.model.dao.*;

public class MySQLJDBCDAOFactory extends DAOFactory {

    private Map factoryParameters;

    private Connection connection;

    public MySQLJDBCDAOFactory(Map factoryParameters) {
        this.factoryParameters=factoryParameters;
    }

    @Override
    public void beginTransaction() {

        try {
            Class.forName(Configuration.DATABASE_DRIVER);
            this.connection = DriverManager.getConnection(Configuration.DATABASE_URL);
            this.connection.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void commitTransaction() {
        try {
            this.connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollbackTransaction() {

        try {
            this.connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void closeTransaction() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryDAO getCategoryDAO() {
        return new CategoryDAOMySQLJDBCImpl(connection);
    }

    @Override
    public FAQDAO getFAQDAO() {
        return new FAQDAOMySQLJDBCImpl(connection);
    }

    @Override
    public MediaDAO getMediaDAO() {
        return new MediaDAOMySQLJDBCImpl(connection);
    }

    @Override
    public NavigationStateDAO getNavigationStateDAO() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PostDAO getPostDAO() {
        return new PostDAOMySQLJDBCImpl(connection);
    }

    @Override
    public TopicDAO getTopicDAO() {
        return new TopicDAOMySQLJDBCImpl(connection);
    }

    @Override
    public TopicSearchFilterDAO getTopicSearchFilterDAO() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDAOMySQLJDBCImpl(connection);
    }
}