package com.example.filestore.controller;

import com.example.filestore.model.FileStoreMetadata;
import com.example.filestore.repository.FileStoreRepository;
import com.example.filestore.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FileStoreControllerTest {
    static class TestableFileStoreController extends FileStoreController {
        public TestableFileStoreController(StorageService storageService, FileStoreRepository filestoreRepository) {
            super(storageService, filestoreRepository);
        }

        @Override
        protected void writeFileToResponse(InputStream inputStream, HttpServletResponse response) throws IOException {
            // Do nothing
        }
    }

    @InjectMocks
    private TestableFileStoreController fileStoreController;

    @Mock
    private FileStoreRepository fileStoreRepository;

    @Mock
    private StorageService storageService;

    private FileStoreMetadata fileStoreMetadata;

    private MockMultipartFile mockMultipartFile;

    private MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

    private InputStream inputStream = new InputStream() {
        @Override
        public int read() throws IOException {
            return 0;
        }
    };

    @BeforeEach
    public void setup() throws IOException {
        initFileStoreMetadata();
        when(storageService.store(mockMultipartFile)).thenReturn(fileStoreMetadata);
        when(storageService.load("uuid")).thenReturn(inputStream);
        when(fileStoreRepository.save(fileStoreMetadata)).thenReturn(fileStoreMetadata);
        when(fileStoreRepository.findById(1L)).thenReturn(Optional.ofNullable(fileStoreMetadata));
    }

    @Test
    public void testUploadFile() {
        ResponseEntity<HttpStatus> res = fileStoreController.uploadFile(mockMultipartFile);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertEquals("http://localhost/1", res.getHeaders().getLocation().toString());
        assertEquals(null, res.getBody());
    }

    @Test
    public void testUploadFileWithException() throws IOException {
        when(storageService.store(mockMultipartFile)).thenThrow(new IOException());
        ResponseEntity<HttpStatus> res = fileStoreController.uploadFile(mockMultipartFile);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
        assertEquals(0, res.getHeaders().size());
        assertEquals(null, res.getBody());
    }

    @Test
    public void testGetValidMetadata() {
        ResponseEntity<FileStoreMetadata> res = fileStoreController.get(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(fileStoreMetadata, res.getBody());
    }

    @Test
    public void testGetInvalidMetadata() {
        ResponseEntity<FileStoreMetadata> res = fileStoreController.get(2L);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertEquals(null, res.getBody());
    }

    @Test
    public void testGetValidDownload() throws UnsupportedEncodingException {
        fileStoreController.getDownload(1L, mockHttpServletResponse);
        assertEquals("attachment;filename=Dummy", mockHttpServletResponse.getHeader("Content-disposition"));
        assertEquals("txt/plain", mockHttpServletResponse.getContentType());
        assertEquals("", mockHttpServletResponse.getContentAsString());
    }

    private void initFileStoreMetadata() {
        fileStoreMetadata = new FileStoreMetadata();
        fileStoreMetadata.setId(1);
        fileStoreMetadata.setName("Dummy");
        fileStoreMetadata.setUuid("uuid");
    }
}
