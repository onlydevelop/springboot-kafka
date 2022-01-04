package com.example.uploader.controller;

import com.example.uploader.model.FileMetadata;
import com.example.uploader.repository.UploaderRepository;
import com.example.uploader.service.ProducerService;
import com.example.uploader.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/files")
public class UploaderController {

    @Autowired
    UploaderRepository uploaderRepository;

    private final StorageService storageService;
    private final ProducerService producerService;

    @Autowired
    public UploaderController(StorageService storageService, ProducerService producerService) {
        this.storageService = storageService;
        this.producerService = producerService;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileMetadata fileMetadata = storageService.store(file);
            FileMetadata savedMetadata = uploaderRepository.save(fileMetadata);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedMetadata.getId())
                    .toUri();

            producerService.sendMessage(String.valueOf(savedMetadata.getId()));
            return ResponseEntity.created(location).build();
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileMetadata> get(@PathVariable("id") Long id) {
        Optional<FileMetadata> fileMetadata = uploaderRepository.findById(id);

        if (fileMetadata.isPresent()) {
            return new ResponseEntity<>(fileMetadata.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
