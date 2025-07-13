package br.com.devchampions.documentosextratortexto.response;

public class ResponseMessage {

    private String value;

    public ResponseMessage(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
