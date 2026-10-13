package br.gov.hackgov.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Classificador Naive Bayes categorico treinado em resultados historicos.
 * Nao usa CPF, CNS, nomes ou identificadores de pacientes.
 */
public final class NaiveBayesAbsenteismo {

    public record Amostra(int diaSemana, int turno, Long ubsId, boolean falta) {}

    private final Map<String, Integer> frequencias = new HashMap<>();
    private final int total;
    private final int faltas;
    private final int quantidadeUbs;

    public NaiveBayesAbsenteismo(List<Amostra> historico) {
        if (historico == null || historico.isEmpty()) {
            throw new IllegalArgumentException("Historico de treinamento obrigatorio");
        }
        total = historico.size();
        int qtdFaltas = 0;
        Set<Long> unidades = new HashSet<>();
        for (Amostra amostra : historico) {
            if (amostra.falta()) qtdFaltas++;
            unidades.add(amostra.ubsId());
            registrar(amostra.falta(), "dia", Integer.toString(amostra.diaSemana()));
            registrar(amostra.falta(), "turno", Integer.toString(amostra.turno()));
            registrar(amostra.falta(), "ubs", String.valueOf(amostra.ubsId()));
        }
        faltas = qtdFaltas;
        quantidadeUbs = unidades.size() + 1; // Reserva para uma UBS nunca vista no treino
    }

    public double probabilidadeFalta(Amostra amostra) {
        double sim = logPosterior(amostra, true);
        double nao = logPosterior(amostra, false);
        return 1.0 / (1.0 + Math.exp(nao - sim));
    }

    private double logPosterior(Amostra amostra, boolean falta) {
        int classe = falta ? faltas : total - faltas;
        // Suavizacao de Laplace tambem sobre a probabilidade a priori.
        double resultado = Math.log((classe + 1.0) / (total + 2.0));
        resultado += logCondicional(falta, "dia", Integer.toString(amostra.diaSemana()), 7, classe);
        resultado += logCondicional(falta, "turno", Integer.toString(amostra.turno()), 2, classe);
        resultado += logCondicional(falta, "ubs", String.valueOf(amostra.ubsId()), quantidadeUbs, classe);
        return resultado;
    }

    private double logCondicional(boolean falta, String caracteristica, String valor,
                                 int cardinalidade, int classe) {
        int ocorrencias = frequencias.getOrDefault(chave(falta, caracteristica, valor), 0);
        return Math.log((ocorrencias + 1.0) / (classe + cardinalidade));
    }

    private void registrar(boolean falta, String caracteristica, String valor) {
        frequencias.merge(chave(falta, caracteristica, valor), 1, Integer::sum);
    }

    private String chave(boolean falta, String caracteristica, String valor) {
        return (falta ? "F" : "R") + "|" + caracteristica + "|" + valor;
    }
}
