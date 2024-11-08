package org.nastya.filestorage.util;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;

public class MinioUtil {

    public static String getUserPrefix(int idUser) {
        return "user-" + idUser + "-files/";
    }

    public static String getObjectWithoutUserPrefix(String object) {
        int startIndex = object.indexOf("/");
        return object.substring(startIndex + 1);
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
