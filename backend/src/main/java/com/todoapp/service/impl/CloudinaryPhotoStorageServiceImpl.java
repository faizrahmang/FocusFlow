package com.todoapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.todoapp.exception.PhotoTooLargeException;
import com.todoapp.service.PhotoStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Uploads proof-of-completion photos to Cloudinary.
 *
 * Cloudinary's free tier rejects files over 10MB, and the resulting error is an
 * opaque RuntimeException surfacing as a generic 500. Reject oversized files
 * up front with a clear, typed error instead.
 */
@Service
public class CloudinaryPhotoStorageServiceImpl implements PhotoStorageService {

    /** Cloudinary free-tier per-file upload ceiling. */
    private static final long MAX_UPLOAD_BYTES = 10L * 1024 * 1024;

    private final Cloudinary cloudinary;

    public CloudinaryPhotoStorageServiceImpl(
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    @Override
    public String uploadPhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        if (file.getSize() > MAX_UPLOAD_BYTES) {
            throw new PhotoTooLargeException("Photo is too large. Please choose an image under 10MB.");
        }
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload photo", e);
        }
    }
}


