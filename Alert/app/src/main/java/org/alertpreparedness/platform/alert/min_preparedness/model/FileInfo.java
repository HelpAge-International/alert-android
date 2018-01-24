package org.alertpreparedness.platform.alert.min_preparedness.model;

/**
 * Created by faizmohideen on 23/01/2018.
 */

public class FileInfo {

    private String fileName;
    private String filePath;
    private Long module;
    private double size;
    private Long sizeType;
    private Long time;
    private String title;
    private String uploadedBy;

    public FileInfo(String fileName, String filePath, Long module, double size, Long sizeType, Long time, String title, String uploadedBy) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.module = module;
        this.size = size;
        this.sizeType = sizeType;
        this.time = time;
        this.title = title;
        this.uploadedBy = uploadedBy;
    }

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

    public Long getModule() {
        return module;
    }

    public void setModule(Long module) {
        this.module = module;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public Long getSizeType() {
        return sizeType;
    }

    public void setSizeType(Long sizeType) {
        this.sizeType = sizeType;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

}
