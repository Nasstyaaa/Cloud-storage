package org.nastya.filestorage.service;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.nastya.filestorage.DTO.BreadcrumbsDTO;
import org.nastya.filestorage.exception.SearchFileError;
import org.nastya.filestorage.util.MinioUtil;
import org.nastya.filestorage.util.PathUtil;
import org.springframework.stereotype.Service;


import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.nastya.filestorage.util.PathUtil.getPathToFile;

@Service
public class SearchService extends ObjectService{

    public SearchService(MinioClient minioClient) {
        super(minioClient);
    }

    public List<BreadcrumbsDTO> searchObject(String query, String path){
        List<BreadcrumbsDTO> foundedObjects = new ArrayList<>();

        Iterable<Result<Item>> results = MinioUtil.getAllFolderObjects(minioClient, bucket, path);
        results.forEach(itemResult -> {
            try {
                String object = PathUtil.getObjectWithoutFirstPrefix(itemResult.get().objectName());
                String objectName = Paths.get(object).getFileName().toString();
                String pathToFile = getPathToFile(object, objectName);
                if(objectName.contains(query)){
                    foundedObjects.add(new BreadcrumbsDTO(objectName, pathToFile));
                }
            }catch (Exception e){
                throw new SearchFileError();
            }

        });

        return foundedObjects;
    }

}
