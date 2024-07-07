package com.forums.forums.services.profilepicpath;

import java.io.File;

public class ProfilePicPath {

    private ProfilePicPath() {
        // Costruttore privato per impedire l'istanziazione esterna
    }

    public static String profilePicPath(String username, boolean isDirectory) {

        // Costruzione del percorso dell'immagine del profilo
        String imagePath = File.separator + "opt" +
                           File.separator + "tomcat" +
                           File.separator + "webapps" +
                           File.separator + "Uploads" +
                           File.separator + "forums" +
                           File.separator + "users" +
                           File.separator + username;

        if (isDirectory == false) imagePath += (File.separator + "profilePic.png");
        return imagePath;
    }

}