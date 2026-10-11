package com.todoapp.service;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoStorageService {
    String uploadPhoto(MultipartFile file);
}


