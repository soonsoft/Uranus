package com.soonsoft.uranus.util.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.lang.StringUtils;

public abstract class FileTools {

    public static boolean exists(File file) {
        Guard.notNull(file, "the parameter file is required.");
        return file.exists();
    }

    public static boolean exists(String filename) {
        Guard.notEmpty(filename, "the parameter filename is required.");
        File file = new File(filename);
        return file.exists();
    }

    public static File createFile(String filename) {
        if(StringUtils.isEmpty(filename)) {
            return null;
        }

        File file = new File(filename);
        if(!file.exists()) {
            File directory = file.getParentFile();
            if(!directory.exists() && !directory.mkdirs()) {
                return null;
            }
            try {
                if(!file.createNewFile()) {
                    return null;
                }
            } catch(IOException e) {
                return null;
            }
        }
        return file;
    }

    public static File createDirectory(String directoryname) {
        if(StringUtils.isEmpty(directoryname)) {
            return null;
        }

        File file = new File(directoryname);
        if(!file.exists() && !file.mkdirs()) {
            return null;
        }
        return file;
    }

    public static void delete(String filename) {
        Guard.notEmpty(filename, "the parameter filename is required.");
        File file = new File(filename);
        if(!file.exists()) {
            return;
        }

        try {
            Files.delete(Path.of(file.getCanonicalPath()));
        } catch(IOException e) {
            throw new IllegalStateException("delete file error.", e);
        }
    }
    
}
