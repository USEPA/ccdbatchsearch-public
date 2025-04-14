package gov.epa.ccte.api.batchsearch.service;

import gov.epa.ccte.api.batchsearch.repository.SearchWithChemicalDetailsRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ExportJob implements Runnable {
    private final SearchWithChemicalDetailsRepository repository;

    public ExportJob(SearchWithChemicalDetailsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run() {

    }
}
