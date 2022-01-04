package com.example.parser.service;

import com.example.parser.model.FileMetadata;
import com.example.parser.repository.ParserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class ParserService {

    @Autowired
    ParserRepository parserRepository;

    @Autowired
    StorageService storageService;

    public void parse(Long id) {
        Optional<FileMetadata> fileMetadataOpt = parserRepository.findById(id);


        if (fileMetadataOpt.isPresent()) {
            FileMetadata fileMetadata = fileMetadataOpt.get();
            try {
                parseFile(fileMetadata);
                parserRepository.save(fileMetadata);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: Could not find the entry " + id);
        }
    }

    private void parseFile(FileMetadata fileMetadata) throws FileNotFoundException {
        Path filePath = storageService.load(fileMetadata.getUuid());
        FileReader file = new FileReader(filePath.toFile());
        BufferedReader bufferedReader = new BufferedReader(file);
        int letterCount = 0, wordCount = 0;
        try {
            String line;
            while ((line = bufferedReader.readLine()) !=null){
                wordCount = wordCount + line.split("\\s+").length;
                letterCount = letterCount + line.replaceAll("\\s", "").length();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileMetadata.setLetterCount(letterCount);
            fileMetadata.setWordCount(wordCount);
        }
    }
}
