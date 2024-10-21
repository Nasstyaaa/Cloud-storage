package org.nastya.filestorage.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class FileService {
    private MinioClient minioClient;

    public FileService() {
        minioClient =
                MinioClient.builder()
                        .endpoint("http://localhost:9000")
                        .credentials("nastya_user", "strong_password123")
                        .build();
    }

    public void uploadFile(int idUser, MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("user-files")
                        .object("user-" + idUser + "-files/" + file.getOriginalFilename())
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .build());
    }
}


        //добавление папки //TODO а если в папке ещё папка
//        File folder = new File("helloTests");
//        for(File file : folder.listFiles()) {
//            minioClient.putObject(
//                    PutObjectArgs.builder()
//                            .bucket("user-files")
//                            .object("test/" + folder.getName() + "/" + file.getName())
//                            .stream(new FileInputStream(file), file.length(), -1)
//                            .build());
//        }

        //скачивание файла
//        minioClient.downloadObject(
//                DownloadObjectArgs.builder()
//                        .bucket("user-files")
//                        .object("test/hello.txt")
//                        .filename("hello.txt")
//                        .build());

        //скачивание папки //TODO а если в папке ещё папка
//        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream("helloTests.zip"))) {
//
//            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
//                    .bucket("user-files")
//                    .prefix("test/helloTests/")
//                    .build());
//
//            for (Result<Item> result : results) {
//                ZipEntry zipEntry = new ZipEntry(result.get().objectName().substring(5));
//                out.putNextEntry(zipEntry);
//
//                File tempFile = new File("temp");
//
//                minioClient.downloadObject(
//                        DownloadObjectArgs.builder()
//                                .bucket("user-files")
//                                .object(result.get().objectName())
//                                .filename(tempFile.getAbsolutePath())
//                                .build());
//
//                try (FileInputStream fis = new FileInputStream(tempFile)) {
//                    byte[] buffer = fis.readAllBytes();
//                    out.write(buffer, 0, buffer.length);
//                }
//                out.closeEntry();
//                tempFile.delete();
//            }
//        }

        //удалить файл
//        minioClient.removeObject(
//                RemoveObjectArgs.builder()
//                        .bucket("user-files")
//                        .object("test/hello.txt")
//                        .build()
//        );

        //удалить папку //TODO а если в папке ещё папка
//        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
//                .bucket("user-files")
//                .prefix("test/helloTests/")
//                .build());
//
//        for (Result<Item> result : results) {
//            minioClient.removeObject(
//                    RemoveObjectArgs.builder()
//                            .bucket("user-files")
//                            .object(result.get().objectName())
//                            .build());
//        }

        //переименовать файл

