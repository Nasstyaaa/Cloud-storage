package org.nastya.filestorage.util;

public class PathUtil {

    public static String getObjectPathWithoutPrefix(String object, String prefix) {
        return object.substring(prefix.length());
    }

    public static String getObjectWithoutFirstPrefix(String object) {
        int startIndex = object.indexOf("/");
        return object.substring(startIndex + 1);
    }

    public static String getFullPath(int userPref, String path){
        return MinioUtil.getUserPrefix(userPref) + path;
    }

    public static String getPathToFolder(String path, String nameFolder) {
        return path.replace(nameFolder + "/", "");
    }

    public static String getPathToFile(String path, String nameFile) {
        return path.replace(nameFile, "");
    }
}
