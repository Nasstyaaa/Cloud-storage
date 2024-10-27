package org.nastya.filestorage.service;

import io.minio.*;
import io.minio.messages.Item;
import org.nastya.filestorage.exception.FolderDeleteException;
import org.nastya.filestorage.exception.FolderDownloadException;
import org.nastya.filestorage.exception.FolderUploadException;
import org.nastya.filestorage.exception.InternalServerException;
import org.nastya.filestorage.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket, MinioUtil.getUserFolder(idUser));
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
                Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket,
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

    public void remove(int idUser, String folder) {
        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket,
                MinioUtil.getUserFolder(idUser) + folder);

        results.forEach(itemResult -> {
            try {
                String objectName = itemResult.get().objectName();
                String objectWithoutUserPrefix = objectName.substring(MinioUtil.getUserFolder(idUser).length());

                if (objectName.endsWith("/")) {
                    remove(idUser, objectWithoutUserPrefix);
                } else {
                    fileService.remove(idUser, objectWithoutUserPrefix);
                }
            } catch (Exception e) {
                throw new FolderDeleteException();
            }
        });
    }

    public void rename(int idUser, String sourceName, String newName) {
        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket,
                MinioUtil.getUserFolder(idUser) + sourceName);

        results.forEach(itemResult -> {
            try {
                String oldObjectName = itemResult.get().objectName();
                String newObjectName = oldObjectName.replaceFirst(MinioUtil.getUserFolder(idUser) + sourceName,
                        MinioUtil.getUserFolder(idUser) + newName);

                if (!oldObjectName.endsWith("/")) {
                    minioClient.copyObject(CopyObjectArgs.builder()
                            .bucket(bucket)
                            .object(newObjectName)
                            .source(CopySource.builder()
                                    .bucket(bucket)
                                    .object(oldObjectName)
                                    .build())
                            .build());
                } else {
                    String old = oldObjectName.substring(MinioUtil.getUserFolder(idUser).length());
                    String newO = newObjectName.substring(MinioUtil.getUserFolder(idUser).length());
                    rename(idUser, old, newO);
                }
            } catch (Exception e) {

            }
        });
        remove(idUser, sourceName);
    }


    public ByteArrayResource download(int idUser, String folder) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            addItemsToZip(MinioUtil.getUserFolder(idUser) + folder, zipOutputStream, idUser);
        } catch (Exception e) {
            throw new FolderDownloadException();
        }

        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    private void addItemsToZip(String path, ZipOutputStream zipOutputStream, int idUser) throws Exception {
        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket, path);

        for (Result<Item> itemResult : results) {
            String objectName = itemResult.get().objectName();
            String objectWithoutUserPrefix = objectName.substring(MinioUtil.getUserFolder(idUser).length());

            if (objectWithoutUserPrefix.endsWith("/")) {
                zipOutputStream.putNextEntry(new ZipEntry(objectWithoutUserPrefix));
                addItemsToZip(MinioUtil.getUserFolder(idUser) + objectWithoutUserPrefix, zipOutputStream, idUser);
                zipOutputStream.closeEntry();
            } else {
                addFileToZip(objectWithoutUserPrefix, zipOutputStream, idUser);
            }
        }
    }

    private void addFileToZip(String fileName, ZipOutputStream zipOutputStream, int idUser) throws Exception {
        ByteArrayResource object = fileService.download(idUser, fileName);
        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        zipOutputStream.write(object.getByteArray(), 0, object.getByteArray().length);
        zipOutputStream.closeEntry();
    }


}
