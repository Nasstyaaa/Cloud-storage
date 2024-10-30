package org.nastya.filestorage.service;

import io.minio.*;
import io.minio.messages.Item;
import org.nastya.filestorage.DTO.file.UploadFileRequestDTO;
import org.nastya.filestorage.DTO.folder.UploadFolderRequestDTO;
import org.nastya.filestorage.exception.*;
import org.nastya.filestorage.util.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FolderService extends ObjectService {
    private final FileService fileService;

    @Autowired
    public FolderService(FileService fileService, MinioClient minioClient) {
        super(minioClient);
        this.fileService = fileService;
    }

    public List<String> getAll(int idUser) {
        return getAll(idUser, false);
    }


    public void upload(UploadFolderRequestDTO requestDTO) {
        for (MultipartFile file : requestDTO.getFolder()) {
            fileService.upload(new UploadFileRequestDTO(file, requestDTO.getPath()));
        }
    }


    public void remove(int idUser, String folder) {
        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket,
                MinioUtil.getFullPathObject(idUser, folder));
        results.forEach(itemResult -> {
            try {
                String objectName = MinioUtil.getObjectWithoutUserPrefix(idUser, itemResult.get().objectName());
                fileService.remove(idUser, objectName);
            } catch (Exception e) {
                throw new FolderException("Folder deletion error, try again");
            }
        });
    }


    public void rename(int idUser, String sourceName, String newName) {
        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket,
                MinioUtil.getFullPathObject(idUser, sourceName));

        results.forEach(itemResult -> {
            try {
                String oldObjectName = MinioUtil.getObjectWithoutUserPrefix(idUser, itemResult.get().objectName());
                int firstSlashIndex = oldObjectName.indexOf('/');
                String newObjectName = newName + oldObjectName.substring(firstSlashIndex + 1);
                fileService.rename(idUser, oldObjectName, newObjectName);
            } catch (Exception e) {
                throw new FolderException("Folder renaming error, try again");
            }
        });
    }

    public ByteArrayResource download(int idUser, String folder) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket,
                MinioUtil.getFullPathObject(idUser, folder));

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (Result<Item> itemResult : results) {
                String objectName = MinioUtil.getObjectWithoutUserPrefix(idUser, itemResult.get().objectName());
                addFileToZip(objectName, zipOutputStream, idUser);
            }
        } catch (Exception e) {
            throw new FolderException("Error downloading the folder, try again");
        }

        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    private void addFileToZip(String fileName, ZipOutputStream zipOutputStream, int idUser) throws Exception {
        ByteArrayResource object = fileService.download(idUser, fileName);
        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        zipOutputStream.write(object.getByteArray(), 0, object.getByteArray().length);
        zipOutputStream.closeEntry();
    }

}
