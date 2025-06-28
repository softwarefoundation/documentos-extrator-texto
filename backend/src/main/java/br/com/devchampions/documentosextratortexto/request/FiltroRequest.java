package br.com.devchampions.documentosextratortexto.request;

public class FiltroRequest {
    String texto;

    public FiltroRequest() {
    }

    public FiltroRequest(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
