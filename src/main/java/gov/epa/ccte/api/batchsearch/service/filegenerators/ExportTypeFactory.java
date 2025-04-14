package gov.epa.ccte.api.batchsearch.service.filegenerators;

import gov.epa.ccte.api.batchsearch.service.filegenerators.csv.*;
import gov.epa.ccte.api.batchsearch.service.filegenerators.excel.*;
import gov.epa.ccte.api.batchsearch.service.filegenerators.sdf.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ExportTypeFactory {

    static Map<String, ExportType> exportTypeMap = new HashMap<>();

    static {
        exportTypeMap.put("IDENTIFIER_CSV", new IdentifierToCsv());
        exportTypeMap.put("IDENTIFIER_SDF", new IdentifierToSdf());
        exportTypeMap.put("IDENTIFIER_EXCEL", new IdentifierToExcel());

        exportTypeMap.put("DTXCID_CSV", new DtxcidToCsv());
        exportTypeMap.put("DTXCID_SDF", new DtxcidToSdf());
        exportTypeMap.put("DTXCID_EXCEL", new DtxcidToExcel());

        exportTypeMap.put("INCHIKEY_SKELETON_CSV", new InChIKeySkeletonToCsv());
        exportTypeMap.put("INCHIKEY_SKELETON_SDF", new InChIKeySkeletonToSdf());
        exportTypeMap.put("INCHIKEY_SKELETON_EXCEL", new InChIKeySkeletonToExcel());

        exportTypeMap.put("MASS_CSV", new MassToCsv());
        exportTypeMap.put("MASS_SDF", new MassToSdf());
        exportTypeMap.put("MASS_EXCEL", new MassToExcel());

        exportTypeMap.put("EXACT_FORMULA_CSV", new ExactFormulaToCsv());
        exportTypeMap.put("EXACT_FORMULA_SDF", new ExactFormulaToSdf());
        exportTypeMap.put("EXACT_FORMULA_EXCEL", new ExactFormulaToExcel());

        exportTypeMap.put("MSREADY_FORMULA_CSV", new MsReadyFormulaToCsv());
        exportTypeMap.put("MSREADY_FORMULA_SDF", new MsReadyFormulaToSdf());
        exportTypeMap.put("MSREADY_FORMULA_EXCEL", new MsReadyFormulaToExcel());

    }

    public static Optional<ExportType> getExportType(String searchType, String exportType) {

        String option = searchType + "_" + exportType;
        log.debug("option ", option);

        switch (option){
            case "IDENTIFIER_CSV":
                return Optional.ofNullable(new IdentifierToCsv());
            case "IDENTIFIER_SDF":
                return Optional.ofNullable(new IdentifierToSdf());
            case "IDENTIFIER_EXCEL":
                return Optional.ofNullable(new IdentifierToExcel());
            case "DTXCID_CSV":
                return Optional.ofNullable(new DtxcidToCsv());
            case "DTXCID_SDF":
                return Optional.ofNullable(new DtxcidToSdf());
            case "DTXCID_EXCEL":
                return Optional.ofNullable(new DtxcidToExcel());
            case "INCHIKEY_SKELETON_CSV":
                return Optional.ofNullable(new InChIKeySkeletonToCsv());
            case "INCHIKEY_SKELETON_SDF":
                return Optional.ofNullable(new InChIKeySkeletonToSdf());
            case "INCHIKEY_SKELETON_EXCEL":
                return Optional.ofNullable(new InChIKeySkeletonToExcel());
            case "MASS_CSV":
                return Optional.ofNullable(new MassToCsv());
            case "MASS_SDF":
                return Optional.ofNullable(new MassToSdf());
            case "MASS_EXCEL":
                return Optional.ofNullable(new MassToExcel());
            case "EXACT_FORMULA_CSV":
                return Optional.ofNullable(new ExactFormulaToCsv());
            case "EXACT_FORMULA_SDF":
                return Optional.ofNullable(new ExactFormulaToSdf());
            case "EXACT_FORMULA_EXCEL":
                return Optional.ofNullable(new ExactFormulaToExcel());
            case "MSREADY_FORMULA_CSV":
                return Optional.ofNullable(new MsReadyFormulaToCsv());
            case "MSREADY_FORMULA_SDF":
                return Optional.ofNullable(new MsReadyFormulaToSdf());
            case "MSREADY_FORMULA_EXCEL":
                return Optional.ofNullable(new MsReadyFormulaToExcel());
            default:
                return null;
        }
    }

//    public static Optional<ExportType> getExportType(String searchType, String exportType) {
//        log.debug("search type {}, export type = {}", searchType, exportType);
//        return Optional.ofNullable(exportTypeMap.get(searchType + "_" + exportType));
//    }

}
