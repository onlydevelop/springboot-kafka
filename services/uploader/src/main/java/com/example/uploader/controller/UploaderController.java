package com.example.uploader.controller;

import com.example.uploader.model.FileParserMetadata;
import com.example.uploader.repository.UploaderRepository;
import com.example.uploader.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/files")
public class UploaderController {

    @Autowired
    UploaderRepository uploaderRepository;

    private final ProducerService producerService;

    @Autowired
    public UploaderController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String uri = "http://filestore/api/v1/files"; // TODO: make it right
            RestTemplate template = new RestTemplate();
            URI fileStoreLocation = template.postForLocation(uri, file);
            FileParserMetadata fileStoreMetadata = template.getForEntity(fileStoreLocation, FileParserMetadata.class).getBody();
            FileParserMetadata fileParserMetadata = new FileParserMetadata();
            fileParserMetadata.setFileStoreId(fileStoreMetadata.getId());
            fileParserMetadata.setName(fileStoreMetadata.getName());
            fileParserMetadata.setUuid(fileStoreMetadata.getUuid());
            FileParserMetadata savedMetadata = uploaderRepository.save(fileStoreMetadata);

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
    public ResponseEntity<FileParserMetadata> get(@PathVariable("id") Long id) {
        Optional<FileParserMetadata> fileMetadata = uploaderRepository.findById(id);

        if (fileMetadata.isPresent()) {
            return new ResponseEntity<>(fileMetadata.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
