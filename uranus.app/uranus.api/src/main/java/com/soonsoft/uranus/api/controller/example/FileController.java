package com.soonsoft.uranus.api.controller.example;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    @PostMapping("/upload")
    public FileInfo uploadFile(@RequestParam("file") MultipartFile file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file.getOriginalFilename());
        fileInfo.setFilePath("/uploads/" + file.getOriginalFilename());
        fileInfo.setFileSize(String.valueOf(file.getSize()));
        return fileInfo;
    }

    public static class FileInfo {
        private String fileName;
        private String filePath;
        private String fileSize;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }
    }
    
}
