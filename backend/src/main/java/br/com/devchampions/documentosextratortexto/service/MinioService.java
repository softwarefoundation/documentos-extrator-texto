package br.com.devchampions.documentosextratortexto.service;

import br.com.devchampions.documentosextratortexto.exceptions.BusinessException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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


    public String getUrlPreAssinadaParaUpload() {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(UUID.randomUUID().toString())
                            .expiry(60, TimeUnit.MINUTES)
                            .build()
            );

        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    public String getUrlPreAssinadaParaDownload(final String uuid) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(uuid)
                            .expiry(60, TimeUnit.MINUTES)
                            .build()
            );

        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }


}
