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
            User uploader,
            Post post
    ) {
        // Ottengo il timestamp corrente
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        PreparedStatement ps;

        Media media = new Media();
        media.setPath(path);
        media.setCreationTimestamp(currentTimestamp);
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
                    + "path, "
                    + "creationTimestamp, "
                    + "uploaderID, "
                    + "postID, "
                    + "deleted) "
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
                    + "deleted = ? "
                    + "WHERE topicID = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, "Y");
            ps.setLong(2, media.getMediaID());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
            media.setPath(rs.getString("path"));
            media.setCreationTimestamp(rs.getTimestamp("creationTimestamp"));
            media.getUploader().setUserID(rs.getLong("uploaderID"));
            media.getPost().setPostID(rs.getLong("postID"));
            media.setDeleted(rs.getString("deleted").equals("Y"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Media", sqle);
        }

        return media;
    }

}
