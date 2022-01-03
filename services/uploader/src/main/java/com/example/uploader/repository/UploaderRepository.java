package com.example.uploader.repository;

import com.example.uploader.model.FileMetadata;
import org.springframework.data.repository.CrudRepository;

public interface UploaderRepository extends CrudRepository<FileMetadata, Long> {

}
