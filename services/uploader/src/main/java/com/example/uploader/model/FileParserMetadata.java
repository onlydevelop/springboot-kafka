package com.example.uploader.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "FileParserMetadata")
public class FileParserMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private long fileStoreId;

    @Column
    private String name;

    @Column
    private String uuid;

    @Column
    private Integer letterCount;

    @Column
    private Integer wordCount;

    public FileParserMetadata() {}

    public FileParserMetadata(String name) {
        this.name = name;
    }
}
