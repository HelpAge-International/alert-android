package org.alertpreparedness.platform.alert.firebase;

import java.io.Serializable;

public class FirebaseModel implements Serializable{

    private String id = null;

    public FirebaseModel(String id) {
        this.id = id;
    }

    public FirebaseModel() {
    }

    @Override
    public String toString() {
        return "FirebaseModel{" +
                "id='" + id + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
