package br.com.devchampions.documentosextratortexto.service;

import br.com.devchampions.documentosextratortexto.dto.PdfDocument;
import br.com.devchampions.documentosextratortexto.repository.DocumentRepository;
import org.springframework.stereotype.Service;


@Service
public class DocumentService {


    private DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }


    public void salvar(PdfDocument pdfDocument) {
        this.documentRepository.save(pdfDocument);
    }

}
