package br.com.devchampions.documentosextratortexto.service;

import br.com.devchampions.documentosextratortexto.dto.Documento;
import br.com.devchampions.documentosextratortexto.repository.DocumentRepository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DocumentService {

    private static final String INDEX_NAME = "documents";
    private final DocumentRepository documentRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;

    public DocumentService(DocumentRepository documentRepository,
                           ElasticsearchOperations elasticsearchOperations
            , ElasticsearchClient elasticsearchClient) {
        this.documentRepository = documentRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.elasticsearchClient = elasticsearchClient;
    }


    public Documento salvar(Documento documento) {
        return this.documentRepository.save(documento);
    }


    /**
     * Busca documentos por conteúdo
     */
    public List<Documento> buscarPorConteudo(String conteudo) throws IOException {
        Query query = QueryBuilders.match()
                .field("content")
                .query(conteudo)
                .build()._toQuery();

        SearchRequest request = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(query)
                .size(100)
        );

        SearchResponse<Documento> response = elasticsearchClient.search(request, Documento.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

}
