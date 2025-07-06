package br.com.devchampions.documentosextratortexto.service;

import br.com.devchampions.documentosextratortexto.dto.Documento;
import br.com.devchampions.documentosextratortexto.enums.FuzzinessEnum;
import br.com.devchampions.documentosextratortexto.repository.DocumentRepository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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




    public Documento processarArquivo(Documento documento) {
        return this.documentRepository.save(documento);
    }


    /**
     * Busca documentos por conteúdo
     */
    public List<Documento> buscarPorConteudo(String conteudo) throws IOException {
        Query query1 = QueryBuilders.match()
                .field("content")
                .query(conteudo)
                .build()._toQuery();

        MatchQuery query2 = MatchQuery.of(b -> b
                .field("content")
                .query(conteudo)
        );

        SearchRequest request = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(query2)
                .size(100)
        );

        SearchResponse<Documento> response = elasticsearchClient.search(request, Documento.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<Documento> buscarPorConteudoHighlight(String conteudo) throws IOException {

        SearchRequest request = SearchRequest.of(b -> b
                .index(INDEX_NAME)
                .query(Query.of(q -> q.match(MatchQuery.of(mb -> mb.field("content").query(conteudo)))))
                .highlight(h -> h.fields("content", f -> f.preTags("<b>").postTags("</b>")))
                .size(100)
        );

        SearchResponse<Documento> response = elasticsearchClient.search(request, Documento.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }


    public List<Documento> buscarPorConteudoFuzzy(String conteudo) throws IOException, ElasticsearchException {
        FuzzyQuery fuzzyQuery = FuzzyQuery.of(b -> b
                .field("content")
                .value(conteudo)
                .fuzziness(FuzzinessEnum.NIVEL_2.getValor())
                .prefixLength(0)
                .transpositions(true)
                .rewrite(null)
        );

        SearchRequest request = SearchRequest.of(b -> b
                .index("documents")
                .query(Query.of(q -> q.fuzzy(fuzzyQuery)))
                .size(50)
        );

        SearchResponse<Documento> response = elasticsearchClient.search(request, Documento.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    /**
     * * → Representa zero ou mais caracteres
     * ? → Representa exatamente um caractere
     *
     * @param wildcard
     * @return
     * @throws IOException
     * @throws ElasticsearchException
     */
    public List<Documento> buscarPorConteudoWildcard(String wildcard) throws IOException, ElasticsearchException {
        WildcardQuery wildcardQuery = WildcardQuery.of(b -> b
                .field("content")
                .value(wildcard)
                .caseInsensitive(true)
        );

        SearchRequest request = SearchRequest.of(b -> b
                .index("documents")
                .query(Query.of(q -> q.wildcard(wildcardQuery)))
                .size(50)
        );

        SearchResponse<Documento> response = elasticsearchClient.search(request, Documento.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }


    public Page<Documento> buscarPorConteudo(String termo, Pageable pageable) throws IOException, ElasticsearchException {
        MatchQuery matchQuery = MatchQuery.of(b -> b
                .field("content")
                .query(termo)
        );

        SearchRequest request = SearchRequest.of(b -> b
                .index(INDEX_NAME)
                .query(Query.of(q -> q.match(matchQuery)))
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize())
        );

        SearchResponse<Documento> response = elasticsearchClient.search(request, Documento.class);
        long totalHits = response.hits().total().value();
        List<Documento> content = response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, totalHits);
    }

}
