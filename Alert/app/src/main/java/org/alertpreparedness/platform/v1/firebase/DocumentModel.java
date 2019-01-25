package org.alertpreparedness.platform.v1.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class DocumentModel extends FirebaseModel {

    private String fileName;
    private String filePath;
    private Long time;
    private String uploadedBy;

    @Exclude
    private DatabaseReference ref;

    @Exclude
    private String key;

    public DocumentModel() {}

    @Override
    public String toString() {
        return "DocumentModel{" +
                "fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", time=" + time +
                ", uploadedBy='" + uploadedBy + '\'' +
                ", ref=" + ref +
                ", id='" + key + '\'' +
                '}' +
                super.toString();
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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    @Exclude
    public DatabaseReference getRef() {
        return ref;
    }

    @Exclude
    public void setRef(DatabaseReference ref) {
        this.ref = ref;
    }
}
