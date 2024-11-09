package org.nastya.filestorage.service;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.nastya.filestorage.DTO.BreadcrumbsDTO;
import org.nastya.filestorage.DTO.folder.FolderRequestDTO;
import org.nastya.filestorage.exception.EmptyFolderException;
import org.nastya.filestorage.exception.InternalServerException;
import org.nastya.filestorage.util.MinioUtil;
import org.nastya.filestorage.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public abstract class ObjectService {

    @Value("${minio.bucket}")
    protected String bucket;

    protected final MinioClient minioClient;

    @Autowired
    public ObjectService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    protected List<BreadcrumbsDTO> getAll(FolderRequestDTO requestDTO, boolean isFile) {
        List<BreadcrumbsDTO> objectList = new ArrayList<>();

        Iterable<Result<Item>> results = MinioUtil.getFolderObjects(minioClient, bucket, requestDTO.getPath());
        if (!results.iterator().hasNext()) {
            throw new EmptyFolderException();
        }
        results.forEach(itemResult -> {
            try {
                String object = PathUtil.getObjectWithoutFirstPrefix(itemResult.get().objectName());
                String objectName = Paths.get(object).getFileName().toString();
                if (!isFile) {
                    if (itemResult.get().isDir()) {
                        objectList.add(new BreadcrumbsDTO(objectName, object));
                    }
                } else {
                    if (!itemResult.get().isDir() && !object.isEmpty() && !object.endsWith("/")) {
                        objectList.add(new BreadcrumbsDTO(objectName, object));
                    }
                }
            } catch (Exception e) {
                throw new InternalServerException();
            }
        });
        return objectList;
    }
}
