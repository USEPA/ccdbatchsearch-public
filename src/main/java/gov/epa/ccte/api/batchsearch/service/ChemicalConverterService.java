package gov.epa.ccte.api.batchsearch.service;

import com.epam.indigo.Indigo;
import com.epam.indigo.IndigoInchi;
import com.epam.indigo.IndigoObject;
import gov.epa.ccte.api.batchsearch.web.rest.errors.InChiKeyGenerationErrorException;
import gov.epa.ccte.api.batchsearch.web.rest.errors.MrvFileBadFormateErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;

@Slf4j
@SuppressWarnings("unused")
@Service
public class ChemicalConverterService {

    private final Indigo indigo = new Indigo();
    private IndigoObject indigoObject;

    public MolV2000 convert(MolV3000 molV3000) {
        log.debug("convert molv3000 to molv2000");

        indigo.setOption("molfile-saving-mode", "2000");

        indigoObject = indigo.loadMolecule(molV3000.getContent());

        MolV2000 molV2000 = new MolV2000(indigoObject.molfile());

        log.debug("converted molv2000 = {} ", molV2000);

        return molV2000;
    }

    public MolV3000 convert(MolV2000 molV2000) {
        log.debug("convert molv2000 to Molv3000");

        indigo.setOption("molfile-saving-mode", "3000");

        indigoObject = indigo.loadMolecule(molV2000.getContent());

        MolV3000 molV3000 = new MolV3000(indigoObject.molfile());

        log.debug("converted molv3000 = {} ", molV3000);

        return molV3000;
    }

    public MolV3000 convert(Smiles smiles) {
        log.debug("convert SMILES to Molv3000");

        indigo.setOption("molfile-saving-mode", "3000");

        indigoObject = indigo.loadMolecule(smiles.getContent());

        MolV3000 molV3000 = new MolV3000(indigoObject.molfile());

        log.debug("converted molv3000 = {} ", molV3000);

        return molV3000;

    }

    public boolean validate(MolV3000 molV3000) {
        log.debug("validate = {} ", molV3000.getContent());

        String[] lines = molV3000.getContent().split("\n");
        String forthLine = lines[3];

        Boolean flag = forthLine.contains("V3000");

        log.debug("validate result = {} ", flag);

        return flag;
    }

    public boolean validate(MolV2000 molV2000) {
        log.debug("validate = {} ", molV2000.getContent());

        String[] lines = molV2000.getContent().split("\n");
        String forthLine = lines[3];

        //Boolean flag = forthLine.endsWith("V2000");
        Boolean flag = forthLine.contains("V2000");

        log.debug("validate result = {} ", flag);

        return flag;
    }

    public boolean validate(Smiles smiles) {
        log.debug("valdating SMILES = {} ", smiles.getContent());

        boolean flag = smiles.getContent().matches("^([^J][A-Za-z0-9@+\\-\\[\\]\\(\\)\\\\\\/%=#$]+)$");

        log.debug("validate result = {} ", flag);

        return flag;
    }

    public InChiKey getInChiKey(MolV3000 molV3000) {
        log.debug("MolV3000 = {}", molV3000.getContent());

        try {
            indigoObject = indigo.loadMolecule(molV3000.getContent());
            IndigoInchi indigoInchi = new IndigoInchi(indigo);

            String inchi = indigoInchi.getInchi(indigoObject);
            String inchikey = indigoInchi.getInchiKey(inchi);
            inchikey = inchikey.replaceFirst("InChIKey=", "");

            log.debug("new InChIKey= {} ", inchikey);

            return new InChiKey(inchikey);

        } catch (Exception e) {
            log.error("Exception called from Indigo = {} ", e.getMessage());
            throw new InChiKeyGenerationErrorException();
        }
    }

    public boolean validate(MrvFile mrvFile) {
        log.debug("ChemicalConverterService - validate - MrvFile ={} ", mrvFile);

        // checking against XML schema -
        Resource mrvSchema = new ClassPathResource("mrvSchema_6_0_0.xsd");

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            factory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);

            Schema schema = factory.newSchema(mrvSchema.getFile());

            Validator validator = schema.newValidator();

            StringReader reader = new StringReader(mrvFile.getContent());

            validator.validate(new StreamSource(reader));

            return true;

        } catch (SAXNotRecognizedException e) {
            return handleMrvError(e);

        } catch (SAXNotSupportedException e) {
            return handleMrvError(e);
        } catch (SAXException e) {
            return handleMrvError(e);
        } catch (IOException e) {
            return handleMrvError(e);
        }
    }

    private boolean handleMrvError(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        throw new MrvFileBadFormateErrorException();
    }

    public static class InChiKey {
        private final String inchikey;

        public InChiKey(String inchikey) {
            this.inchikey = inchikey;
        }

        public String getContent() {
            return this.inchikey;
        }
    }

    public static class MolV2000 {
        private final String molFileContent;

        public MolV2000(String molFileContent) {
            this.molFileContent = molFileContent;
        }

        public String getContent() {
            return this.molFileContent;
        }
    }

    public static class MolV3000 {
        private final String molFileContent;

        public MolV3000(String molFileContent) {
            this.molFileContent = molFileContent;
        }

        public String getContent() {
            return this.molFileContent;
        }
    }

    public static class Smiles {
        private final String smilesString;

        public Smiles(String smilesString) {
            this.smilesString = smilesString;
        }

        public String getContent() {
            return this.smilesString;
        }
    }

    public static class MrvFile {
        private final String mrvFileContent;


        public MrvFile(String mrvFileContent) {
            this.mrvFileContent = mrvFileContent;
        }

        public String getContent() {
            return mrvFileContent;
        }
    }

}
