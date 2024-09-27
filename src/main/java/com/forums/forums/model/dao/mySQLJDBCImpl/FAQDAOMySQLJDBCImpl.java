package com.forums.forums.model.dao.mySQLJDBCImpl;

import com.forums.forums.model.dao.DAOFactory;
import com.forums.forums.model.dao.FAQDAO;
import com.forums.forums.model.dao.UserDAO;
import com.forums.forums.model.mo.*;
import com.forums.forums.services.config.Configuration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FAQDAOMySQLJDBCImpl implements FAQDAO {

    private final String COUNTER_ID = "faqID";
    Connection conn;

    public FAQDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public FAQ create(
            String question,
            String answer,
            Timestamp creationTimestamp,
            User author
    ) {
        PreparedStatement ps;

        FAQ faq = new FAQ();
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setCreationTimestamp(creationTimestamp);
        faq.setAuthor(author);
        faq.setDeleted(false);

        try {
            String sql = "UPDATE COUNTER SET counterValue=counterValue+1 where counterID='" + COUNTER_ID + "'";

            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            sql = "SELECT counterValue FROM COUNTER WHERE counterID='" + COUNTER_ID + "'";

            ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();

            faq.setFaqID(resultSet.getLong("counterValue"));

            resultSet.close();

            sql =
                    "INSERT INTO FAQ "
                            + "(faqID, "
                            + "faqQuestion, "
                            + "faqAnswer, "
                            + "faqCreationTimestamp, "
                            + "faqAuthorID, "
                            + "faqDeleted) "
                            + "VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setLong(i++, faq.getFaqID());
            ps.setString(i++, faq.getQuestion());
            ps.setString(i++, faq.getAnswer());
            ps.setTimestamp(i++, faq.getCreationTimestamp());
            ps.setLong(i++, faq.getAuthor().getUserID());
            ps.setString(i++,"N");

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return faq;
    }

    @Override
    public void update(FAQ faq) {
        PreparedStatement ps;

        try {
            String sql = "UPDATE FAQ SET "
                    + "faqQuestion = ?, "
                    + "faqAnswer = ? "
                    + "WHERE faqID = ? ";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, faq.getQuestion());
            ps.setString(i++, faq.getAnswer());
            ps.setLong(i++, faq.getFaqID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(FAQ faq) {
        PreparedStatement ps;
        String sql;

        try {
            sql
                    = "UPDATE FAQ SET "
                    + "faqDeleted = ? "
                    + "WHERE faqID = ? ";
            ps = conn.prepareStatement(sql);

            ps.setString(1, "Y");
            ps.setLong(2, faq.getFaqID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FAQ findByID(Long faqID) {
        PreparedStatement ps;
        FAQ faq = null;

        try {

            String sql
                    = " SELECT * "
                    + "   FROM FAQ "
                    + " WHERE "
                    + "   faqID = ?";

            ps = conn.prepareStatement(sql);
            ps.setLong(1, faqID);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                faq = read(resultSet);
            }
            resultSet.close();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return faq;
    }

    @Override
    public List<FAQ> getAll() {
        PreparedStatement ps;
        List<FAQ> faqs = new ArrayList<>();

        try {
            String sql =    "SELECT F.*, U.userUsername, U.userID " +
                            "FROM FAQ AS F " +
                            "LEFT JOIN USER AS U ON F.faqAuthorID = U.userID " +
                            "WHERE F.faqDeleted = 'N' " +
                            "ORDER BY F.faqQuestion ASC ";;

            ps = conn.prepareStatement(sql);

            ResultSet resultSet = ps.executeQuery();

            UserDAOMySQLJDBCImpl userDAOMySQLJDBC = new UserDAOMySQLJDBCImpl(this.conn);

            while(resultSet.next()){
                FAQ faq = read(resultSet);
                User author = userDAOMySQLJDBC.read(resultSet);
                faq.setAuthor(author);
                faqs.add(faq);
            }
            resultSet.close();
            ps.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return faqs;
    }

    FAQ read(ResultSet rs) {
        FAQ faq = new FAQ();

        User author = new User();

        faq.setAuthor(author);

        try {
            faq.setFaqID(rs.getLong("faqID"));
        } catch (SQLException sqle) {
        }


        try {
            faq.setQuestion(rs.getString("faqQuestion"));
        } catch (SQLException sqle) {
        }


        try {
            faq.setAnswer(rs.getString("faqAnswer"));
        } catch (SQLException sqle) {
        }


        try {
            faq.setCreationTimestamp(rs.getTimestamp("faqCreationTimestamp"));
        } catch (SQLException sqle) {
        }


        try {
            faq.getAuthor().setUserID(rs.getLong("faqAuthorID"));
        } catch (SQLException sqle) {
        }


        try {
            faq.setDeleted(rs.getString("faqDeleted").equals("Y"));
        } catch (SQLException sqle) {
        }

        return faq;
    }

}
