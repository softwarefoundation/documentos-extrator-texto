package br.com.devchampions.documentosextratortexto.repository;

import br.com.devchampions.documentosextratortexto.dto.Documento;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends ElasticsearchRepository<Documento, String> {
}
