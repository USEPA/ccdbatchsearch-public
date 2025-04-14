package gov.epa.ccte.api.batchsearch.service;

import gov.epa.ccte.api.batchsearch.repository.*;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportTypeFactory;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchTypeFactory;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExportJobExecutor {

    @Async
    public void run(String jobId, BatchSearchForm formBean,
                    SearchWithChemicalDetailsRepository chemicalRepository,
                    ChemicalDetailsRepository detailsRepository,
                    ExportJobStatus exportJobStatus,
                    ChemlistToSubtanceRepository chemlistToSubtanceRepository,
                    ChemicalSynonymRepository synonymRepository,
                    RelatedSubstanceRepository relatedSubstanceRepository,
                    BioactivityAssayListRepository assayListRepository,
                    ToxvalBatchSearchRepository toxvalBatchSearchRepository,
                    ToxrefBatchSearchRepository toxrefBatchSearchRepository,
                    ChemicalPropertiesRepository propertiesRepository) {

        log.info(" ---------- START JOB DETAILS = {}  ----------", jobId);
        log.info(" input type = {} ", formBean.getInputType());
        log.info(" identifier tye = {} ", formBean.getIdentifierTypes());
        log.info(" download type = {}", formBean.getDownloadType());
        log.info(" download items = {} ", formBean.getDownloadItems());
        log.info(" search items count = {} ", formBean.getSearchItems().split("\n").length);
        log.info(" ---------- END JOB DETAILS = {}  ----------", jobId);

        SearchType searchType = SearchTypeFactory.getSearchType(formBean.getInputType()).get(); // I need to implement exception here

        searchType.setRepository(chemicalRepository, detailsRepository, chemlistToSubtanceRepository, synonymRepository, relatedSubstanceRepository, assayListRepository, toxvalBatchSearchRepository, toxrefBatchSearchRepository,propertiesRepository);

        ExportType exportType = ExportTypeFactory.getExportType(formBean.getInputType(), formBean.getDownloadType()).get();

        exportType.configure(searchType, formBean);

        // HashMap<String, List<SearchWithChemicalDetails>> result = identifierSearch.getResults(formBean);
        // https://www.ch.ic.ac.uk/chemime/ has list of mime types for chemcials
        ResponseEntity<byte[]> contents = exportType.getResponseEntity();

        exportJobStatus.addContents(jobId, contents);
    }
}
