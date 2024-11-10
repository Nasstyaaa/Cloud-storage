package org.nastya.filestorage.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
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
        try {
            uploadEmptyFolder(requestDTO);
            for (MultipartFile file : requestDTO.getFolder()) {
                fileService.upload(new UploadFileRequestDTO(file, requestDTO.getPath()));
            }
            log.info("Folder {} successfully uploaded", (Object) requestDTO.getFolder());
        } catch (Exception e) {
            log.warn("An error occurred while uploading the folder {}", (Object) requestDTO.getFolder());
            throw new FolderException("Folder upload error, try again");
        }
    }

    private void uploadEmptyFolder(UploadFolderRequestDTO requestDTO) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MultipartFile[] files = requestDTO.getFolder();
        MultipartFile lastFile = files[files.length - 1];
        String[] pathParts = lastFile.getOriginalFilename().split("/");
        String fullPath = requestDTO.getPath();
        for (int i = 0; i < pathParts.length - 1; i++) {
            fullPath += pathParts[i] + "/";
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fullPath)
                            .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                            .build());
        }
        log.info("Empty folders {} successfully uploaded", fullPath);
    }


    public void remove(RemoveFolderRequestDTO requestDTO) {
        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket, requestDTO.getPath());
        results.forEach(itemResult -> {
            try {
                fileService.remove(new RemoveFileRequestDTO("", itemResult.get().objectName()));
                log.info("Folder {} successfully removed", requestDTO.getNameFolder());
            } catch (Exception e) {
                log.warn("An error occurred while removing the folder {}", requestDTO.getNameFolder());
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
                log.info("Folder {} successfully renamed", requestDTO.getNameFolder());
            } catch (Exception e) {
                log.warn("An error occurred while renaming the folder {}", requestDTO.getNameFolder());
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
            log.info("Folder {} successfully downloading", requestDTO.getNameFolder());
        } catch (Exception e) {
            log.warn("An error occurred while downloading the folder {}", requestDTO.getNameFolder());
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
