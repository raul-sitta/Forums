package com.forums.forums.model.dao.mySQLJDBCImpl;

import com.forums.forums.model.dao.CategoryDAO;
import com.forums.forums.model.dao.exception.DuplicatedObjectException;
import com.forums.forums.model.mo.*;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;


public class CategoryDAOMySQLJDBCImpl implements CategoryDAO {

    private final String COUNTER_ID = "categoryID";
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
                    + " categoryDeleted = 'N' AND "
                    + " categoryName = ? ";

            ps = conn.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, category.getName());

            ResultSet resultSet = ps.executeQuery();

            boolean exist;
            exist = resultSet.next();
            resultSet.close();

            if (exist) {
                throw new DuplicatedObjectException("CategoryDAOJDBCImpl.create: Tentativo di inserimento di una categoria già esistente.");
            }

            sql = "UPDATE COUNTER SET counterValue=counterValue+1 where counterID='" + COUNTER_ID + "'";

            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            sql = "SELECT counterValue FROM COUNTER WHERE counterID='" + COUNTER_ID + "'";

            ps = conn.prepareStatement(sql);
            resultSet = ps.executeQuery();
            resultSet.next();

            category.setCategoryID(resultSet.getLong("counterValue"));

            resultSet.close();

            sql
                    = "INSERT INTO CATEGORY "
                    + "(categoryID, "
                    + "categoryName, "
                    + "categoryDeleted) "
                    + "VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql);

            i = 1;
            ps.setLong(i++, category.getCategoryID());
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
                    + " categoryDeleted = 'N' AND "
                    + " categoryName = ? ";

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
                    + "categoryName = ? "
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
                    + "categoryDeleted = ? "
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
                    "categoryName = ?";

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
        }
        catch (SQLException sqle){
        }


        try {
            category.setName(rs.getString("categoryName"));
        }
        catch (SQLException sqle){
        }


        try {
            category.setDeleted(rs.getString("categoryDeleted").equals("Y"));
        }
        catch (SQLException sqle){
        }


        return category;
    }

}
