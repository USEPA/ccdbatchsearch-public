package gov.epa.ccte.api.batchsearch.service.filegenerators.sdf;

import com.epam.indigo.Indigo;
import com.epam.indigo.IndigoObject;
import gov.epa.ccte.api.batchsearch.domain.ChemlistToSubtance;
import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportBase;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
abstract class SdfBase extends ExportBase {

    // SDF export
    Indigo indigo = new Indigo();

    protected IndigoObject getIndigoSdfObject(SearchWithChemicalDetails chemical, String[] attributes, String searchName) {

        // set default value for empty value
        setEmptyValue("N/A");

        IndigoObject sdf;

        String molFile;
        //log.debug("dtxsid = {}", chemical.getDtxsid());

        if (chemical.getDtxcid() != null)
            molFile = chemical.getMolFile();
        else
            molFile = null;

        if (molFile != null)
            try {
                sdf = indigo.loadMolecule(molFile);
            }catch (Exception e){
                // mol file couldn't load
                log.error("Dtxsid = {} mol file has some issue, couldn't load it.", chemical.getDtxsid());
                sdf = indigo.createMolecule();
            }
        else
            sdf = indigo.createMolecule();

        // Check if attributes contains MS_READY_SMILES and add two headers MS_READY_MASS,MS_READY_FORMULAE
        boolean containsMsready = false;
        for (String attribute : attributes) {
            if (attribute.equals("MS_READY_SMILES")) {
                containsMsready = true;
                break;
            }
        }

        if (containsMsready) {
            String[] updatedAttributes = new String[attributes.length + 2];
            System.arraycopy(attributes, 0, updatedAttributes, 0, attributes.length);
            updatedAttributes[attributes.length] = "MS_READY_MASS";
            updatedAttributes[attributes.length + 1] = "MS_READY_FORMULAE";
            attributes = updatedAttributes;
        }

        for (String attr : attributes) {
            sdf.setProperty(attr, checkNull(getColumnContents(searchName, attr, chemical)));
            //getAttributeValue(attr, chemical)));
        }

        return sdf;
    }


    // download file Media type
    public MediaType getSdfContentType() {
        log.debug("media type = {}", "chemical/x-mdl-sdfile");
        return MediaType.parseMediaType("chemical/x-mdl-sdfile");
    }

    public String getSdfFilename() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh_mm_ss");
        String excelFileName = "CCD-Batch-Search_" + formatter.format(LocalDateTime.now()) + ".sdf";

        log.debug("excel file name = {}", excelFileName);

        return excelFileName;
    }

    IndigoObject buildSDFfile(HashMap<String, List<SearchWithChemicalDetails>> chemicals, String[] headers, String molVersion){
        IndigoObject buf = indigo.writeBuffer();
        indigo.setOption("ignore-stereochemistry-errors", true);
        indigo.setOption("ignore-noncritical-query-features", true);

        if(molVersion == null || molVersion.equalsIgnoreCase("V2000")){
            indigo.setOption("molfile-saving-mode", "2000");
        }else{
            indigo.setOption("molfile-saving-mode", "3000");
        }

        for(Map.Entry<String, List<SearchWithChemicalDetails>> result : chemicals.entrySet()){
            for (SearchWithChemicalDetails details : result.getValue()) {
                IndigoObject sdf = getIndigoSdfObject(details, headers, result.getKey());
                buf.sdfAppend(sdf);
            }
        }

        return buf;
    }

    protected IndigoObject getIndigoSdfObjectWithChemicalLists(SearchWithChemicalDetails chemical, String[] attributes, String searchName, Set<String> chemlistLookup, String[] selectedChemicalLists) {

        // set default value for empty value
        setEmptyValue("N/A");

        IndigoObject sdf;

        String molFile;
        //log.debug("dtxsid = {}", chemical.getDtxsid());

        if (chemical.getDtxcid() != null)
            molFile = chemical.getMolFile();
        else
            molFile = null;

        if (molFile != null)
            try {
                sdf = indigo.loadMolecule(molFile);
            }catch (Exception e){
                // mol file couldn't load
                log.error("Dtxsid = {} mol file has some issue, couldn't load it.", chemical.getDtxsid());
                sdf = indigo.createMolecule();
            }
        else
            sdf = indigo.createMolecule();

        for (String attr : attributes) {
            sdf.setProperty(attr, checkNull(getColumnContents(searchName, attr, chemical)));
            for(String s: selectedChemicalLists) {
                if (attr.equalsIgnoreCase(s)) {
                    sdf.setProperty(attr, checkNull(getChemicalList(chemical, attr, chemlistLookup)));
                }
            }
        }

        return sdf;
    }
    public String getChemicalList(SearchWithChemicalDetails chemical, String attr,Set<String> chemlistLookup) {
        if(chemlistLookup != null && chemlistLookup.contains(chemical.getDtxsid() + "-" + attr))
            return "Y";
        else
            return "-";
    }

    IndigoObject buildSDFfileWithChemicalLists(HashMap<String, List<SearchWithChemicalDetails>> chemicals, String[] headers, String molVersion, Set<String> chemlistLookup, String[] selectedChemicalLists){
        IndigoObject buf = indigo.writeBuffer();
        indigo.setOption("ignore-stereochemistry-errors", true);
        indigo.setOption("ignore-noncritical-query-features", true);

        if(molVersion == null || molVersion.equalsIgnoreCase("V2000")){
            indigo.setOption("molfile-saving-mode", "2000");
        }else{
            indigo.setOption("molfile-saving-mode", "3000");
        }

        for(Map.Entry<String, List<SearchWithChemicalDetails>> result : chemicals.entrySet()){
            for (SearchWithChemicalDetails details : result.getValue()) {
                IndigoObject sdf = getIndigoSdfObjectWithChemicalLists(details, headers, result.getKey(),chemlistLookup,selectedChemicalLists);
                buf.sdfAppend(sdf);
            }
        }

        return buf;
    }

    protected Set<String> buildChemicalListlookup(List<ChemlistToSubtance> chemlistToSubtanceList){
        Set<String> lookup = new HashSet<>();

        log.debug("build chemical list lookup - count()={} ", chemlistToSubtanceList.size());
        for(ChemlistToSubtance chemlist : chemlistToSubtanceList){
            lookup.add(chemlist.getDtxsid() + "-" + chemlist.getListName());
        }

        return lookup;
    }

    protected HashMap<String, List<SearchWithChemicalDetails>> addMissingChemicals(BatchSearchForm searchForm, HashMap<String, String> processedSearchWords, HashMap<String, List<SearchWithChemicalDetails>> chemicals) {
        for (String searchWord : searchForm.getSearchItems().split("\n")) {
            String key = processedSearchWords.get(searchWord);
            if (!chemicals.containsKey(key)) {
                List<SearchWithChemicalDetails> detailsList = new ArrayList<>();
                SearchWithChemicalDetails searchWithChemicalDetails = new SearchWithChemicalDetails();
                searchWithChemicalDetails.setSearchWord(key);
                searchWithChemicalDetails.setModifiedValue(key);
                searchWithChemicalDetails.setSearchMatch("Found 0 results");
                detailsList.add(searchWithChemicalDetails);
                chemicals.put(key, detailsList);
            }
        }
        return chemicals;

    }

}
