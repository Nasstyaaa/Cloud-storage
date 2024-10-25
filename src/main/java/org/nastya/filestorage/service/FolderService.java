package org.nastya.filestorage.service;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.nastya.filestorage.exception.FolderUploadException;
import org.nastya.filestorage.exception.InternalServerException;
import org.nastya.filestorage.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class FolderService {

    @Value("${minio.bucket}")
    private String bucket;

    private final FileService fileService;
    private final MinioClient minioClient;

    @Autowired
    public FolderService(FileService fileService, MinioClient minioClient) {
        this.fileService = fileService;
        this.minioClient = minioClient;
    }

    //TODO может ещё сократить
    public List<String> getAll(int idUser) {
        List<String> folderList = new ArrayList<>();

        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, MinioUtil.getUserFolder(idUser));
        results.forEach(itemResult -> {
            try {
                String folder = itemResult.get().objectName();

                if (folder.endsWith("/") && !folder.equals(MinioUtil.getUserFolder(idUser))) {
                    folderList.add(folder.split("/")[1]);
                }
            } catch (Exception e) {
                throw new InternalServerException();
            }
        });
        return folderList;
    }


    public void upload(int idUser, MultipartFile[] files) {
        for (MultipartFile file : files) {

            if (!file.getOriginalFilename().endsWith("/")) {
                fileService.upload(idUser, file);
            } else {
                Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient,
                        MinioUtil.getUserFolder(idUser) + file.getOriginalFilename().split("/")[0]);

                List<MultipartFile> newFiles = new ArrayList<>();
                results.forEach(itemResult -> {
                    try {
                        newFiles.add((MultipartFile) itemResult.get());
                    } catch (Exception e) {
                        throw new FolderUploadException();
                    }
                });
                upload(idUser, newFiles.toArray(new MultipartFile[0]));
            }
        }
    }
}
