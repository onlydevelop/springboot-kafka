package com.example.filestore.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "FileStoreMetadata")
public class FileStoreMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String name;

    @Column
    private String uuid;

    public FileStoreMetadata() {}

    public FileStoreMetadata(String name) {
        this.name = name;
    }
}
