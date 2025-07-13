package br.com.devchampions.documentosextratortexto.validation;

import br.com.devchampions.documentosextratortexto.exceptions.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public class DocumentoValidador {

    /**
     * `
     * https://mimetype.io/all-types
     * <p>
     * `.shp`, `.shx`, `.dbf`, `.prj`, `.cpg`, `.qpj`, `.sbn`, `.sbx`, `.tfw`, `.aux.xml`, `.jgw`, `.pgw`, `.kml`,
     * `.dxf`
     * `.sql`, `.pgsql`, `.pgadmin`
     */
    private static final Map<String, String> TIPO_DOCUMENTO_VALIDO = Map.ofEntries(
            Map.entry("pdf", "application/pdf")
            , Map.entry("doc", "application/msword")
            , Map.entry("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            , Map.entry("xls", "application/vnd.ms-excel")
            , Map.entry("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            , Map.entry("ppt", "application/vnd.ms-powerpoint")
            , Map.entry("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation")
            , Map.entry("png", "image/png")
            , Map.entry("jpeg", "image/jpeg")
            , Map.entry("gif", "image/gif")
            , Map.entry("bmp", "image/bmp")
            , Map.entry("epub", "application/epub+zip")
            , Map.entry("mp3", "audio/mpeg")
            , Map.entry("mp4", "video/mp4")
            , Map.entry("heic", "image/heic")
            , Map.entry("dwg", "image/vnd.dwg")
            , Map.entry("kmz", "application/vnd.google-earth.kmz")
    );


    /**
     * @param file
     */
    public static void validarTipoDocumento(final MultipartFile file) {

        if (!TIPO_DOCUMENTO_VALIDO.containsValue(file.getContentType())) {
            throw new BusinessException("Tipo de documento inávlido: ".concat(file.getContentType()));
        }
    }


}
