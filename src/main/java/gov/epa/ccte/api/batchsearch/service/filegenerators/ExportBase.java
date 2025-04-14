package gov.epa.ccte.api.batchsearch.service.filegenerators;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.SearchChemicalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

@Slf4j
public abstract class ExportBase {

    protected String EMPTY_VALUE = "N/A";

    protected void setEmptyValue(String emptyValue) {
        this.EMPTY_VALUE = emptyValue;
    }

    protected String getColumnContents(String searchWord, String column, SearchWithChemicalDetails details) {

        switch (StringUtils.upperCase(column.trim())) {
            case "INPUT":
                return searchWord;
            case "FOUND_BY":
                return details.getSearchMatch();

            //  Chemical Identifiers
            case "DTXSID":
                return checkNull(details.getDtxsid());
            case "PREFERRED_NAME":
            case "SEARCHED_CHEMICAL":// Synonym Identifier
                return checkNull(details.getPreferredName());
            case "DSSTox_Compound_Id":
            case "DTXCID":
                return checkNull(details.getDtxcid());
            case "CASRN":
                return checkNull(details.getCasrn());
            case "INCHIKEY":
                return checkNull(details.getInchikey());
            case "IUPAC_NAME":
                return checkNull(details.getIupacName());

            // Structures
            case "SMILES":
                return checkNull(details.getSmiles());
            case "INCHI_STRING":
                return checkNull(details.getInchiString());
            case "MS_READY_SMILES":
                return checkNull(details.getMsReadySmiles());
            case "MS_READY_FORMULAE":
                return checkNull(details.getMolFormula());
            case "MS_READY_MASS":
                return checkNull(details.getMolWeight());
            case "QSAR_READY_SMILES":
                return checkNull(details.getQsarReadySmiles());

            // Intrinsic And Predicted Properties
            case "MOLECULAR_FORMULA":
                return checkNull(details.getMolFormula());
            case "AVERAGE_MASS":
                return checkNull(details.getAverageMass());
            case "MONOISOTOPIC_MASS":
                return checkNull(details.getMonoisotopicMass());

            // Intrinsic And Predicted Properties - TEST model prediction
            case "BIOCONCENTRATION_FACTOR_TEST_PRED":
                return checkNull(details.getBioconcentrationFactorTestPred());
            case "BOILING_POINT_DEGC_TEST_PRED":
                return checkNull(details.getBoilingPointDegcTestPred());
            case "48HR_DAPHNIA_LC50_MOL/L_TEST_PRED":
                return checkNull(details.getHrDiphniaLc50());
            case "DENSITY_G/CM^3_TEST_PRED":
                return checkNull(details.getDensity());
            case "DEVTOX_TEST_PRED":
                return checkNull(details.getDevtoxTestPred());
            case "96HR_FATHEAD_MINNOW_MOL/L_TEST_PRED":
                return checkNull(details.getHrFatheadMinnow());
            case "FLASH_POINT_DEGC_TEST_PRED":
                return checkNull(details.getFlashPointDegcTestPred());
            case "MELTING_POINT_DEGC_TEST_PRED":
                return checkNull(details.getMeltingPointDegcTestPred());
            case "AMES_MUTAGENICITY_TEST_PRED":
                return checkNull(details.getAmesMutagenicityTestPred());
            case "ORAL_RAT_LD50_MOL/KG_TEST_PRED":
                return checkNull(details.getOralRatLd50Mol());
            case "SURFACE_TENSION_DYN/CM_TEST_PRED":
                return checkNull(details.getSurfaceTension());
            case "THERMAL_CONDUCTIVITY_MW/(M*K)_TEST_PRED":
                return checkNull(details.getThermalConductivity());
            case "TETRAHYMENA_PYRIFORMIS_IGC50_MOL/L_TEST_PRED":
                return checkNull(details.getTetrahymenaPyriformis());
            case "VISCOSITY_CP_CP_TEST_PRED":
                return checkNull(details.getViscosityCpCpTestPred());
            case "VAPOR_PRESSURE_MMHG_TEST_PRED":
                return checkNull(details.getVaporPressureMmhgTestPred());
            case "WATER_SOLUBILITY_MOL/L_TEST_PRED":
                return checkNull(details.getWaterSolubilityTest());

            //Intrinsic And Predicted Properties - OPERA Model Predictions
            case "ATMOSPHERIC_HYDROXYLATION_RATE_(AOH)_CM3/MOLECULE*SEC_OPERA_PRED":
                return checkNull(details.getAtmosphericHydroxylationRate());
            case "BIOCONCENTRATION_FACTOR_OPERA_PRED":
                return checkNull(details.getBioconcentrationFactorOperaPred());
            case "BIODEGRADATION_HALF_LIFE_DAYS_DAYS_OPERA_PRED":
                return checkNull(details.getBiodegradationHalfLifeDays());
            case "BOILING_POINT_DEGC_OPERA_PRED":
                return checkNull(details.getBoilingPointDegcOperaPred());
            case "HENRYS_LAW_ATM-M3/MOLE_OPERA_PRED":
                return checkNull(details.getHenrysLawAtm());
            case "OPERA_KM_DAYS_OPERA_PRED":
                return checkNull(details.getOperaKmDaysOperaPred());
            case "OCTANOL_AIR_PARTITION_COEFF_LOGKOA_OPERA_PRED":
                return checkNull(details.getOctanolAirPartitionCoeff());
            case "SOIL_ADSORPTION_COEFFICIENT_KOC_L/KG_OPERA_PRED":
                return checkNull(details.getSoilAdsorptionCoefficient());
            case "OCTANOL_WATER_PARTITION_LOGP_OPERA_PRED":
                return checkNull(details.getOctanolWaterPartition());
            case "MELTING_POINT_DEGC_OPERA_PRED":
                return checkNull(details.getMeltingPointDegcOperaPred());
            case "VAPOR_PRESSURE_MMHG_OPERA_PRED":
                return checkNull(details.getVaporPressureMmhgOperaPred());
            case "WATER_SOLUBILITY_MOL/L_OPERA_PRED":
                return checkNull(details.getWaterSolubilityOpera());
            case "OPERA_PKAA_OPERA_PRED":
                return checkNull(details.getPkaaOperaPred());
            case "OPERA_PKAB_OPERA_PRED":
                return checkNull(details.getPkabOperaPred());
            case "LOGD5.5":
                return checkNull(details.getLogD55());
            case "LOGD7.4":
                return checkNull(details.getLogD74());
            case "READY_BIO_DEG":
                return checkNull(details.getReadyBioDeg());

            // Metadata
            case "QC_LEVEL":
                return checkNull(details.getQcLevel());
            case "EXPOCAST_MEDIAN_EXPOSURE_PREDICTION_MG/KG-BW/DAY":
                return checkNull(details.getExpocatMedianPrediction());
            case "EXPOCAST":
                return getExpoCastURL(details.getExpocat(), details.getDtxsid());
            case "NHANES":
                return getNhanesUrl(details.getNhanes(), details.getDtxsid());

            case "DATA_SOURCES":
                return checkNull(details.getSourcesCount());
            case "TOXVAL_DATA":
                return getToxvalUrl(details.getToxvalData(), details.getDtxsid());
            case "TOXCAST_PERCENT_ACTIVE":
                return checkNull(calculatePercent(details.getActiveAssays(), details.getTotalAssays()));
            case "TOXCAST_NUMBER_OF_ASSAYS/TOTAL":
                return getAssaysAndTotal(details.getActiveAssays(),details.getTotalAssays());
            case "IRIS_LINK":
                return getIrisLink(details.getIrisLink());
            case "PPRTV_LINK":
                return getPprtvLink(details.getPprtvLink());
            case "WIKIPEDIA_ARTICLE":
                return getWikipediaFlag(details.getWikipediaArticle());
            case "QC_NOTES":
                return checkNull(details.getQcNotes());
            case "ACTOR_REPORT":
                return getActorWsUrl(details.getCasrn());
            case "SAFETY_DATA":
                return getSafetyData(details.getDtxsid());


            // for Mass search
            case "MASS_DIFFERENCE":
                return String.valueOf(details.getMonoisotopicMass() - parseStartMassValue(searchWord));
            case "MONOISOTOPIC_MASS_INDIVIDUAL_COMPONENT":
                return checkNull(details.getMonoisotopicMass());
            case "SMILES_INDIVIDUAL_COMPONENT":
                return checkNull(details.getSmiles());
            // Ms Ready formula search
            case "FORMULA_INDIVIDUAL_COMPONENT":
                return checkNull(details.getMolFormula());

            //Enhanced Data Sheets
            // Toxprint
            case "TOXPRINTS_FINGERPRINT":
                return checkNull(details.getDescriptorStringTsv());
            case "TOXPRINTS_CHEMOTYPER":
                return checkNull(details.getDescriptorStringTsv());
            case "IDENTIFIER":
                return checkNull(details.getSynonyms());
            case "PC-CODES":
                return checkNull(details.getPcCode());

            // Abstract Sifter
            case "DSSTOX_LINK_TO_DASHBOARD":
                return checkNull(details.getDtxsid());
            case "CHEMICAL/ENTITY_QUERY":
                return details.getCasrn() + " OR " + details.getPreferredName();

            // MetFrag Input File
            case "DTXCID_INDIVIDUAL_COMPONENT":
                return checkNull(details.getDtxcid());
//            case "FORMULA_INDIVIDUAL_COMPONENT":
//                return checkNull(details.getMolFormula());
//            case "SMILES_INDIVIDUAL_COMPONENT":
//                return checkNull(details.getSmiles());
            case "MAPPED_DTXSID":
                return checkNull(details.getDtxsid());
            case "PREFERRED_NAME_DTXSID":
                return checkNull(details.getPreferredName());
            case "CASRN_DTXSID":
                return checkNull(details.getCasrn());
            case "FORMULA_MAPPED_DTXSID":
                return checkNull(details.getMolFormula());
            case "SMILES_MAPPED_DTXSID":
                return checkNull(details.getSmiles());
//            case "MS_READY_SMILES":
//                return checkNull(details.getMsReadySmiles());
            case "INCHI_STRING_DTXCID":
                return checkNull(details.getInchiString());
            case "INCHIKEY_DTXCID":
                return checkNull(details.getInchikey());
            case "MONOISOTOPIC_MASS_DTXCID":
                return checkNull(details.getMonoisotopicMass());

            default:
                return column; // means no matching column found
        }
    }

    private String getAssaysAndTotal(Integer activeAssays, Integer totalAssays) {
        if(activeAssays != null && totalAssays != null &&
                activeAssays.equals("") == false && totalAssays.equals("") == false){
            return activeAssays + "/" + totalAssays;
        }else
            return EMPTY_VALUE;
    }

    private String calculatePercent(Integer activeAssays, Integer totalAssays) {
        log.debug("active = {} and total = {}", activeAssays, totalAssays);

        if(activeAssays != null && totalAssays != null && totalAssays != 0){
            double percent = ((double) activeAssays/totalAssays);
            percent *= 100.0;

            DecimalFormat format = new DecimalFormat("##.##");

            return format.format(percent);
        }else{
            return EMPTY_VALUE;
        }
    }

    private String getPprtvLink(String pprtvLink) {
        if(StringUtils.isNotBlank(pprtvLink))
            return "Y";
        else
            return EMPTY_VALUE;
    }

    private String getSafetyData(String dtxsid) {
        if(SearchChemicalService.getIsSafetyDataValue(dtxsid))
            return "Y";
        else
            return EMPTY_VALUE;
    }

    private String getIrisLink(String irisLink) {
        if(StringUtils.isNotBlank(irisLink))
            return "Y";
        else
            return EMPTY_VALUE;
    }

    private String getWikipediaFlag(String wikipediaArticle) {
        if(StringUtils.isNotBlank(wikipediaArticle))
            //return "https://comptox.epa.gov/dashboard/dsstoxdb/results?search=" + dtxsid + "#toxicity-values";
            return "Y";
        else
            return EMPTY_VALUE;
    }

    private String getToxvalUrl(String toxvalData, String dtxsid) {
        if(StringUtils.isNotBlank(toxvalData) && toxvalData.equalsIgnoreCase("Y"))
            //return "https://comptox.epa.gov/dashboard/dsstoxdb/results?search=" + dtxsid + "#toxicity-values";
            return "Y";
        else
            return EMPTY_VALUE;
    }

    protected String getExpoCastURL(String expocat, String dtxsid) {
        if(StringUtils.isNotBlank(expocat) && expocat.equalsIgnoreCase("Y") && StringUtils.isNotBlank(dtxsid))
            //return "http://ccte-ccd.epa.gov/dashboard/chemical/exposure-predictions/" + dtxsid;
            return "Y";
        else
            return EMPTY_VALUE;
    }

    protected String getNhanesUrl(String nhanes, String dtxsid) {
        if(StringUtils.isNotBlank(nhanes) && nhanes.equalsIgnoreCase("Y")&& StringUtils.isNotBlank(dtxsid))
            // return "http://ccte-ccd.epa.gov/dashboard/chemical/monitoring-data/" + dtxsid;
            return "Y";
        else
            return EMPTY_VALUE;
    }

    protected String getActorWsUrl(String casrn) {
        if(StringUtils.isNotBlank(casrn))
            return "https://actorws.epa.gov/actorws/actor/2015q3/chemicalPdfExport.pdf?casrn=" + casrn;
        else
            return EMPTY_VALUE;
    }

    protected Double parseStartMassValue(String searchWord) {
        //log.debug("searchWord = {}", searchWord);
        // example word - 189 +/- 5ppm
        String temp = searchWord.substring(0, searchWord.indexOf("+"));
        //log.debug("parse = {}", temp);
        return Double.parseDouble(temp);
    }


    // --- null checkes
    protected String checkNull(BigInteger value) {
        if (value != null)
            return value.toString();
        else
            return EMPTY_VALUE;
    }

    protected String checkNull(BigDecimal value) {
        if (value != null)
            return value.toString();
        else
            return EMPTY_VALUE;
    }

    protected String checkNull(Long value) {
        if (value != null)
            return value.toString();
        else
            return EMPTY_VALUE;
    }

    protected String checkNull(Integer value) {
        if (value != null)
            return value.toString();
        else
            return EMPTY_VALUE;
    }

    protected String checkNull(Double value) {
        if (value != null)
            return value.toString();
        else
            return EMPTY_VALUE;
    }

    protected String checkNull(String value) {
        if ( value != null && value.equals("") == false)
            return value;
        else
            return EMPTY_VALUE;
    }

}
