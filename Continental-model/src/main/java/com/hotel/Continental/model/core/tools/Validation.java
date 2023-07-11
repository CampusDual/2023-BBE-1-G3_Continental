package com.hotel.continental.model.core.tools;

import com.hotel.continental.model.core.dao.CriteriaDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;

public class Validation {

    /**
     * Metodo que comprueba si el documento es valido
     *
     * @param document    Documento
     * @param countryCode Codigo de pais
     * @return true si es valido, false si no lo es
     */
    public static boolean checkDocument(String document, String countryCode) {
        if (countryCode.equals("ES")) {
            String dniRegex = "^\\d{8}[A-HJ-NP-TV-Z]$";
            String nieRegex = "^[XYZ]\\d{7}[A-Z]$";
            String cifRegex = "^([ABCDEFGHJKLMNPQRSUVW])(\\d{7})([0-9A-J])$";
            if (document.matches(dniRegex)) {
                String dniNumbers = document.substring(0, 8);
                String dniLetter = document.substring(8).toUpperCase();

                String validLetters = "TRWAGMYFPDXBNJZSQVHLCKE";
                int dniMod = Integer.parseInt(dniNumbers) % 23;
                char calculatedLetter = validLetters.charAt(dniMod);
                return dniLetter.charAt(0) == calculatedLetter;
            }
            String firstLetter = document.substring(0,1);
            if ((firstLetter.equals("Z") || firstLetter.equals("X") || firstLetter.equals("Y")) && document.matches(nieRegex)) {
                String nieNumbers = document.substring(1, 8);
                String validLetters = "TRWAGMYFPDXBNJZSQVHLCKE";
                String lastLetter = document.substring(8);
                int nieMod = Integer.parseInt(nieNumbers) % 23;
                char calculatedLetter = validLetters.charAt(nieMod);
                return lastLetter.charAt(0) == calculatedLetter;

            }
            return document.matches(cifRegex);
        }
        return true;
    }

    public static EntityResult checkNumber(String param) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(EntityResult.OPERATION_WRONG);

        try {
            double multiplier = Double.parseDouble(param);
            if(multiplier <= 0) {
                er.setMessage(ErrorMessages.MULTIPLIER_NOT_CORRECT_FORMAT);
                return er;
            }
        } catch (NumberFormatException e) {
            EntityResult erError = new EntityResultMapImpl();
            erError.setCode(EntityResult.OPERATION_WRONG);
            erError.setMessage(ErrorMessages.MULTIPLIER_NOT_NUMBER);
            return erError;
        }

        er.setCode(EntityResult.OPERATION_SUCCESSFUL);
        return er;
    }
}
