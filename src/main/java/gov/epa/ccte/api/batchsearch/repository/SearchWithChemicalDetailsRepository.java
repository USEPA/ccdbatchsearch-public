package gov.epa.ccte.api.batchsearch.repository;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Collection;
import java.util.List;

//@SuppressWarnings("unused")
@RepositoryRestResource(exported = false)
public interface SearchWithChemicalDetailsRepository extends JpaRepository<SearchWithChemicalDetails, String> {

    // start with multiple values
    @RestResource(exported = false)
/*    @Query(value = "select  *  " +
            "from ccd_app.vw_chemical_search  " +
            "where modified_value ~* :inchikeys and search_name in (:searchName) ", nativeQuery = true)*/
//    @Query(value = "select new gov.epa.ccte.api.batchsearch.domain.ChemicalDetailsDto (dtxsid, null, dtxcid, generic_substance_id, casrn, preferred_name, compound_id," +
//            "       stereo,isotope, multicomponent, pubchem_count,pubmed_count, sources_count,cpdata_count,active_assays," +
//            "       total_assays, percent_assays,toxcast_select,monoisotopic_mass,mol_formula, qc_level,qc_level_desc, " +
//            "       pubchem_cid,has_structure_image,related_substance_count,related_structure_count,iupac_name,smiles,inchikey,average_mass)" +
//            "from (select row_number() over (partition by modified_value, dtxsid order by rank asc) as rnk,\n" +
//            "id, dtxsid,dtxcid,rank,search_name,search_group,search_value,modified_value,casrn,compound_id,generic_substance_id,\n" +
//            "             preferred_name,active_assays,cpdata_count,mol_formula,monoisotopic_mass,percent_assays,pubchem_count,\n" +
//            "             pubmed_count,sources_count,qc_level,qc_level_desc,stereo,isotope,multicomponent,total_assays,toxcast_select,\n" +
//            "             pubchem_cid,related_substance_count,related_structure_count,iupac_name,smiles,inchi_string,average_mass,\n" +
//            "             inchikey,has_structure_image\n" +
//            "      from ccd_app.vw_chemical_search\n" +
//            "      where modified_value ~* :inchikeys and search_name in ('InChIKey', 'Indigo InChIKey')\n" +
//            "      group by id, dtxsid, dtxcid, search_name, search_group, search_value, modified_value, rank, casrn,\n" +
//            "               compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula,\n" +
//            "               monoisotopic_mass, percent_assays, pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc,\n" +
//            "               stereo, isotope, multicomponent, total_assays, toxcast_select, pubchem_cid, related_substance_count,\n" +
//            "               related_structure_count, iupac_name, smiles, inchi_string, average_mass, inchikey, has_structure_image\n" +
//            "      order by modified_value\n" +
//            "     ) x\n" +
//            "where rnk = 1\n" +
//            "order by inchikey", nativeQuery = true
//    )
//    @Query(value = "select * " +
//            "from (select row_number() over (partition by modified_value, dtxsid order by rank asc) as rnk,\n" +
//            "id, dtxsid,dtxcid,rank,search_name,search_group,search_value,modified_value,casrn,compound_id,generic_substance_id,\n" +
//            "             preferred_name,active_assays,cpdata_count,mol_formula,monoisotopic_mass,percent_assays,pubchem_count,\n" +
//            "             pubmed_count,sources_count,qc_level,qc_level_desc,stereo,isotope,multicomponent,total_assays,toxcast_select,\n" +
//            "             pubchem_cid,related_substance_count,related_structure_count,iupac_name,smiles,inchi_string,average_mass,\n" +
//            "             inchikey,has_structure_image\n" +
//            "      from ccd_app.vw_chemical_search\n" +
//            "      where modified_value ~* :inchikeys and search_name in ('InChIKey', 'Indigo InChIKey')\n" +
//            "      group by id, dtxsid, dtxcid, search_name, search_group, search_value, modified_value, rank, casrn,\n" +
//            "               compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula,\n" +
//            "               monoisotopic_mass, percent_assays, pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc,\n" +
//            "               stereo, isotope, multicomponent, total_assays, toxcast_select, pubchem_cid, related_substance_count,\n" +
//            "               related_structure_count, iupac_name, smiles, inchi_string, average_mass, inchikey, has_structure_image\n" +
//            "      order by modified_value\n" +
//            "     ) x\n" +
//            "where rnk = 1\n" +
//            "order by inchikey", nativeQuery = true
//    )

//    @Query(value = "select *\n" +
//            "from (select row_number() over (partition by modified_value, dtxsid order by rank asc) as rnk, id, search_name, " +
//            "search_group, search_value, modified_value, rank, dtxsid, dtxcid, casrn, compound_id, generic_substance_id, " +
//            "preferred_name, active_assays, cpdata_count, mol_formula, monoisotopic_mass, percent_assays, pubchem_count, " +
//            "pubmed_count, sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays, toxcast_select, " +
//            "pubchem_cid, mol_file, mrv_file, related_substance_count, related_structure_count, has_structure_image, iupac_name, " +
//            "smiles, inchi_string, average_mass, inchikey, qc_notes, ms_ready_smiles, qsar_ready_smiles, iris_link, pprtv_link, " +
//            "wikipedia_article, water_solubility_test, water_solubility_opera, viscosity_cp_cp_test_pred, vapor_pressure_mmhg_test_pred, " +
//            "vapor_pressure_mmhg_opera_pred, thermal_conductivity, tetrahymena_pyriformis, surface_tension, soil_adsorption_coefficient, " +
//            "oral_rat_ld50_mol, opera_km_days_opera_pred, octanol_water_partition, octanol_air_partition_coeff, melting_point_degc_test_pred, " +
//            "melting_point_degc_opera_pred, hr_fathead_minnow, hr_diphnia_lc50, henrys_law_atm, flash_point_degc_test_pred, " +
//            "devtox_test_pred, density, boiling_point_degc_test_pred, boiling_point_degc_opera_pred, biodegradation_half_life_days, " +
//            "bioconcentration_factor_test_pred, bioconcentration_factor_opera_pred, atmospheric_hydroxylation_rate, " +
//            "ames_mutagenicity_test_pred, descriptor_string_tsv, " +
//            "expocat, nhanes, toxval_data, expocat_median_prediction, synonyms, pc_code " +
//            "      from ccd_app.vw_chemical_search_long \n" +
//            "      where modified_value ~* :inchikeys \n" +
//            "        and search_name in ('InChIKey', 'Indigo InChIKey')\n" +
//            "      group by id, search_name, search_group, search_value, modified_value, rank, dtxsid, dtxcid, casrn, compound_id, " +
//            "generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, monoisotopic_mass, percent_assays, " +
//            "pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays, " +
//            "toxcast_select, pubchem_cid, mol_file, mrv_file, related_substance_count, related_structure_count, has_structure_image, " +
//            "iupac_name, smiles, inchi_string, average_mass, inchikey, qc_notes, ms_ready_smiles, qsar_ready_smiles, iris_link, " +
//            "pprtv_link, wikipedia_article, water_solubility_test, water_solubility_opera, viscosity_cp_cp_test_pred, " +
//            "vapor_pressure_mmhg_test_pred, vapor_pressure_mmhg_opera_pred, thermal_conductivity, tetrahymena_pyriformis, " +
//            "surface_tension, soil_adsorption_coefficient, oral_rat_ld50_mol, opera_km_days_opera_pred, octanol_water_partition, " +
//            "octanol_air_partition_coeff, melting_point_degc_test_pred, melting_point_degc_opera_pred, hr_fathead_minnow, " +
//            "hr_diphnia_lc50, henrys_law_atm, flash_point_degc_test_pred, devtox_test_pred, density, boiling_point_degc_test_pred, " +
//            "boiling_point_degc_opera_pred, biodegradation_half_life_days, bioconcentration_factor_test_pred, bioconcentration_factor_opera_pred, " +
//            "atmospheric_hydroxylation_rate, ames_mutagenicity_test_pred, descriptor_string_tsv, " +
//            "expocat, nhanes, toxval_data, expocat_median_prediction, synonyms, pc_code " +
//            "      order by modified_value\n" +
//            "     ) x\n" +
//            "where rnk = 1\n" +
//            "order by inchikey", nativeQuery = true)
    @Query(value = "select  row_number() over (order by dtxsid) as id, 'InChIKey Skeleton/InChIKey' as search_name, 'InChIKey' as search_value, d.inchikey as modified_value, 'InChIKey' as search_group, 0 as rank,\n" +
            " dtxsid, dtxcid, casrn, compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, monoisotopic_mass, mol_wgt, percent_assays, " +
            "pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays, toxcast_select, pubchem_cid, mol_file, " +
            "mrv_file, related_substance_count, related_structure_count, mol_image, has_structure_image, iupac_name, smiles, inchi_string, average_mass, inchikey, " +
            "qc_notes, ms_ready_smiles, qsar_ready_smiles, iris_link, pprtv_link, wikipedia_article, expocat_median_prediction, expocat, nhanes, toxval_data, " +
            "water_solubility_test, water_solubility_opera, viscosity_cp_cp_test_pred, vapor_pressure_mmhg_test_pred, vapor_pressure_mmhg_opera_pred, " +
            "thermal_conductivity, tetrahymena_pyriformis, surface_tension, soil_adsorption_coefficient, oral_rat_ld50_mol, opera_km_days_opera_pred, " +
            "octanol_water_partition, octanol_air_partition_coeff, melting_point_degc_test_pred, melting_point_degc_opera_pred, hr_fathead_minnow, hr_diphnia_lc50, " +
            "henrys_law_atm, flash_point_degc_test_pred, devtox_test_pred, density, boiling_point_degc_test_pred, boiling_point_degc_opera_pred, " +
            "biodegradation_half_life_days, bioconcentration_factor_test_pred, bioconcentration_factor_opera_pred, atmospheric_hydroxylation_rate, " +
            "pkaa_opera_pred, pkab_opera_pred, ames_mutagenicity_test_pred, descriptor_string_tsv, '' as synonyms, '' as pc_code,logd5_5, logd7_4, ready_bio_deg\n" +
            "from {h-schema}chemical_details d where d.dtxsid in (\n" +
            "select distinct s.dtxsid from {h-schema}search_chemical s  where s.modified_value ~* :inchikeys\n" +
            "        and s.search_name in ('InChIKey', 'Indigo InChIKey')) order by inchikey", nativeQuery = true)
    List<SearchWithChemicalDetails> startWithInChIKeySkeleton(@Param("inchikeys") String inchikeys);

    // -- for results endpoint
//    @Query(value = "Select id, dtxsid,\n" +
//            "       case when lag(modified_value) over (partition by modified_value) is not null OR lead(modified_value) over (partition by modified_value) is not null then search_name || ' - WARNING: Synonym mapped to two or more chemicals' else search_name end as search_name,\n" +
//            "       search_group, search_value, modified_value, rank, dtxcid, casrn, compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, monoisotopic_mass, percent_assays, pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays, toxcast_select, pubchem_cid, related_substance_count, related_structure_count, iupac_name, smiles, inchi_string, average_mass, inchikey, has_structure_image\n" +
//            " from (select row_number() over (partition by modified_value, dtxsid order by rank asc) as rnk, id, dtxsid, rank, search_name, search_group, search_value, modified_value, dtxcid, casrn, compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, monoisotopic_mass, percent_assays, pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays, toxcast_select, pubchem_cid, related_substance_count, related_structure_count, iupac_name, smiles, inchi_string, average_mass, inchikey, has_structure_image\n" +
//            "               from ccd_app.vw_chemical_search c where c.modified_value in (:searchWords) and c.search_name in (:searchMatchToInclude) \n" +
//            "               group by id, dtxsid, search_name, search_group, search_value, modified_value, rank, dtxcid, casrn, compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, monoisotopic_mass, percent_assays, pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays, toxcast_select, pubchem_cid, related_substance_count, related_structure_count, iupac_name, smiles, inchi_string, average_mass, inchikey, has_structure_image\n" +
//            "                order by modified_value\n" +
//            "              ) X Where rnk=1\n" +
//            " order by modified_value", nativeQuery = true)
//    @Query(value = "Select id, dtxsid,\n" +
//            "       case\n" +
//            "           when lag(modified_value) over (partition by modified_value) is not null OR\n" +
//            "                lead(modified_value) over (partition by modified_value) is not null\n" +
//            "               then search_name || ' - WARNING: Synonym mapped to two or more chemicals'\n" +
//            "           else search_name end as search_name,\n" +
//            "search_group, search_value, modified_value, rank, dtxcid, casrn, compound_id, generic_substance_id, preferred_name, " +
//            "active_assays, cpdata_count, mol_formula, monoisotopic_mass, percent_assays, pubchem_count, pubmed_count, " +
//            "sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays, toxcast_select, pubchem_cid, " +
//            "mol_file, mrv_file, related_substance_count, related_structure_count, has_structure_image, iupac_name, " +
//            "smiles, inchi_string, average_mass, inchikey, qc_notes, ms_ready_smiles, qsar_ready_smiles, iris_link, pprtv_link, " +
//            "wikipedia_article, water_solubility_test, water_solubility_opera, viscosity_cp_cp_test_pred, vapor_pressure_mmhg_test_pred, " +
//            "vapor_pressure_mmhg_opera_pred, thermal_conductivity, tetrahymena_pyriformis, surface_tension, soil_adsorption_coefficient, " +
//            "oral_rat_ld50_mol, opera_km_days_opera_pred, octanol_water_partition, octanol_air_partition_coeff, melting_point_degc_test_pred, " +
//            "melting_point_degc_opera_pred, hr_fathead_minnow, hr_diphnia_lc50, henrys_law_atm, flash_point_degc_test_pred, devtox_test_pred, " +
//            "density, boiling_point_degc_test_pred, boiling_point_degc_opera_pred, biodegradation_half_life_days, bioconcentration_factor_test_pred, " +
//            "bioconcentration_factor_opera_pred, atmospheric_hydroxylation_rate, ames_mutagenicity_test_pred, descriptor_string_tsv," +
//            "expocat, nhanes, toxval_data, expocat_median_prediction, synonyms, pc_code " +
//            "from (select row_number() over (partition by modified_value, dtxsid order by rank asc) as rnk,\n" +
//            "id, search_name, search_group, search_value, modified_value, rank, dtxsid, dtxcid, casrn, compound_id, generic_substance_id, " +
//            "preferred_name, active_assays, cpdata_count, mol_formula, monoisotopic_mass, percent_assays, pubchem_count, " +
//            "pubmed_count, sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays, toxcast_select, " +
//            "pubchem_cid, mol_file, mrv_file, related_substance_count, related_structure_count, has_structure_image, iupac_name, " +
//            "smiles, inchi_string, average_mass, inchikey, qc_notes, ms_ready_smiles, qsar_ready_smiles, iris_link, pprtv_link, " +
//            "wikipedia_article, water_solubility_test, water_solubility_opera, viscosity_cp_cp_test_pred, vapor_pressure_mmhg_test_pred, " +
//            "vapor_pressure_mmhg_opera_pred, thermal_conductivity, tetrahymena_pyriformis, surface_tension, soil_adsorption_coefficient, " +
//            "oral_rat_ld50_mol, opera_km_days_opera_pred, octanol_water_partition, octanol_air_partition_coeff, melting_point_degc_test_pred, " +
//            "melting_point_degc_opera_pred, hr_fathead_minnow, hr_diphnia_lc50, henrys_law_atm, flash_point_degc_test_pred, devtox_test_pred, " +
//            "density, boiling_point_degc_test_pred, boiling_point_degc_opera_pred, biodegradation_half_life_days, bioconcentration_factor_test_pred, " +
//            "bioconcentration_factor_opera_pred, atmospheric_hydroxylation_rate, ames_mutagenicity_test_pred, descriptor_string_tsv," +
//            "expocat, nhanes, toxval_data, expocat_median_prediction, synonyms, pc_code " +
//            "      from ccd_app.vw_chemical_search_long c\n" +
//            "      where c.modified_value in (:searchWords)\n" +
//            "        and c.search_name in (:searchMatchToInclude)\n" +
//            "      group by id, search_name, search_group, search_value, modified_value, rank, dtxsid, dtxcid, casrn, compound_id, " +
//            "generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, monoisotopic_mass, percent_assays, " +
//            "pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays, " +
//            "toxcast_select, pubchem_cid, mol_file, mrv_file, related_substance_count, related_structure_count, has_structure_image, " +
//            "iupac_name, smiles, inchi_string, average_mass, inchikey, qc_notes, ms_ready_smiles, qsar_ready_smiles, iris_link, " +
//            "pprtv_link, wikipedia_article, water_solubility_test, water_solubility_opera, viscosity_cp_cp_test_pred, vapor_pressure_mmhg_test_pred, " +
//            "vapor_pressure_mmhg_opera_pred, thermal_conductivity, tetrahymena_pyriformis, surface_tension, soil_adsorption_coefficient, " +
//            "oral_rat_ld50_mol, opera_km_days_opera_pred, octanol_water_partition, octanol_air_partition_coeff, melting_point_degc_test_pred, " +
//            "melting_point_degc_opera_pred, hr_fathead_minnow, hr_diphnia_lc50, henrys_law_atm, flash_point_degc_test_pred, devtox_test_pred, " +
//            "density, boiling_point_degc_test_pred, boiling_point_degc_opera_pred, biodegradation_half_life_days, bioconcentration_factor_test_pred, " +
//            "bioconcentration_factor_opera_pred, atmospheric_hydroxylation_rate, ames_mutagenicity_test_pred, descriptor_string_tsv," +
//            "expocat, nhanes, toxval_data, expocat_median_prediction, synonyms, pc_code " +
//            "      order by modified_value\n" +
//            "     ) X\n" +
//            "Where rnk = 1", nativeQuery = true)
    @Query(value = "select s.id, d.dtxsid,\n" +
            " case\n" +
            "    when lag(modified_value) over (partition by modified_value) is not null OR\n" +
            "    lead(modified_value) over (partition by modified_value) is not null\n" +
            "    then search_name || ' - WARNING: Synonym mapped to two or more chemicals'\n" +
            "    else search_name\n" +
            " end as search_name,\n" +
            " search_group, search_value, modified_value, rank, d.dtxcid, casrn, compound_id, generic_substance_id, preferred_name,\n" +
            " active_assays, cpdata_count, mol_formula, monoisotopic_mass, mol_wgt, percent_assays, pubchem_count, pubmed_count,\n" +
            " sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays, toxcast_select, pubchem_cid,\n" +
            " mol_file, mrv_file, related_substance_count, related_structure_count, has_structure_image, iupac_name,\n" +
            " smiles, inchi_string, average_mass, inchikey, qc_notes, ms_ready_smiles, qsar_ready_smiles, iris_link, pprtv_link,\n" +
            " wikipedia_article, water_solubility_test, water_solubility_opera, viscosity_cp_cp_test_pred, vapor_pressure_mmhg_test_pred,\n" +
            " vapor_pressure_mmhg_opera_pred, thermal_conductivity, tetrahymena_pyriformis, surface_tension, soil_adsorption_coefficient,\n" +
            " oral_rat_ld50_mol, opera_km_days_opera_pred, octanol_water_partition, octanol_air_partition_coeff, melting_point_degc_test_pred,\n" +
            " melting_point_degc_opera_pred, hr_fathead_minnow, hr_diphnia_lc50, henrys_law_atm, flash_point_degc_test_pred, devtox_test_pred,\n" +
            " density, boiling_point_degc_test_pred, boiling_point_degc_opera_pred, biodegradation_half_life_days, bioconcentration_factor_test_pred,\n" +
            " bioconcentration_factor_opera_pred, atmospheric_hydroxylation_rate, ames_mutagenicity_test_pred, descriptor_string_tsv,\n" +
            " expocat, nhanes, toxval_data, expocat_median_prediction, '' as synonyms, '' as pc_code, pkaa_opera_pred, pkab_opera_pred, logd5_5, logd7_4, ready_bio_deg \n" +
            " from (select row_number() over (partition by modified_value, dtxsid order by rank asc) as rnk, id, search_name, modified_value, dtxsid, dtxcid, search_group, search_value, rank " +
            " from {h-schema}search_chemical c " +
            " where c.search_name in (:searchMatchToInclude)\n" +
            " and c.modified_value in (:searchWords) \n" +
            " ) s join {h-schema}chemical_details d on s.dtxsid = d.dtxsid" +
            " where s.rnk = 1 order by search_value", nativeQuery = true)
    List<SearchWithChemicalDetails> getIdentifierResult(@Param("searchWords") Collection<String> searchWords, @Param("searchMatchToInclude") List<String> searchMatchToInclude);

    // @Query(value = "from SearchWithChemicalDetails where searchMatch in (:searchNames) and modifiedValue in :searchWords ")
    @Query(value = "select  row_number() over (order by dtxsid) as id, '' as search_name, '' as search_value, d.inchikey as modified_value, '' as search_group, 0 as rank,\n" +
            " dtxsid, dtxcid, casrn, compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, monoisotopic_mass, mol_wgt, " +
            " percent_assays, pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays, " +
            "toxcast_select, pubchem_cid, mol_file, mrv_file, related_substance_count, related_structure_count, mol_image, has_structure_image, " +
            "iupac_name, smiles, inchi_string, average_mass, inchikey, qc_notes, ms_ready_smiles, qsar_ready_smiles, iris_link, pprtv_link, wikipedia_article, " +
            "expocat_median_prediction, expocat, nhanes, toxval_data, water_solubility_test, water_solubility_opera, viscosity_cp_cp_test_pred, " +
            "vapor_pressure_mmhg_test_pred, vapor_pressure_mmhg_opera_pred, thermal_conductivity, tetrahymena_pyriformis, surface_tension, soil_adsorption_coefficient, " +
            "oral_rat_ld50_mol, opera_km_days_opera_pred, octanol_water_partition, octanol_air_partition_coeff, melting_point_degc_test_pred, " +
            "melting_point_degc_opera_pred, hr_fathead_minnow, hr_diphnia_lc50, henrys_law_atm, flash_point_degc_test_pred, devtox_test_pred, density, " +
            "boiling_point_degc_test_pred, boiling_point_degc_opera_pred, biodegradation_half_life_days, bioconcentration_factor_test_pred, " +
            "bioconcentration_factor_opera_pred, atmospheric_hydroxylation_rate, ames_mutagenicity_test_pred, descriptor_string_tsv, '' as synonyms, '' as pc_code, pkaa_opera_pred, pkab_opera_pred \n" +
            "from {h-schema}chemical_details d where d.dtxsid in (\n" +
            "select distinct s.dtxsid from {h-schema}search_chemical s  where s.modified_value in (:searchWords)\n" +
            "        and s.search_name in (:searchNames))", nativeQuery = true)
    List<SearchWithChemicalDetails> getChemicaDetails(@Param("searchWords") Collection<String> searchWords, @Param("searchNames") List<String> searchNames);

    @Query(value = "select  row_number() over (order by dtxsid) as id, 'DSSTox_Compound_Id' as search_name, dtxcid as search_value, dtxcid as modified_value, 'DTXCID' as search_group, 0 as rank,\n" +
            " dtxsid, dtxcid, casrn, compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, monoisotopic_mass, mol_wgt, " +
            " percent_assays, pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays, " +
            "toxcast_select, pubchem_cid, mol_file, mrv_file, related_substance_count, related_structure_count, mol_image, has_structure_image, " +
            "iupac_name, smiles, inchi_string, average_mass, inchikey, qc_notes, ms_ready_smiles, qsar_ready_smiles, iris_link, pprtv_link, wikipedia_article, " +
            "expocat_median_prediction, expocat, nhanes, toxval_data, water_solubility_test, water_solubility_opera, viscosity_cp_cp_test_pred, " +
            "vapor_pressure_mmhg_test_pred, vapor_pressure_mmhg_opera_pred, thermal_conductivity, tetrahymena_pyriformis, surface_tension, soil_adsorption_coefficient, " +
            "oral_rat_ld50_mol, opera_km_days_opera_pred, octanol_water_partition, octanol_air_partition_coeff, melting_point_degc_test_pred, " +
            "melting_point_degc_opera_pred, hr_fathead_minnow, hr_diphnia_lc50, henrys_law_atm, flash_point_degc_test_pred, devtox_test_pred, density, " +
            "boiling_point_degc_test_pred, boiling_point_degc_opera_pred, biodegradation_half_life_days, bioconcentration_factor_test_pred, " +
            "bioconcentration_factor_opera_pred, atmospheric_hydroxylation_rate, ames_mutagenicity_test_pred, descriptor_string_tsv, '' as synonyms, '' as pc_code, pkaa_opera_pred, pkab_opera_pred,logd5_5, logd7_4, ready_bio_deg \n" +
            "from {h-schema}chemical_details d where d.dtxcid in :dtxcids ", nativeQuery = true)
    List<SearchWithChemicalDetails> searchDtxcid(@Param("dtxcids") List<String> dtxcids);


//    @Query(value = "select new gov.epa.ccte.api.batchsearch.domain.ChemicalDetailsDto (dtxsid, false, dtxcid), generic_substance_id, casrn, preferred_name, compound_id," +
//            "       stereo,isotope, multicomponent, pubchem_count,pubmed_count, sources_count,cpdata_count,active_assays," +
//            "       total_assays, percent_assays,toxcast_select,monoisotopic_mass,mol_formula, qc_level,qc_level_desc, " +
//            "       pubchem_cid,has_structure_image,related_substance_count,related_structure_count,iupac_name,smiles,inchikey,average_mass)" +
//            " from ccd_app.vw_chemical_search where search_name = 'DSSTox_Compound_Id' and modified_value in :dtxcids",
//            nativeQuery = true)
//    List<SearchWithChemicalDetails> getDtxcids(@Param("dtxcids") Collection<String> dtxcids);

//    @Query(value = "select row_number() over (order by dtxsid) as id, '' as search_name, '' as search_value, '' as modified_value, 0 as rank, " +
//            " dtxsid, dtxcid, casrn, compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, " +
//            " monoisotopic_mass, percent_assays, pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, " +
//            " isotope, multicomponent, total_assays, toxcast_select, pubchem_cid, related_substance_count, related_structure_count, " +
//            " iupac_name, smiles, inchi_string, average_mass, inchikey, has_structure_image " +
//            " from ccd_app.vw_chemical_details where dtxsid <> 'DTXSID00000000' and monoisotopic_mass between :start and :end ", nativeQuery = true)

    @Query(value = "select row_number() over (order by dtxsid) as id, '' as search_name, '' as search_value, '' as modified_value,'' as search_group, 0 as rank,\n" +
            "dtxsid, dtxcid, casrn, compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, " +
            "monoisotopic_mass, mol_wgt, percent_assays, pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, " +
            "isotope, multicomponent, total_assays, toxcast_select, pubchem_cid, mol_file, mrv_file, related_substance_count, " +
            "related_structure_count, has_structure_image, iupac_name, smiles, inchi_string, average_mass, inchikey, qc_notes, " +
            "ms_ready_smiles, qsar_ready_smiles, iris_link, pprtv_link, wikipedia_article, water_solubility_test, water_solubility_opera," +
            "viscosity_cp_cp_test_pred, vapor_pressure_mmhg_test_pred, vapor_pressure_mmhg_opera_pred, thermal_conductivity, " +
            "tetrahymena_pyriformis, surface_tension, soil_adsorption_coefficient, oral_rat_ld50_mol, opera_km_days_opera_pred, " +
            "octanol_water_partition, octanol_air_partition_coeff, melting_point_degc_test_pred, melting_point_degc_opera_pred, " +
            "hr_fathead_minnow, hr_diphnia_lc50, henrys_law_atm, flash_point_degc_test_pred, devtox_test_pred, density, " +
            "boiling_point_degc_test_pred, boiling_point_degc_opera_pred, biodegradation_half_life_days, bioconcentration_factor_test_pred, " +
            "bioconcentration_factor_opera_pred, atmospheric_hydroxylation_rate, ames_mutagenicity_test_pred, descriptor_string_tsv, " +
            "expocat, nhanes, toxval_data, expocat_median_prediction, '' as synonyms, '' as pc_code, pkaa_opera_pred, pkab_opera_pred,logd5_5, logd7_4, ready_bio_deg " +
            " from {h-schema}chemical_details where dtxsid is not null and monoisotopic_mass between :start and :end", nativeQuery = true)
    List<SearchWithChemicalDetails> getChemicalForMassRange(@Param("start") Double start, @Param("end") Double end);

//    @Query(value = "select row_number() over (order by dtxsid) as id, '' as search_name, '' as search_value, '' as modified_value, 0 as rank, " +
//            " dtxsid, dtxcid, casrn, compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, " +
//            " monoisotopic_mass, percent_assays, pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, " +
//            " isotope, multicomponent, total_assays, toxcast_select, pubchem_cid, related_substance_count, related_structure_count, " +
//            " iupac_name, smiles, inchi_string, average_mass, inchikey, has_structure_image " +
//            " from ccd_app.vw_chemical_details where dtxsid <> 'DTXSID00000000' and mol_formula in :formulas ", nativeQuery = true)

    @Query(value = "select row_number() over (order by dtxsid) as id, 'Exact Formula' as search_name, '' as search_value, '' as modified_value, '' as search_group, 0 as rank,\n" +
            "dtxsid, dtxcid, casrn, compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, " +
            "monoisotopic_mass, mol_wgt, percent_assays, pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, " +
            "isotope, multicomponent, total_assays, toxcast_select, pubchem_cid, mol_file, mrv_file, related_substance_count, " +
            "related_structure_count, has_structure_image, iupac_name, smiles, inchi_string, average_mass, inchikey, qc_notes, " +
            "ms_ready_smiles, qsar_ready_smiles, iris_link, pprtv_link, wikipedia_article, water_solubility_test, water_solubility_opera," +
            "viscosity_cp_cp_test_pred, vapor_pressure_mmhg_test_pred, vapor_pressure_mmhg_opera_pred, thermal_conductivity, " +
            "tetrahymena_pyriformis, surface_tension, soil_adsorption_coefficient, oral_rat_ld50_mol, opera_km_days_opera_pred, " +
            "octanol_water_partition, octanol_air_partition_coeff, melting_point_degc_test_pred, melting_point_degc_opera_pred, " +
            "hr_fathead_minnow, hr_diphnia_lc50, henrys_law_atm, flash_point_degc_test_pred, devtox_test_pred, density, " +
            "boiling_point_degc_test_pred, boiling_point_degc_opera_pred, biodegradation_half_life_days, bioconcentration_factor_test_pred, " +
            "bioconcentration_factor_opera_pred, atmospheric_hydroxylation_rate, ames_mutagenicity_test_pred, descriptor_string_tsv, " +
            "expocat, nhanes, toxval_data, expocat_median_prediction, '' as synonyms, '' as pc_code, pkaa_opera_pred, pkab_opera_pred,logd5_5, logd7_4, ready_bio_deg " +
            " from {h-schema}chemical_details where dtxsid is not null and mol_formula in :formulas order by mol_formula", nativeQuery = true)
    List<SearchWithChemicalDetails> getChemicalsForFormula(@Param("formulas") String[] formulas);

//    @Query(value = "select row_number() over (order by dtxsid) as id, '' as search_name, '' as search_value, '' as modified_value, 0 as rank, " +
//            " dtxsid, dtxcid, casrn, compound_id, generic_substance_id, preferred_name, active_assays, cpdata_count, mol_formula, " +
//            " monoisotopic_mass, percent_assays, pubchem_count, pubmed_count, sources_count, qc_level, qc_level_desc, stereo, " +
//            " isotope, multicomponent, total_assays, toxcast_select, pubchem_cid, related_substance_count, related_structure_count, " +
//            " iupac_name, smiles, inchi_string, average_mass, inchikey, has_structure_image from ccd_app.vw_chemical_details " +
//            " where dtxsid in ( Select distinct r.dtxsid from ccd_app.search_ms_ready r where r.mol_formula in :formulas )", nativeQuery = true)

    @Query(value = "select * from  (\n" +
            "select row_number() over (  order by c.dtxsid) as id,\n" +
            "       row_number() over ( partition by c.dtxsid  order by c.dtxsid) as rn ,\n" +
            "       'MS Ready Formula' as search_name,\n" +
            "       r.mol_formula as search_value ,\n" +
            "       '' as modified_value, '' as search_group, 0 as rank,\n" +
            "       c.dtxsid, c.dtxcid, casrn, compound_id, generic_substance_id, preferred_name, active_assays,\n" +
            "       cpdata_count, c.mol_formula, monoisotopic_mass, mol_wgt, percent_assays, pubchem_count, pubmed_count,\n" +
            "       sources_count, qc_level, qc_level_desc, stereo, isotope, multicomponent, total_assays,\n" +
            "       toxcast_select, pubchem_cid, mol_file, mrv_file, related_substance_count, related_structure_count,\n" +
            "       has_structure_image, iupac_name, smiles, inchi_string, average_mass, inchikey, qc_notes,\n" +
            "       ms_ready_smiles, qsar_ready_smiles, iris_link, pprtv_link, wikipedia_article, water_solubility_test,\n" +
            "       water_solubility_opera, viscosity_cp_cp_test_pred, vapor_pressure_mmhg_test_pred, vapor_pressure_mmhg_opera_pred,\n" +
            "       thermal_conductivity, tetrahymena_pyriformis, surface_tension, soil_adsorption_coefficient, oral_rat_ld50_mol,\n" +
            "       opera_km_days_opera_pred, octanol_water_partition, octanol_air_partition_coeff, melting_point_degc_test_pred,\n" +
            "       melting_point_degc_opera_pred, hr_fathead_minnow, hr_diphnia_lc50, henrys_law_atm, flash_point_degc_test_pred,\n" +
            "       devtox_test_pred, density, boiling_point_degc_test_pred, boiling_point_degc_opera_pred, biodegradation_half_life_days,\n" +
            "       bioconcentration_factor_test_pred, bioconcentration_factor_opera_pred, atmospheric_hydroxylation_rate, ames_mutagenicity_test_pred,\n" +
            "       descriptor_string_tsv, expocat, nhanes, toxval_data, expocat_median_prediction, '' as synonyms, '' as pc_code, pkaa_opera_pred, pkab_opera_pred,logd5_5, logd7_4, ready_bio_deg\n" +
            "from {h-schema}chemical_details c\n" +
            "     inner join  {h-schema}search_ms_ready r\n" +
            "           on c.dtxsid = r.dtxsid\n" +
            "where  r.mol_formula in  :formulas\n" +
            "order by r.mol_formula\n" +
            " ) x\n" +
            " where  rn = 1 ", nativeQuery = true)
    List<SearchWithChemicalDetails> getChemicalsForMsReadyFormula(@Param("formulas") String[] formulas);
}
