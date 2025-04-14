package gov.epa.ccte.api.batchsearch.repository;

import gov.epa.ccte.api.batchsearch.domain.ChemicalDetails;
import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;
import java.util.List;

@RepositoryRestResource(exported = false)
public interface ChemicalDetailsRepository extends JpaRepository<ChemicalDetails, String> {

    @Query(value = "select d.h_chem_hash_key, d.dtxsid, d.dtxcid, d.generic_substance_id, d.casrn, d.preferred_name, d.compound_id, d.stereo, d.isotope, d.multicomponent, d.pubchem_count, d.pubmed_count, d.sources_count, d.cpdata_count, d.active_assays, d.total_assays, d.percent_assays, d.toxcast_select, d.monoisotopic_mass, d.mol_wgt, d.mol_formula,\n" +
            "       d.qc_level, d.qc_level_desc, d.pubchem_cid, d.has_structure_image, d.related_substance_count, d.related_structure_count, d.iupac_name, d.smiles, d.inchi_string, d.inchikey, d.average_mass\n" +
            "from {h-schema}chemical_details d\n" +
            "where d.dtxsid in (\n" +
            "    select distinct s.dtxsid from {h-schema}search_chemical s where s.search_name in (:searchNames) " +
            " and s.modified_value in (:searchWords))", nativeQuery = true)
    List<ChemicalDetails> searchChemicals(@Param("searchWords") Collection<String> searchWords, @Param("searchNames") List<String> searchNames);

    @Query(value = "select d.h_chem_hash_key, d.dtxsid, d.dtxcid, d.generic_substance_id, d.casrn, d.preferred_name, d.compound_id, d.stereo, d.isotope, d.multicomponent, d.pubchem_count, d.pubmed_count, d.sources_count, d.cpdata_count, d.active_assays, d.total_assays, d.percent_assays, d.toxcast_select, d.monoisotopic_mass, d.mol_wgt, d.mol_formula,\n" +
            "       d.qc_level, d.qc_level_desc, d.pubchem_cid, d.has_structure_image, d.related_substance_count, d.related_structure_count, d.iupac_name, d.smiles, d.inchi_string, d.inchikey, d.average_mass\n" +
            "from {h-schema}chemical_details d\n" +
            "where d.dtxcid in (\n" +
            "    select distinct s.dtxcid from {h-schema}search_chemical s where s.search_name = 'DSSTox_Compound_Id'" +
            " and s.modified_value in (:searchWords))", nativeQuery = true)
    List<ChemicalDetails> searchDtxcid(@Param("searchWords") Collection<String> searchWords);

    @Query(value = "select d.h_chem_hash_key, d.dtxsid, d.dtxcid, d.generic_substance_id, d.casrn, d.preferred_name, d.compound_id, d.stereo, d.isotope, d.multicomponent, d.pubchem_count, d.pubmed_count, d.sources_count, d.cpdata_count, d.active_assays, d.total_assays, d.percent_assays, d.toxcast_select, d.monoisotopic_mass, d.mol_wgt, d.mol_formula,\n" +
            "       d.qc_level, d.qc_level_desc, d.pubchem_cid, d.has_structure_image, d.related_substance_count, d.related_structure_count, d.iupac_name, d.smiles, d.inchi_string, d.inchikey, d.average_mass\n" +
            "from {h-schema}chemical_details d\n" +
            "where d.dtxsid is not null and d.dtxsid <> 'DTXSID00000000' and d.mol_formula in (:formulas)", nativeQuery = true)
    List<ChemicalDetails> searchFormulas(@Param("formulas") String[] formulas);

    @Query(value = "select d.h_chem_hash_key, d.dtxsid, d.dtxcid, d.generic_substance_id, d.casrn, d.preferred_name, d.compound_id, d.stereo, d.isotope, d.multicomponent, d.pubchem_count, d.pubmed_count, d.sources_count, d.cpdata_count, d.active_assays, d.total_assays, d.percent_assays, d.toxcast_select, d.monoisotopic_mass, d.mol_wgt, d.mol_formula,\n" +
            "       d.qc_level, d.qc_level_desc, d.pubchem_cid, d.has_structure_image, d.related_substance_count, d.related_structure_count, d.iupac_name, d.smiles, d.inchi_string, d.inchikey, d.average_mass\n" +
            "from {h-schema}chemical_details d\n" +
            "where d.dtxsid in (" +
            " select distinct r.dtxsid from ccd_app.search_ms_ready r where r.mol_formula in :formulas )\n", nativeQuery = true)
    List<ChemicalDetails> searchMsReadyFormula(@Param("formulas") String[] formulas);

    //
    @Query(value = " select d.h_chem_hash_key, d.dtxsid, d.dtxcid, d.generic_substance_id, d.casrn, d.preferred_name, d.compound_id, d.stereo, d.isotope, d.multicomponent, d.pubchem_count, d.pubmed_count, d.sources_count, d.cpdata_count, d.active_assays, d.total_assays, d.percent_assays, d.toxcast_select, d.monoisotopic_mass, d.mol_wgt, d.mol_formula,\n" +
            "       d.qc_level, d.qc_level_desc, d.pubchem_cid, d.has_structure_image, d.related_substance_count, d.related_structure_count, d.iupac_name, d.smiles, d.inchi_string, d.inchikey, d.average_mass\n" +
            " from {h-schema}chemical_details d\n" +
            " where d.dtxsid is not null and d.inchikey ~* :inchikeys ", nativeQuery = true)
    List<ChemicalDetails> startWithInChIKeySkeleton(@Param("inchikeys") String inchikeys);


    @Query(value="select * from {h-schema}chemical_details where 1=0", nativeQuery = true)
    List<ChemicalDetails> testDbConnection();

}
