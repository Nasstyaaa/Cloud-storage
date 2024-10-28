package org.nastya.filestorage.util;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;

public class MinioUtil {

    public static String getFullPathObject(int idUser, String object) {
        return ("user-" + idUser + "-files/") + object;
    }

    public static String getObjectWithoutUserPrefix(int idUser, String object) {
        return object.substring(MinioUtil.getFullPathObject(idUser, "").length());
    }

    public static Iterable<Result<Item>> getFolderObjects(MinioClient minioClient, String bucket, String prefix) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build());
    }

    public static Iterable<Result<Item>> getAllFolderObjects(MinioClient minioClient, String bucket, String prefix) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(prefix)
                .recursive(true)
                .build());
    }
}
