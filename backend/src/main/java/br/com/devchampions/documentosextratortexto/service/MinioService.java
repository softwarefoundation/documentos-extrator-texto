package br.com.devchampions.documentosextratortexto.service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MinioService {

    @Value("${minio.bucket}")
    private String bucketName;

    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void upload(String uuid, MultipartFile file) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }


        Map<String, String> tags = new HashMap<>();
        tags.put("fileName", file.getOriginalFilename());
        tags.put("fileType", file.getContentType());

        ObjectWriteResponse response = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(uuid)
                        .tags(tags)
                        .stream(file.getInputStream(), -1, 10485760)
                        .contentType(file.getContentType())
                        .build()
        );

        String etag = response.etag();
        String object = response.object();

        System.out.println("E-TAG: " + etag);
        System.out.println("OBJECT: " + object);
    }

    public InputStream download(String uuid) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(uuid)
                        .build()
        );
    }

    public StatObjectResponse getFileMetadata(String uuid) throws Exception {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(uuid)
                        .build()
        );
    }

    public void excluirDocumento(final String uuid) {
        try {
            this.minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName).object(uuid).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
