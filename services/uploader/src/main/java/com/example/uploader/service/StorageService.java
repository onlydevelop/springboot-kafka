package com.example.uploader.service;

import com.example.uploader.model.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {
    void setup() throws IOException;
    FileMetadata store(MultipartFile file) throws IOException;
    Path load(String filename);
    void cleanup();
}
