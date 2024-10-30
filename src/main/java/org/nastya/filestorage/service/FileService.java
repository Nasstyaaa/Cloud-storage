package org.nastya.filestorage.service;

import io.minio.*;
import org.nastya.filestorage.DTO.file.DownloadFileRequestDTO;
import org.nastya.filestorage.DTO.file.UploadFileRequestDTO;
import org.nastya.filestorage.exception.*;
import org.nastya.filestorage.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileService extends ObjectService {

    @Autowired
    public FileService(MinioClient minioClient) {
        super(minioClient);
    }

    public List<String> getAll(int idUser) {
        return getAll(idUser, true);
    }

    public void upload(UploadFileRequestDTO requestDTO) {
        MultipartFile file = requestDTO.getFile();
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(requestDTO.getPath() + file.getOriginalFilename())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build());
        } catch (Exception e) {
            throw new FileException("Error uploading the file, try again");
        }
    }

    public ByteArrayResource download(DownloadFileRequestDTO requestDTO) {
        try {
            GetObjectResponse object = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(requestDTO.getPath() + requestDTO.getNameFile())
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
                            .object(MinioUtil.getFullPathObject(idUser, file))
                            .build()
            );
        } catch (Exception e) {
            throw new FileException("File deletion error, try again");
        }
    }


    public void rename(int idUser, String sourceName, String newName) {
        try {
            String newPath = MinioUtil.getFullPathObject(idUser, newName + getFileExtension(sourceName)); //TODO в папках не должно добавляться
            String sourcePath = MinioUtil.getFullPathObject(idUser, sourceName);

            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(bucket)
                    .object(newPath)
                    .source(CopySource.builder()
                            .bucket(bucket)
                            .object(sourcePath)
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

