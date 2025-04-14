package gov.epa.ccte.api.batchsearch.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Hashtable;


@Slf4j
@Service
public class ExportJobStatus {
    private final Hashtable<String, ResponseEntity<byte[]>> jobs = new Hashtable<>();

    public void addJob(String jobId) {

        jobs.put(jobId, new ResponseEntity<>(HttpStatus.NOT_FOUND)); // initially it is empty
    }

    public boolean getStatus(String jobId) {
        ResponseEntity<?> output = jobs.get(jobId);

        if(output != null){
            return HttpStatus.NOT_FOUND != output.getStatusCode();
        }else{
            log.error("jobid = {} doesn't exists", jobId);
            return false;
        }
    }

    public ResponseEntity<byte[]> getContent(String jobId) {
        log.debug("get content for job id = {}", jobId);

        ResponseEntity<byte[]> output = jobs.get(jobId);

        if(output != null){
            log.debug("Response Entity with status code = {} ", output.getStatusCode());

            removeJob(jobId);

            log.info("job cache has {} records ", jobs.size());
            return output;
        }else{
            log.error("jobid = {} doesn't exists", jobId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public void addContents(String jobId, ResponseEntity<byte[]> contents) {
        log.debug("add content to job id = {}", jobId);

        jobs.put(jobId, contents);
    }

    public void removeJob(String jobId) {
        log.debug("remove job id = {}", jobId);

        jobs.remove(jobId);
    }
}
