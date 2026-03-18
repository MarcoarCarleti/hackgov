package br.gov.hackgov.util;

public final class MaskingUtils {

    private MaskingUtils() {
    }

    public static String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "***.***.***-" + cpf.substring(cpf.length() - 2);
    }

    public static String maskCartaoSus(String cartaoSus) {
        if (cartaoSus == null || cartaoSus.length() < 4) {
            return "***";
        }
        return "***********" + cartaoSus.substring(cartaoSus.length() - 4);
    }
}
