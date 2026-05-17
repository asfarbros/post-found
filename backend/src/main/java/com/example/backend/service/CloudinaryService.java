package com.example.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    // Pattern to extract public ID from Cloudinary URL
    private static final Pattern PUBLIC_ID_PATTERN = Pattern.compile(".*/([^/.]+)(\\.[^.]+)?$");


    public String uploadFile(MultipartFile file) {
        try {
            // Upload the file to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            // Return the secure URL provided by Cloudinary
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not upload file to Cloudinary", e);
        }
    }

    public boolean deleteFile(String imageUrl) {
         if (imageUrl == null || imageUrl.isBlank()) {
            return false; // No image to delete
         }
        try {
            // Extract the public ID from the URL
            String publicId = extractPublicId(imageUrl);
            if (publicId == null) {
                System.err.println("Could not extract public ID from URL: " + imageUrl);
                return false;
            }

            // Delete the image from Cloudinary using its public ID
            Map deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            // Check the result from Cloudinary
            return "ok".equals(deleteResult.get("result"));
        } catch (IOException e) {
            System.err.println("Failed to delete image from Cloudinary: " + imageUrl + " Error: " + e.getMessage());
            return false; // Indicate failure
        } catch (Exception e) {
             System.err.println("An unexpected error occurred during Cloudinary deletion: " + e.getMessage());
             return false;
        }
    }

    // Helper method to extract the public ID (filename without extension) from a Cloudinary URL
   private String extractPublicId(String url) {
        Matcher matcher = PUBLIC_ID_PATTERN.matcher(url);
        if (matcher.find()) {
            // Group 1 captures the filename part without the extension
            return matcher.group(1);
        }
        return null; // Return null if pattern doesn't match
    }
}