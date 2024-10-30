package org.nastya.filestorage.service;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.nastya.filestorage.exception.InternalServerException;
import org.nastya.filestorage.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public abstract class ObjectService {

    @Value("${minio.bucket}")
    protected String bucket;

    protected final MinioClient minioClient;

    @Autowired
    public ObjectService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    protected List<String> getAll(int idUser, boolean isFile) {
        List<String> objectList = new ArrayList<>();

        Iterable<Result<Item>> results = MinioUtil.getFolderObjects(minioClient, bucket,
                MinioUtil.getFullPathObject(idUser, ""));
        results.forEach(itemResult -> {
            try {
                String object = itemResult.get().objectName();
                String objectName = Paths.get(object).getFileName().toString();

                if (!isFile) {
                    if (object.endsWith("/") && !object.equals(MinioUtil.getFullPathObject(idUser, ""))) {
                        objectList.add(objectName);
                    }
                } else {
                    if (!object.endsWith("/")) {
                        objectList.add(objectName);
                    }
                }
            } catch (Exception e) {
                throw new InternalServerException();
            }
        });
        return objectList;
    }
}
