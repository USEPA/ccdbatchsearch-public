package gov.epa.ccte.api.batchsearch.web.rest;

import gov.epa.ccte.api.batchsearch.domain.ChemicalDetails;
import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.repository.*;
import gov.epa.ccte.api.batchsearch.service.ExportJobExecutor;
import gov.epa.ccte.api.batchsearch.service.ExportJobStatus;
import gov.epa.ccte.api.batchsearch.service.SearchChemicalService;
import gov.epa.ccte.api.batchsearch.service.SearchResultService;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportTypeFactory;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchTypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@CrossOrigin
@RestController
public class BatchSearchController {

    final private SearchWithChemicalDetailsRepository chemicalRepository;
    final private ChemicalDetailsRepository detailsRepository;
    final private SearchChemicalService chemicalService;
    final private ExportJobStatus exportJobStatus;
    final private ExportJobExecutor exportJobExecutor;
    final private ChemlistToSubtanceRepository chemlistToSubtanceRepository;
    final private ChemicalSynonymRepository synonymRepository;
    final private RelatedSubstanceRepository relatedSubstanceRepository;
    final private BioactivityAssayListRepository assayListRepository;
    final private ToxvalBatchSearchRepository toxvalBatchSearchRepository;
    final private ToxrefBatchSearchRepository toxrefBatchSearchRepository;
    final private ChemicalPropertiesRepository propertiesRepository;


    public BatchSearchController(SearchWithChemicalDetailsRepository chemicalRepository,
                                 ChemicalDetailsRepository detailsRepository, SearchChemicalService chemicalService,
                                 ExportJobStatus exportJobStatus,
                                 ExportJobExecutor exportJobExecutor,
                                 ChemlistToSubtanceRepository chemlistToSubtanceRepository,
                                 ChemicalSynonymRepository synonymRepository,
                                 RelatedSubstanceRepository relatedSubstanceRepository,
                                 BioactivityAssayListRepository assayListRepository, ToxvalBatchSearchRepository toxvalBatchSearchRepository, ToxrefBatchSearchRepository toxrefBatchSearchRepository, ChemicalPropertiesRepository propertiesRepository) {

        this.chemicalRepository = chemicalRepository;
        this.detailsRepository = detailsRepository;
        this.chemicalService = chemicalService;
        this.exportJobStatus = exportJobStatus;
        this.exportJobExecutor = exportJobExecutor;
        this.chemlistToSubtanceRepository = chemlistToSubtanceRepository;
        this.synonymRepository = synonymRepository;
        this.relatedSubstanceRepository = relatedSubstanceRepository;
        this.assayListRepository = assayListRepository;
        this.toxvalBatchSearchRepository = toxvalBatchSearchRepository;
        this.toxrefBatchSearchRepository = toxrefBatchSearchRepository;
        this.propertiesRepository = propertiesRepository;
    }

    @RequestMapping(value = "/export/", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> getJobId(@RequestBody BatchSearchForm formBean) throws Exception {

        String jobId = UUID.randomUUID().toString();
        log.debug("Job id generated = {} ", jobId);
        log.debug("form = {} ", formBean.getSearchItems());

        exportJobStatus.addJob(jobId);

        exportJobExecutor.run(jobId, formBean, chemicalRepository, detailsRepository, exportJobStatus,
                chemlistToSubtanceRepository, synonymRepository, relatedSubstanceRepository, assayListRepository, toxvalBatchSearchRepository, toxrefBatchSearchRepository,propertiesRepository);

        return new ResponseEntity<>(jobId, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/export/status/{jobid}", method = RequestMethod.GET)
    public @ResponseBody
    Boolean getJobStatus(@PathVariable("jobid") String jobId) throws Exception {

        log.debug(" get status for job id = {} ", jobId);

        return exportJobStatus.getStatus(jobId);
    }

    @RequestMapping(value = "/export/remove/{jobid}", method = RequestMethod.GET)
    public @ResponseBody
    Boolean removeJobId(@PathVariable("jobid") String jobId) throws Exception {

        log.debug(" remove job id = {} ", jobId);

        exportJobStatus.removeJob(jobId);
      
        return true;
    }

    @RequestMapping(value = "/export/content/{jobid}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<byte[]> getJobContent(@PathVariable("jobid") String jobId) throws Exception {

        log.debug(" get content for job id = {} ", jobId);

        ResponseEntity<byte[]> output = exportJobStatus.getContent(jobId);

        exportJobStatus.removeJob(jobId);

        return output;
    }


    @RequestMapping(value = "/export/excel", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<byte[]> exportExcel(@RequestBody BatchSearchForm formBean) throws Exception {

        SearchType searchType = SearchTypeFactory.getSearchType(formBean.getInputType()).get(); // I need to implement exception here

        searchType.setRepository(chemicalRepository, detailsRepository, chemlistToSubtanceRepository,
                synonymRepository, relatedSubstanceRepository, assayListRepository, toxvalBatchSearchRepository, toxrefBatchSearchRepository, propertiesRepository);

        ExportType exportType = ExportTypeFactory.getExportType(formBean.getInputType(), "EXCEL").get();

        exportType.configure(searchType, formBean);

        // HashMap<String, List<SearchWithChemicalDetails>> result = identifierSearch.getResults(formBean);
        // https://www.ch.ic.ac.uk/chemime/ has list of mime types for chemcials

        return exportType.getResponseEntity();
    }

    @RequestMapping(value = "/export/csv", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<byte[]> exportCsv(@RequestBody BatchSearchForm formBean) throws Exception {

        SearchType searchType = SearchTypeFactory.getSearchType(formBean.getInputType()).get(); // I need to implement exception here

        searchType.setRepository(chemicalRepository, detailsRepository, chemlistToSubtanceRepository,
                synonymRepository, relatedSubstanceRepository,assayListRepository,toxvalBatchSearchRepository,toxrefBatchSearchRepository,propertiesRepository);

        ExportType exportType = ExportTypeFactory.getExportType(formBean.getInputType(), "CSV").get();

        exportType.configure(searchType, formBean);

        // HashMap<String, List<SearchWithChemicalDetails>> result = identifierSearch.getResults(formBean);
        // https://www.ch.ic.ac.uk/chemime/ has list of mime types for chemcials
        return exportType.getResponseEntity();
    }


    @RequestMapping(value = "/export/sdf", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<byte[]> exportSdf(@RequestBody BatchSearchForm formBean) throws Exception {

        SearchType identifierSearch = SearchTypeFactory.getSearchType(formBean.getInputType()).get(); // I need to implement exception here

        identifierSearch.setRepository(chemicalRepository, detailsRepository, chemlistToSubtanceRepository,
                synonymRepository, relatedSubstanceRepository, assayListRepository,toxvalBatchSearchRepository,toxrefBatchSearchRepository,propertiesRepository);

        ExportType exportType = ExportTypeFactory.getExportType(formBean.getInputType(), "SDF").get();

        exportType.configure(identifierSearch, formBean);

        // https://www.ch.ic.ac.uk/chemime/ has list of mime types for chemcials
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("chemical/x-mdl-sdfile"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "test.sdf")
                .body(exportType.export());
    }

    @RequestMapping(value = "/chemicals", method = RequestMethod.POST)
    public @ResponseBody
    List<ChemicalDetails> getChemicals(@RequestBody BatchSearchForm formBean) throws Exception {

        log.debug("form = {}", formBean);

        SearchType searchType = SearchTypeFactory.getSearchType(formBean.getInputType()).get(); // I need to implement exception here

        log.debug("searchType = {} ", searchType);

        searchType.setRepository(chemicalRepository, detailsRepository, chemlistToSubtanceRepository,
                synonymRepository, relatedSubstanceRepository, assayListRepository,toxvalBatchSearchRepository,toxrefBatchSearchRepository,propertiesRepository);

        return searchType.getChemicals(formBean);
    }

    @RequestMapping(value = "/results", method = RequestMethod.POST)
    public @ResponseBody
    List<BatchSearchResult> getResults(@RequestBody BatchSearchForm formBean) throws Exception {

        SearchType searchType = SearchTypeFactory.getSearchType(formBean.getInputType()).get(); // I need to implement exception here

        searchType.setRepository(chemicalRepository, detailsRepository, chemlistToSubtanceRepository,
                synonymRepository, relatedSubstanceRepository, assayListRepository,toxvalBatchSearchRepository, toxrefBatchSearchRepository,propertiesRepository);

        HashMap<String, List<SearchWithChemicalDetails>> dbResult = searchType.getResults(formBean);

        return SearchResultService.getBatchSearchResults(searchType.getProcessedSearchName(), dbResult);
    }

    @RequestMapping(value = "/testdb", method = RequestMethod.GET)
    void testDbConnection(){

        log.debug("testing db connection");

        List<ChemicalDetails> chemicals = detailsRepository.testDbConnection();
    }

    private List<String> getSearchMatchToInclude(List<String> inputTypes) {

        List<String> searchMatchToInclude = new ArrayList<>();
        List<String> searchMatchForChemicalName = Arrays.asList("Approved Name", "Synonym", "Systematic Name", "Integrated Source Name", "Expert Validated Synonym", "Synonym from Valid Source", "FDA CAS-Like Identifier", "EHCA Number");
        List<String> searchMatchForCasrn = Arrays.asList("Deleted CAS-RN", "Alternate CAS-RN", "CAS-RN", "Integrated Source CAS-RN");
        List<String> searchMatchForInchikey = Arrays.asList("InChIKey", "Indigo InChIKey");

        for (String inputtype : inputTypes) {

            if (inputtype.equalsIgnoreCase("chemical_name")) {
                searchMatchToInclude.addAll(searchMatchForChemicalName);
            } else if (inputtype.equalsIgnoreCase("casrn")) {
                searchMatchToInclude.addAll(searchMatchForCasrn);
            } else if (inputtype.equalsIgnoreCase("inchikey")) {
                searchMatchToInclude.addAll(searchMatchForInchikey);
            } else if (inputtype.equalsIgnoreCase("DSSTox_Substance_Id")) {
                searchMatchToInclude.add("DSSTox_Substance_Id");
            } else if (inputtype.equalsIgnoreCase("DSSTox_Compound_Id")) {
                searchMatchToInclude.add("DSSTox_Compound_Id");
            } else if (inputtype.equalsIgnoreCase("skeleton")) {
                searchMatchToInclude.addAll(searchMatchForInchikey);
            }
        }

        return searchMatchToInclude;
    }

}
