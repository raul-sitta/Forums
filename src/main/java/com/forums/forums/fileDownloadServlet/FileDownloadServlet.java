package com.forums.forums.fileDownloadServlet;

import com.forums.forums.services.filesystemservice.FileSystemService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/FileDownloadServlet")
public class FileDownloadServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String mediaPath = request.getParameter("mediaPath");
        String fullMediaPath = FileSystemService.BASE_DIR_PATH + mediaPath;
        File file = new File(fullMediaPath);

        if (file.exists()) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

            try (FileInputStream inStream = new FileInputStream(file);
                 OutputStream outStream = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Errore, file non trovato!");
        }
    }
}
