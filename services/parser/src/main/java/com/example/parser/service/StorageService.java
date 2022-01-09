package com.example.parser.service;

import com.example.parser.model.FileParserMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {
    void setup() throws IOException;
    FileParserMetadata store(MultipartFile file) throws IOException;
    Path load(String filename);
    void cleanup();
}
