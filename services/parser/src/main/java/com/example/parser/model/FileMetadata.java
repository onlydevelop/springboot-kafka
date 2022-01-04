package com.example.parser.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "FileMetadata")
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String name;

    @Column
    private String uuid;

    @Column
    private Integer letterCount;

    @Column
    private Integer wordCount;

    public FileMetadata() {}

    public FileMetadata(String name) {
        this.name = name;
    }
}
