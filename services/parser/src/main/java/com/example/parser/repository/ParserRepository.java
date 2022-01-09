package com.example.parser.repository;

import com.example.parser.model.FileParserMetadata;
import org.springframework.data.repository.CrudRepository;

public interface ParserRepository extends CrudRepository<FileParserMetadata, Long> {

}