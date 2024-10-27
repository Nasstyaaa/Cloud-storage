package org.nastya.filestorage.util;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;

public class MinioUtil {

    public static String getUserFolder(int idUser){
        return ("user-" + idUser + "-files/");
    }

    public static Iterable<Result<Item>> getAllFolderObjects(MinioClient minioClient, String bucket, String prefix){
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build());
    }
}