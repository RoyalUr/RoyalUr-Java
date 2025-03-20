package net.royalur.util;

import java.io.File;

public class FileUtils {

    private FileUtils() {}

    public static File replaceExtension(File originalFile, String newExtension) {
        String fileName = originalFile.getName();
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = (dotIndex > 0) ? fileName.substring(0, dotIndex) : fileName;
        return new File(originalFile.getParent(), baseName + newExtension);
    }
}
