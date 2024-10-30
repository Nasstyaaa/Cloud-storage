package org.nastya.filestorage.DTO.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class UploadFileRequestDTO {
    private MultipartFile file;
    private String path;
}
