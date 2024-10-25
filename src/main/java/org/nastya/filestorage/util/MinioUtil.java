package org.nastya.filestorage.util;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;

public class MinioUtil {

    @Value("${minio.bucket}")
    public static String bucket;

    public static String getUserFolder(int idUser){
        return ("user-" + idUser + "-files/");
    }

    public static Iterable<Result<Item>> getAllFolderObjects(MinioClient minioClient, String prefix){
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build());
    }
}
