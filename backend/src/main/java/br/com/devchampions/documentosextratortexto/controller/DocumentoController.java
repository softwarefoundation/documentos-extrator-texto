package br.com.devchampions.documentosextratortexto.controller;

import br.com.devchampions.documentosextratortexto.dto.Documento;
import br.com.devchampions.documentosextratortexto.exceptions.BusinessException;
import br.com.devchampions.documentosextratortexto.request.FiltroRequest;
import br.com.devchampions.documentosextratortexto.response.ResponseMessage;
import br.com.devchampions.documentosextratortexto.service.DocumentService;
import br.com.devchampions.documentosextratortexto.service.MinioService;
import br.com.devchampions.documentosextratortexto.service.TikaIntegrationService;
import br.com.devchampions.documentosextratortexto.util.DocumentoContentType;
import br.com.devchampions.documentosextratortexto.util.converter.DocumentoConverter;
import br.com.devchampions.documentosextratortexto.validation.DocumentoValidador;
import io.minio.StatObjectResponse;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/documento")
public class DocumentoController {

    private final MinioService minioService;
    private final TikaIntegrationService tikaIntegrationService;
    private final DocumentService documentService;

    public DocumentoController(MinioService minioService,
                               TikaIntegrationService tikaIntegrationService,
                               DocumentService documentService
    ) {
        this.minioService = minioService;
        this.tikaIntegrationService = tikaIntegrationService;
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> upload(@RequestParam("file") MultipartFile file) throws Exception {


        try {
            DocumentoValidador.validarTipoDocumento(file);

            String texto = this.tikaIntegrationService.extrairTextoDoDocumento(file);

            String uuid = UUID.randomUUID().toString();
            Documento document = new Documento();
            document.setId(uuid);
            document.setFileName(file.getOriginalFilename());
            document.setContent(texto);

            Documento docResponse = this.documentService.processarArquivo(document);

            minioService.upload(uuid, file);

            return ResponseEntity.ok(new ResponseMessage(docResponse.getId()));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }


    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename) throws Exception {
        InputStream is = minioService.download(filename);
        byte[] content = is.readAllBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(content);
    }

    @GetMapping("/download/base64/{uuid}")
    public ResponseEntity<String> downloadBase64(@PathVariable String uuid) throws Exception {

        System.out.println("Buscando arquivo: " + uuid);

        try (InputStream originalFile = minioService.download(uuid)) {

            InputStream pdfInputStream;

            StatObjectResponse response = minioService.getFileMetadata(uuid);

            if (DocumentoContentType.APPLICATION_PDF.equalsIgnoreCase(response.contentType())) {
                pdfInputStream = originalFile;
            } else if (DocumentoContentType.APPLICATION_PDF.equalsIgnoreCase(response.contentType())) {
                pdfInputStream = DocumentoConverter.convertDocToPdf(originalFile);
            } else {
                return ResponseEntity.badRequest()
                        .body("Tipo de arquivo não suportado: ".concat(response.contentType()));
            }

            // Converte o PDF para base64
            String base64Content = convertToBase64(pdfInputStream);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                    .header("Content-Disposition", "inline; filename=\"" + uuid + ".pdf\"")
                    .body(base64Content);

        }

    }


    private String getFileExtension(InputStream originalFile) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384]; // 16KB
        while ((nRead = originalFile.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        String mimeType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(buffer.toByteArray()));

        return mimeType;
    }


    private String convertToBase64(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Base64OutputStream b64os = new Base64OutputStream(baos)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                b64os.write(buffer, 0, bytesRead);
            }

            return new String(baos.toByteArray(), StandardCharsets.US_ASCII);
        }
    }


    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> excluirDocumento(@PathVariable String uuid) {

        System.out.println("Excluindo arquivo: " + uuid);

        this.documentService.excluirArquivo(uuid);
        this.minioService.excluirDocumento(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/buscar-conteudo")
    public ResponseEntity<?> buscarPorConteudo(@RequestBody FiltroRequest request) {
        try {
            List<Documento> documentos = documentService.buscarPorConteudo(request.getTexto());
            return ResponseEntity.ok(documentos);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro na busca: " + e.getMessage());
        }
    }

    @GetMapping("/buscar-conteudo-highlight")
    public ResponseEntity<?> buscarPorConteudoHighlight(@RequestParam String conteudo) {
        try {
            List<Documento> documentos = documentService.buscarPorConteudoHighlight(conteudo);
            return ResponseEntity.ok(documentos);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro na busca: " + e.getMessage());
        }
    }


    @GetMapping("/buscar-conteudo-fuzzy")
    public ResponseEntity<?> buscarPorConteudoFuzzy(@RequestParam String conteudo) {
        try {
            List<Documento> documentos = documentService.buscarPorConteudoFuzzy(conteudo);
            return ResponseEntity.ok(documentos);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro na busca: " + e.getMessage());
        }
    }

    @GetMapping("/buscar-conteudo-wildcard")
    public ResponseEntity<?> buscarPorConteudoWildcard(@RequestParam String conteudo) {
        try {
            List<Documento> documentos = documentService.buscarPorConteudoWildcard(conteudo);
            return ResponseEntity.ok(documentos);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro na busca: " + e.getMessage());
        }
    }


}
