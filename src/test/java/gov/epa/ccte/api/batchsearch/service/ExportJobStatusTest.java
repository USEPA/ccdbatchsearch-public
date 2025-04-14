package gov.epa.ccte.api.batchsearch.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

class ExportJobStatusTest {

    ExportJobStatus exportJobStatus;
    String jobId;

    @BeforeEach
    void setUp() {
        exportJobStatus = new ExportJobStatus();

        jobId = UUID.randomUUID().toString();

        exportJobStatus.addJob(jobId);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addJob() {

        ResponseEntity<?> output = exportJobStatus.getContent(jobId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, output.getStatusCode());

        //Assertions.assertNull(exportJobStatus.getStatus(jobId));
    }

    @Test
    void getStatusWhenJobInProgress() {
        boolean result = exportJobStatus.getStatus(jobId);

        Assertions.assertFalse(result);
    }

    @Test
    void getStatusWhenJobIsDone() {

        boolean result = exportJobStatus.getStatus(jobId);

        Assertions.assertFalse(result);
    }

    @Test
    void getContent() {
        ResponseEntity<byte[]> content = new ResponseEntity<>(HttpStatus.OK);

        exportJobStatus.addContents(jobId, content);

        ResponseEntity<byte[]> output = exportJobStatus.getContent(jobId);

        Assertions.assertEquals(output, content);

    }

    @Test
    void addContents() {
        ResponseEntity<byte[]> content = new ResponseEntity<>(HttpStatus.OK);

        exportJobStatus.addContents(jobId, content);

        boolean result = exportJobStatus.getStatus(jobId);

        Assertions.assertTrue(result);
    }
}