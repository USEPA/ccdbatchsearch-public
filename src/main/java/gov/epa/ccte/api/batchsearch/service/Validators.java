package gov.epa.ccte.api.batchsearch.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Validators {

    public static boolean isDtxcid(String dtxcid) {
        dtxcid = dtxcid.toUpperCase();
        return dtxcid.matches("DTXCID(.*)");
    }

    public static boolean isDtxsid(String dtxsid) {
        dtxsid = dtxsid.toUpperCase();
        return dtxsid.matches("DTXSID(.*)");
    }

    public static String toCasrn(String number) {
        return String.format("%s-%s-%s", number.substring(0, number.length() - 3), number.substring(number.length() - 3, number.length() - 1), number.substring(number.length() - 1));
    }

    public static boolean isCasrn(String casrn) {
        return casrn.matches("^\\d{1,7}-\\d{2}-\\d$");
    }

    public static boolean isECNumber(String casrn) {
        return casrn.matches("^\\d{3}-\\d{3}-\\d$");
    }

    public static boolean isInchiKey(String inchikey) {
        inchikey = inchikey.toUpperCase();
        return inchikey.matches("[A-Z]{14}-[A-Z]{10}-[A-Z]");
    }

    public static boolean isInchiKeySkeleton(String inchikeyskeleton) {
        inchikeyskeleton = inchikeyskeleton.toUpperCase();
        return inchikeyskeleton.matches("[A-Z]{14}");
    }

    public static boolean isMassValues(String massValues) {
        String[] values = massValues.split(",");

        if (values.length == 2) {
            return isNumeric(values[0]) && isNumeric(values[1]);
        }
        return false;
    }

    //  this will not check -ve and decimal numbers
    public static Boolean isNumeric(String number) {
        // null or empty
        if (number == null || number.length() == 0) {
            return false;
        }

        boolean isDecimalFound = false;

        for (char c : number.toCharArray()) {
            if (!Character.isDigit(c)) {
                // check for decimal
                if (c == '.' && !isDecimalFound) {
                    isDecimalFound = true;
                    continue;
                }
                return false;
            }
        }
        return true;

//        return  number.chars().allMatch( Character::isDigit );
    }

    public static boolean checkCasrnFormat(String casrn, boolean checkForDash) {
// Check the string against the mask
        if (checkForDash && !casrn.matches("^\\d{1,7}-\\d{2}-\\d$")) {
            return false;
        } else {
// Remove the dashes
            casrn = casrn.replaceAll("-", "");
            int sum = 0;
            for (int indx = 0; indx < casrn.length() - 1; indx++) {
                sum += (casrn.length() - indx - 1) * Integer.parseInt(casrn.substring(indx, indx + 1));
            }
// Check digit is the last char, compare to sum mod 10.
            log.debug("v1= {} and v2= {}", Integer.parseInt(casrn.substring(casrn.length() - 1)), (sum % 10));
            return Integer.parseInt(casrn.substring(casrn.length() - 1)) == (sum % 10);
        }
    }

    public static String getDataNotFoundMsg(String notFoundWord) {

        String msg;

        if (isCasrn(notFoundWord)) {
            if (checkCasrnFormat(notFoundWord, true) == false)
                msg = "CAS number fails checksum.";
            else
                msg = "Searched by CASRN: Found 0 results";
        } else if (isDtxcid(notFoundWord)) {
            msg = "Searched by DTX Compound Id: Found 0 results";
        } else if (isDtxsid(notFoundWord)) {
            msg = "Searched by DTX Substance Id: Found 0 results ";
        } else if (isInchiKey(notFoundWord)) {
            msg = "Searched by InChIKey: Found 0 results";
        } else if (isInchiKeySkeleton(notFoundWord)) {
            msg = "Searched by InChIKey Skeleton: Found 0 results ";
        } else if (isMassValues(notFoundWord)) {
            msg = "Search for Monoisotopic Mass : Found 0 results";
        } else {
            msg = "Searched by Synonym: Found 0 results";
        }
        return msg;
    }

}
