package com.example.parser.repository;

import com.example.parser.model.FileMetadata;
import org.springframework.data.repository.CrudRepository;

public interface ParserRepository extends CrudRepository<FileMetadata, Long> {

}