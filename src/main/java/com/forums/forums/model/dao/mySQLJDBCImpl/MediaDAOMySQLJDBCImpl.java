package com.forums.forums.model.dao.mySQLJDBCImpl;

import com.forums.forums.model.dao.MediaDAO;
import com.forums.forums.model.mo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaDAOMySQLJDBCImpl implements MediaDAO {

    private final String COUNTER_ID = "mediaID";
    Connection conn;

    public MediaDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Media create(
            String path,
            Timestamp creationTimestamp,
            User uploader,
            Post post
    ) {
        PreparedStatement ps;

        Media media = new Media();
        media.setPath(path);
        media.setCreationTimestamp(creationTimestamp);
        media.setUploader(uploader);
        media.setPost(post);
        media.setDeleted(false);

        try {
            String sql = "UPDATE COUNTER SET counterValue=counterValue+1 where counterID='" + COUNTER_ID + "'";

            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            sql = "SELECT counterValue FROM COUNTER WHERE counterID='" + COUNTER_ID + "'";

            ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();

            media.setMediaID(resultSet.getLong("counterValue"));

            resultSet.close();

            sql =
                    "INSERT INTO MEDIA "
                            + "(mediaID, "
                            + "mediaPath, "
                            + "mediaCreationTimestamp, "
                            + "mediaUploaderID, "
                            + "mediaPostID, "
                            + "mediaDeleted) "
                            + "VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setLong(i++, media.getMediaID());
            ps.setString(i++, media.getPath());
            ps.setTimestamp(i++, media.getCreationTimestamp());
            ps.setLong(i++, media.getUploader().getUserID());
            ps.setLong(i++, media.getPost().getPostID());
            ps.setString(i++,"N");

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return media;
    }

    @Override
    public void update(Media media) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Media media) {
        PreparedStatement ps;
        String sql;

        try {
            sql
                    = "UPDATE MEDIA SET "
                    + "mediaDeleted = ? "
                    + "WHERE mediaTopicID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, "Y");
            ps.setLong(2, media.getMediaID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Media findByMediaID(Long mediaID) {
        PreparedStatement ps;
        Media media = null;

        try {

            String sql
                    = " SELECT * "
                    + "   FROM MEDIA "
                    + " WHERE "
                    + "   mediaID = ?";

            ps = conn.prepareStatement(sql);
            ps.setLong(1, mediaID);

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                media = read(resultSet);
            }
            resultSet.close();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return media;
    }

    @Override
    public List<Media> getAll() {
        PreparedStatement ps;
        List<Media> medias = new ArrayList<>();

        try {
            String sql = "SELECT * FROM MEDIA";

            ps = conn.prepareStatement(sql);

            ResultSet resultSet = ps.executeQuery();

            while(resultSet.next()){
                Media media = read(resultSet);
                medias.add(media);
            }
            resultSet.close();
            ps.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }

        return medias;
    }

    Media read(ResultSet rs) {
        Media media = new Media();

        User uploader = new User();
        Post post = new Post();

        media.setUploader(uploader);
        media.setPost(post);

        try {
            media.setMediaID(rs.getLong("mediaID"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Media", sqle);
        }


        try {
            media.setPath(rs.getString("mediaPath"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Media", sqle);
        }


        try {
            media.setCreationTimestamp(rs.getTimestamp("mediaCreationTimestamp"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Media", sqle);
        }


        try {
            media.getUploader().setUserID(rs.getLong("mediaUploaderID"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Media", sqle);
        }


        try {
            media.getPost().setPostID(rs.getLong("mediaPostID"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Media", sqle);
        }


        try {
            media.setDeleted(rs.getString("mediaDeleted").equals("Y"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Media", sqle);
        }

        return media;
    }

}
