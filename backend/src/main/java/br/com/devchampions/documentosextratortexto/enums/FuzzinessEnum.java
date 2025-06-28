package br.com.devchampions.documentosextratortexto.enums;

import lombok.Getter;

@Getter
public enum FuzzinessEnum {


    /**
     * Deixa o ElasticSearch decidir com base no tamanho da palavra
     */
    NIVEL_AUTO("AUTO"),
    /**
     * Somente correspondências exatas
     */
    NIVEL_0("0"),
    /**
     * Um caractere alterado/diferente
     */
    NIVEL_1("1"),
    /**
     * Até dois caracteres alterados/diferentes
     */
    NIVEL_2("2");

    private String valor;

    FuzzinessEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
