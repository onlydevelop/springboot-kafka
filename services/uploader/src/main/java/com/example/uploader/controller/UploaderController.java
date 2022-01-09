package com.example.uploader.controller;

import com.example.uploader.model.FileParserMetadata;
import com.example.uploader.repository.UploaderRepository;
import com.example.uploader.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/files")
public class UploaderController {

    @Value( "${fileStoreServer}" )
    private String fileStoreServer;

    @Autowired
    UploaderRepository uploaderRepository;

    private final ProducerService producerService;

    @Autowired
    public UploaderController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        try {
            String uri = "http://" + fileStoreServer + "/api/v1/files";
            RestTemplate restTemplate = new RestTemplate();

            Path tempFile = Files.createTempFile(null, null);
            Files.write(tempFile, multipartFile.getBytes());
            File fileToSend = tempFile.toFile();

            MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
            parameters.add("file", new FileSystemResource(fileToSend));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "multipart/form-data");
            HttpEntity httpEntity = new HttpEntity<>(parameters, headers);
            ResponseEntity response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, ResponseEntity.class);

            URI fileStoreLocation = response.getHeaders().getLocation();
            FileParserMetadata fileStoreMetadata = restTemplate.getForEntity(fileStoreLocation, FileParserMetadata.class).getBody();
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
