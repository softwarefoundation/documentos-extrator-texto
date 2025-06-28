package br.com.devchampions.documentosextratortexto.controller;

import br.com.devchampions.documentosextratortexto.dto.Documento;
import br.com.devchampions.documentosextratortexto.request.FiltroRequest;
import br.com.devchampions.documentosextratortexto.service.DocumentService;
import br.com.devchampions.documentosextratortexto.service.MinioService;
import br.com.devchampions.documentosextratortexto.service.TikaIntegrationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
    public ResponseEntity<Documento> upload(@RequestParam("file") MultipartFile file) throws Exception {
        minioService.upload(file.getOriginalFilename(), file.getInputStream(), file.getContentType());
        String texto = this.tikaIntegrationService.extrairTextoDoDocumento(file);

        Documento document = new Documento();
        document.setId(UUID.randomUUID().toString());
        document.setFileName(file.getOriginalFilename());
        document.setContent(texto);

        Documento docResponse = this.documentService.salvar(document);

        return ResponseEntity.ok(docResponse);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename) throws Exception {
        InputStream is = minioService.download(filename);
        byte[] content = is.readAllBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(content);
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
