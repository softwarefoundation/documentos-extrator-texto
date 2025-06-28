package br.com.devchampions.documentosextratortexto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
public class TikaResponse {

    @JsonProperty("X-TIKA:content")
    private String xTikaContent;

    public String getxTikaContent() {
        return xTikaContent;
    }

    public void setxTikaContent(String xTikaContent) {
        this.xTikaContent = xTikaContent;
    }
}
