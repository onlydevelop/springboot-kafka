package com.example.parser.service;

import com.example.parser.model.FileParserMetadata;
import com.example.parser.repository.ParserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class ParserService {

    @Autowired
    ParserRepository parserRepository;

    public void parse(Long id) {
        Optional<FileParserMetadata> fileMetadataOpt = parserRepository.findById(id);

        if (fileMetadataOpt.isPresent()) {
            FileParserMetadata fileMetadata = fileMetadataOpt.get();
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

    private void parseFile(FileParserMetadata fileParserMetadata) throws FileNotFoundException {
        String url = "http://filestore/api/v1/files/" + fileParserMetadata.getFileStoreId() + "/download";
        File tempFile = null;
        try {
            tempFile = File.createTempFile("parser-", ".parse");
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        FileReader file = new FileReader(tempFile);
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
            fileParserMetadata.setLetterCount(letterCount);
            fileParserMetadata.setWordCount(wordCount);
            tempFile.delete();
        }
    }
}
