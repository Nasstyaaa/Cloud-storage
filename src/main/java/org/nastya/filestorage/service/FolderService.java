package org.nastya.filestorage.service;

import io.minio.*;
import io.minio.messages.Item;
import org.nastya.filestorage.DTO.BreadcrumbsDTO;
import org.nastya.filestorage.DTO.file.*;
import org.nastya.filestorage.DTO.folder.*;
import org.nastya.filestorage.exception.*;
import org.nastya.filestorage.util.MinioUtil;
import org.nastya.filestorage.util.PathUtil;
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

    public List<BreadcrumbsDTO> getAll(FolderRequestDTO requestDTO) {
        return getAll(requestDTO, false);
    }


    public void upload(UploadFolderRequestDTO requestDTO) {
        for (MultipartFile file : requestDTO.getFolder()) {
            fileService.upload(new UploadFileRequestDTO(file, requestDTO.getPath()));
        }
    }


    public void remove(RemoveFolderRequestDTO requestDTO) {
        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket, requestDTO.getPath());
        results.forEach(itemResult -> {
            try {
                fileService.remove(new RemoveFileRequestDTO("", itemResult.get().objectName()));
            } catch (Exception e) {
                throw new FolderException("Folder deletion error, try again");
            }
        });
    }


    public void rename(RenameFolderRequestDTO requestDTO) {
        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket, requestDTO.getPath());
        results.forEach(itemResult -> {
            try {
                String fileName = "/" + PathUtil.getObjectPathWithoutPrefix(itemResult.get().objectName(), requestDTO.getPath());
                fileService.rename(new RenameFileRequestDTO("", requestDTO.getNewPath() + fileName,
                        requestDTO.getPath() + fileName));
            } catch (Exception e) {
                throw new FolderException("Folder renaming error, try again");
            }
        });
    }

    public ByteArrayResource download(DownloadFolderRequestDTO requestDTO) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket, requestDTO.getPath());

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (Result<Item> itemResult : results) {
                String objectName = PathUtil.getObjectPathWithoutPrefix(itemResult.get().objectName(), requestDTO.getPath());
                addFileToZip(zipOutputStream, new DownloadFileRequestDTO(objectName, itemResult.get().objectName()));
            }
        } catch (Exception e) {
            throw new FolderException("Error downloading the folder, try again");
        }

        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    private void addFileToZip(ZipOutputStream zipOutputStream, DownloadFileRequestDTO requestDTO) throws Exception {
        ByteArrayResource object = fileService.download(requestDTO);
        zipOutputStream.putNextEntry(new ZipEntry(requestDTO.getNameFile()));
        zipOutputStream.write(object.getByteArray(), 0, object.getByteArray().length);
        zipOutputStream.closeEntry();
    }

}
