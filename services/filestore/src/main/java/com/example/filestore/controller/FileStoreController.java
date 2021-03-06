package com.example.filestore.controller;

import com.example.filestore.model.FileStoreMetadata;
import com.example.filestore.repository.FileStoreRepository;
import com.example.filestore.service.StorageService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/files")
public class FileStoreController {

    private final FileStoreRepository filestoreRepository;
    private final StorageService storageService;

    @Autowired
    public FileStoreController(StorageService storageService, FileStoreRepository filestoreRepository) {
        this.storageService = storageService;
        this.filestoreRepository = filestoreRepository;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileStoreMetadata fileMetadata = storageService.store(file);
            FileStoreMetadata savedMetadata = filestoreRepository.save(fileMetadata);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequestUri()
                    .path("/{id}")
                    .buildAndExpand(savedMetadata.getId())
                    .toUri();

            return ResponseEntity.created(location).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileStoreMetadata> get(@PathVariable("id") Long id) {
        Optional<FileStoreMetadata> fileMetadata = filestoreRepository.findById(id);

        if (fileMetadata.isPresent()) {
            return new ResponseEntity<>(fileMetadata.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/download")
    public void getDownload(@PathVariable("id") Long id, HttpServletResponse response) {
        Optional<FileStoreMetadata> fileMetadata = filestoreRepository.findById(id);

        if (fileMetadata.isPresent()) {
            String uuid = fileMetadata.get().getUuid();
            InputStream myStream = storageService.load(uuid);
            if (myStream == null) return;

            response.addHeader("Content-disposition", "attachment;filename=" + fileMetadata.get().getName());
            response.setContentType("txt/plain");

            try {
                writeFileToResponse(myStream, response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void writeFileToResponse(InputStream inputStream, HttpServletResponse response) throws IOException {
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }
}
