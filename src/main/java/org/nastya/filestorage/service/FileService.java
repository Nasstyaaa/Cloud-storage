package org.nastya.filestorage.service;

import io.minio.*;
import io.minio.messages.Item;
import org.nastya.filestorage.exception.*;
import org.nastya.filestorage.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    @Value("${minio.bucket}")
    private String bucket;

    private final MinioClient minioClient;

    @Autowired
    public FileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public List<String> getAll(int idUser) {
        List<String> fileList = new ArrayList<>();


        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket, MinioUtil.getUserFolder(idUser));
        results.forEach(itemResult -> {
            try {
                String file = itemResult.get().objectName();

                if (!file.endsWith("/")) {
                    fileList.add(file.split("/")[1]);
                }
            } catch (Exception e) {
                throw new InternalServerException();
            }
        });
        return fileList;
    }

    public void upload(int idUser, MultipartFile file) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(MinioUtil.getUserFolder(idUser) + file.getOriginalFilename())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build());
        } catch (Exception e) {
            throw new FileException("Error uploading the file, try again");
        }
    }

    public ByteArrayResource download(int idUser, String file) {
        try {
            GetObjectResponse object = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(MinioUtil.getUserFolder(idUser) + file)
                            .build());
            return new ByteArrayResource(object.readAllBytes());
        } catch (Exception e) {
            throw new FileException("Error downloading the file, try again");
        }
    }


    public void remove(int idUser, String file) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(MinioUtil.getUserFolder(idUser) + file)
                            .build()
            );
        } catch (Exception e) {
            throw new FileException("File deletion error, try again");
        }
    }


    public void rename(int idUser, String sourceName, String newName) {
        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(bucket)
                    .object(MinioUtil.getUserFolder(idUser) + newName + getFileExtension(sourceName))
                    .source(CopySource.builder()
                            .bucket(bucket)
                            .object(MinioUtil.getUserFolder(idUser) + sourceName)
                            .build())
                    .build());

            remove(idUser, sourceName);

        } catch (Exception e) {
            throw new FileException("File renaming error, try again");
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "");
    }
}

