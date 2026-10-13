package br.gov.hackgov.service;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NaiveBayesAbsenteismoTest {
    @Test
    void riscoAprendePadroesPresentesNoHistorico() {
        List<NaiveBayesAbsenteismo.Amostra> exemplos = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            exemplos.add(new NaiveBayesAbsenteismo.Amostra(1, 0, 1L, true));
            exemplos.add(new NaiveBayesAbsenteismo.Amostra(5, 1, 1L, false));
        }
        var modelo = new NaiveBayesAbsenteismo(exemplos);
        double segundaManha = modelo.probabilidadeFalta(new NaiveBayesAbsenteismo.Amostra(1, 0, 1L, false));
        double sextaTarde = modelo.probabilidadeFalta(new NaiveBayesAbsenteismo.Amostra(5, 1, 1L, false));
        assertTrue(segundaManha > sextaTarde);
        assertTrue(segundaManha > 0 && segundaManha < 1);
        assertTrue(sextaTarde > 0 && sextaTarde < 1);
    }

    @Test
    void naoAceitaHistoricoVazio() {
        assertThrows(IllegalArgumentException.class, () -> new NaiveBayesAbsenteismo(List.of()));
    }
}
