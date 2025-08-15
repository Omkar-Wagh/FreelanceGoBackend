package com.freelancego.service.UserService;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class ImageEncoderService {

    public byte[] downloadImageFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000); // optional timeout
            connection.setReadTimeout(3000);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Image not found or inaccessible: " + responseCode);
                return null;
            }

            try (InputStream in = connection.getInputStream();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                return baos.toByteArray();
            }

        } catch (Exception e) {
            // Optional: log the error or ignore silently
            System.out.println("Error fetching image: " + e.getMessage());
            return null;
        }
    }

}
