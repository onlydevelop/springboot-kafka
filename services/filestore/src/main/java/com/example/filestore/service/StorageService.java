package com.example.filestore.service;

import com.example.filestore.model.FileStoreMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface StorageService {
    void setup() throws IOException;
    FileStoreMetadata store(MultipartFile file) throws IOException;
    InputStream load(String filename);
    void cleanup();
}
