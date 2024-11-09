package org.nastya.filestorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileStorageApplication.class, args);
    }
}
//TODO логи
//TODO общая страница ошибки При неправильном адресе
//TODO перекидывать на главную при несуществующем пути к папке и пустые папки