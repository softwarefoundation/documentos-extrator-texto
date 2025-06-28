# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.0/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.0/maven-plugin/build-image.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.5.0/reference/using/devtools.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.0/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.0/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Validation](https://docs.spring.io/spring-boot/3.5.0/reference/io/validation.html)
* [Spring Data Elasticsearch (Access+Driver)](https://docs.spring.io/spring-boot/3.5.0/reference/data/nosql.html#data.nosql.elasticsearch)

### Swagger
Documentação dos endpoints do Projeto
* [Documentação Swagger](http://localhost:8080/swagger-ui/index.html#/)


### MinIO Object Storage
Extração de texto e metadados de documentos
* [Console de administração do MinIO](http://localhost:9001/login)

Usuário: minioadmin

Senha: minioadmin

### Tika Rest API
Extração de texto e metadados de documentos

* [TIKA Rest APIs](http://localhost:9998/)


### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

