package com.example.filestore.service;

import com.example.filestore.model.FileStoreMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {
    private final Path baseDir;

    @Autowired
    public StorageServiceImpl() {
        this.baseDir = Paths.get("").toAbsolutePath().getParent().resolve("storage");
    }

    @Override
    public void setup() throws IOException {
        try {
            Files.createDirectories(baseDir);
        }
        catch (IOException e) {
            throw new IOException("Failed to setup storage", e);
        }
    }

    @Override
    public FileStoreMetadata store(MultipartFile file) throws IOException {
        FileStoreMetadata fileMetadata = new FileStoreMetadata();
        try {
            if (file.isEmpty()) {
                throw new IOException("Failed to store empty file.");
            }
            UUID uuid = UUID.randomUUID();
            fileMetadata.setName(file.getOriginalFilename());
            fileMetadata.setUuid(uuid.toString());
            Path destinationFile = this.baseDir.resolve(
                            Paths.get(uuid.toString()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.baseDir.toAbsolutePath())) {
                // This is a security check
                throw new IOException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new IOException("Failed to store file.", e);
        } finally {
            return fileMetadata;
        }
    }

    @Override
    public InputStream load(String filename) {
        Path path = baseDir.resolve(filename);
        try {
            return Files.newInputStream(path, StandardOpenOption.READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void cleanup() {
        FileSystemUtils.deleteRecursively(baseDir.toFile());
    }
}
