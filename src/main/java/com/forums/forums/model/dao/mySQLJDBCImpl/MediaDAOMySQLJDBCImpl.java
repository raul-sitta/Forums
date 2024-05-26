package com.forums.forums.model.dao.mySQLJDBCImpl;

import com.forums.forums.model.dao.MediaDAO;
import com.forums.forums.model.mo.*;

import java.sql.*;

public class MediaDAOMySQLJDBCImpl implements MediaDAO {
    Connection conn;

    public MediaDAOMySQLJDBCImpl(Connection conn) {
        this.conn = conn;
    }

    public Media create(
            String path,
            User uploader,
            String linkedResourceType,
            Long linkedResourceID
    ) {
        // Ottengo il timestamp corrente
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        PreparedStatement ps;

        Media media = new Media();
        media.setPath(path);
        media.setCreationTimestamp(currentTimestamp);
        media.setUploader(uploader);
        media.setLinkedResourceType(linkedResourceType);
        media.setLinkedResourceID(linkedResourceID);
        media.setDeleted(false);

        try {
            String sql = "INSERT INTO MEDIA "
                    + "(path, "
                    + "creationTimestamp, "
                    + "uploaderID, "
                    + "linkedResourceType, "
                    + "linkedResourceID, "
                    + "deleted) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);

            int i = 1;
            ps.setString(i++, media.getPath());
            ps.setTimestamp(i++, media.getCreationTimestamp());
            ps.setLong(i++, media.getUploader().getUserID());
            ps.setString(i++, media.getLinkedResourceType());
            ps.setLong(i++, media.getLinkedResourceID());
            ps.setString(i++,"N");

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return media;
    }

    public void update(Media media) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

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

    public Media read(ResultSet rs) {
        Media media = new Media();

        User uploader = new User();

        media.setUploader(uploader);

        try {
            media.setMediaID(rs.getLong("mediaID"));
            media.setPath(rs.getString("path"));
            media.setCreationTimestamp(rs.getTimestamp("creationTimestamp"));
            media.getUploader().setUserID(rs.getLong("uploaderID"));
            media.setLinkedResourceType(rs.getString("linkedResourceType"));
            media.setLinkedResourceID(rs.getLong("linkedResourceID"));
            media.setDeleted(rs.getString("deleted").equals("Y"));
        } catch (SQLException sqle) {
            throw new RuntimeException("Error: read rs - Media", sqle);
        }

        return media;
    }

}
