package gov.epa.ccte.api.batchsearch.service.filegenerators.excel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ExcelBaseTest {

    ExcelBase excelBase;

    @BeforeEach
    void setUp() {
        excelBase = Mockito.mock(ExcelBase.class, Mockito.CALLS_REAL_METHODS);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getContentType() {
    }

    @Test
    void getFilename() {
        String fileName = excelBase.getExcelFilename();

        Assertions.assertEquals("CCD-Batch-Search", fileName.split("_")[0]);
    }

    @Test
    void addHeader() {


    }
}