package com.example.uploader.repository;

import com.example.uploader.model.FileParserMetadata;
import org.springframework.data.repository.CrudRepository;

public interface UploaderRepository extends CrudRepository<FileParserMetadata, Long> {

}
