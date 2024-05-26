package com.forums.forums.model.dao.mySQLJDBCImpl;

import com.forums.forums.model.dao.CategoryDAO;
import com.forums.forums.model.dao.exception.DuplicatedObjectException;
import com.forums.forums.model.mo.*;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;


public class CategoryDAOMySQLJDBCImpl implements CategoryDAO {
    Connection conn;

    public CategoryDAOMySQLJDBCImpl(Connection conn){this.conn = conn;}

    @Override
    public Category create(String name) throws DuplicatedObjectException {
        PreparedStatement ps;
        Category category = new Category();

        category.setName(name);
        category.setDeleted(false);

        try {

            String sql
                    = " SELECT categoryID "
                    + " FROM CATEGORY "
                    + " WHERE "
                    + " deleted = 'N' AND "
                    + " name = ? ";

            ps = conn.prepareStatement(sql);
            ps.setString(1, category.getName());

            ResultSet resultSet = ps.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new DuplicatedObjectException("CategoryDAOJDBCImpl.create: Tentativo di inserimento di una categoria già esistente.");
            }

            sql
                    = "INSERT INTO CATEGORY "
                    + "(name, "
                    + "deleted) "
                    + "VALUES (?, ?)";
            ps = conn.prepareStatement(sql);

            int i=1;
            ps.setString(i++, category.getName());
            ps.setString(i++, "N");

            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return category;
    }

    @Override
    public void update(Category category) throws DuplicatedObjectException {
        PreparedStatement ps;
        try {

            String sql
                    = " SELECT categoryID "
                    + " FROM CATEGORY "
                    + " WHERE "
                    + " deleted = 'N' AND "
                    + " name = ? ";

            ps = conn.prepareStatement(sql);
            ps.setString(1, category.getName());

            ResultSet resultSet = ps.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new DuplicatedObjectException("CategoryDAOJDBCImpl.create: Tentativo di inserimento di una categoria già esistente.");
            }

            sql = "UPDATE CATEGORY "
                    + "SET "
                    + "name = ? "
                    + "WHERE "
                    + "categoryID = ? ";
            ps = conn.prepareStatement(sql);

            int i=1;
            ps.setString(i, category.getName());
            ps.setLong(i++, category.getCategoryID());

            ps.executeUpdate();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Category category) {
        PreparedStatement ps;
        String sql;

        try {
            sql
                    = "UPDATE CATEGORY SET "
                    + "deleted = ? "
                    + "WHERE categoryID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, "Y");
            ps.setLong(2, category.getCategoryID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Category findByCategoryID(Long categoryID) {
        PreparedStatement ps;
        Category category = new Category();
        try {
            String sql
                    = "SELECT * " +
                    "FROM CATEGORY " +
                    "WHERE " +
                    "categoryID = ?";

            ps = conn.prepareStatement(sql);
            ps.setLong(1, categoryID);

            ResultSet resultSet = ps.executeQuery();

            if(resultSet.next()){
                category = read(resultSet);
            }
            resultSet.close();
            ps.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return category;
    }

    @Override
    public Category findByName(String name) {
        PreparedStatement ps;
        Category category = new Category();
        try {
            String sql
                    = "SELECT * " +
                    "FROM CATEGORY " +
                    "WHERE " +
                    "name = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, name);

            ResultSet resultSet = ps.executeQuery();

            if(resultSet.next()){
                category = read(resultSet);
            }
            resultSet.close();
            ps.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return category;
    }

    @Override
    public List<Category> getAll() {
        PreparedStatement ps;
        List<Category> categories = new ArrayList<>();

        try {
            String sql = "SELECT * FROM CATEGORY";

            ps = conn.prepareStatement(sql);

            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()){
                Category category = read(resultSet);
                categories.add(category);
            }
            resultSet.close();
            ps.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return categories;
    }


    Category read(ResultSet rs){
        Category category = new Category();

        try {

            category.setCategoryID(rs.getLong("categoryID"));

            category.setName(rs.getString("name"));

            category.setDeleted(rs.getString("deleted").equals("Y"));

        }
        catch (SQLException sqle){

            throw new RuntimeException("Error: read rs - Category", sqle);

        }

        return category;
    }

}
