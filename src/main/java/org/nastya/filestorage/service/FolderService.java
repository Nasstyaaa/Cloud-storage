package org.nastya.filestorage.service;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.nastya.filestorage.exception.FolderUploadException;
import org.nastya.filestorage.exception.InternalServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class FolderService {
    private final FileService fileService;
    private final MinioClient minioClient;

    @Autowired
    public FolderService(FileService fileService) {
        this.fileService = fileService;
        minioClient =
                MinioClient.builder()
                        .endpoint("http://localhost:9000")
                        .credentials("nastya_user", "strong_password123")
                        .build();
    }

    public List<String> findAll(int idUser) {
        List<String> folderList = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket("user-files")
                    .prefix("user-" + idUser + "-files/")
                    .build());
            for(Result<Item> result : results){
                String folder = result.get().objectName();

                if(folder.endsWith("/") && !folder.equals("user-" + idUser + "-files/")){
                    folderList.add(folder.split("/")[1]);
                }
            }
        } catch (Exception e){
            throw new InternalServerException();
        }
        return folderList;
    }


    public void upload(int idUser, MultipartFile[] files){
        for(MultipartFile file : files){

            if(!file.getOriginalFilename().endsWith("/")){
                fileService.upload(idUser, file);
            }
            else {
                try {
                    Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                            .bucket("user-files")
                            .prefix("user-" + idUser + "-files/" + file.getOriginalFilename().split("/")[0])
                            .build());

                    List<MultipartFile> newFiles = new ArrayList<>();
                    for (Result<Item> result : results) {
                        newFiles.add((MultipartFile) result.get());
                    }
                    upload(idUser, newFiles.toArray(new MultipartFile[0]));
                } catch (Exception e){
                    throw new FolderUploadException();
                }
            }
        }
    }
}
