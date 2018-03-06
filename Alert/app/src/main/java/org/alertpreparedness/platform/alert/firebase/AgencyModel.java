package org.alertpreparedness.platform.alert.firebase;

public class AgencyModel extends FirebaseModel {

    private String name;

    private String logoPath;

    public AgencyModel() {
    }

    @Override
    public String toString() {
        return "AgencyModel{" +
                "name='" + name + '\'' +
                ", logoPath='" + logoPath + '\'' +
                '}' +
                super.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}
