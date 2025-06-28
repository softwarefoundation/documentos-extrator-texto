package br.com.devchampions.documentosextratortexto.controller;

import br.com.devchampions.documentosextratortexto.dto.PdfDocument;
import br.com.devchampions.documentosextratortexto.service.DocumentService;
import br.com.devchampions.documentosextratortexto.service.MinioService;
import br.com.devchampions.documentosextratortexto.service.TikaIntegrationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/documento")
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
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        minioService.upload(file.getOriginalFilename(), file.getInputStream(), file.getContentType());
        String texto = this.tikaIntegrationService.extrairTextoDoDocumento(file);

        PdfDocument document = new PdfDocument();
        document.setId(UUID.randomUUID().toString());
        document.setFileName(file.getOriginalFilename());
        document.setContent(texto);

        this.documentService.salvar(document);

        return ResponseEntity.ok("Upload realizado com sucesso!: \n" + texto);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename) throws Exception {
        InputStream is = minioService.download(filename);
        byte[] content = is.readAllBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(content);
    }

}
