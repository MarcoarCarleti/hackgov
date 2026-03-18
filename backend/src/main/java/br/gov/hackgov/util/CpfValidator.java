package br.gov.hackgov.util;

public final class CpfValidator {

    private CpfValidator() {
    }

    public static boolean isValid(String cpf) {
        if (cpf == null) {
            return false;
        }
        String cleaned = cpf.replaceAll("\\D", "");
        if (cleaned.length() != 11 || cleaned.matches("(\\d)\\1{10}")) {
            return false;
        }
        int d1 = calculateDigit(cleaned, 9, 10);
        int d2 = calculateDigit(cleaned, 10, 11);
        return d1 == Character.getNumericValue(cleaned.charAt(9))
                && d2 == Character.getNumericValue(cleaned.charAt(10));
    }

    private static int calculateDigit(String cpf, int length, int weightStart) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (weightStart - i);
        }
        int mod = 11 - (sum % 11);
        return mod >= 10 ? 0 : mod;
    }
}
