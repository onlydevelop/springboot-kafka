package com.example.filestore.repository;

import com.example.filestore.model.FileStoreMetadata;
import org.springframework.data.repository.CrudRepository;

public interface FileStoreRepository extends CrudRepository<FileStoreMetadata, Long> {

}
