package br.com.devchampions.documentosextratortexto.validation;

import br.com.devchampions.documentosextratortexto.exceptions.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class DocumentoValidador {

    /**
     *
     */
    private static final Set<String> TIPO_DOCUMENTO_VALIDO = Set.of(
            "application/pdf"
            , "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            , "image/png"
    );


    /**
     * @param file
     */
    public static void validarTipoDocumento(final MultipartFile file) {

        if (!TIPO_DOCUMENTO_VALIDO.contains(file.getContentType())) {
            throw new BusinessException("Tipo de documento inávlido: ".concat(file.getContentType()));
        }
    }


}
