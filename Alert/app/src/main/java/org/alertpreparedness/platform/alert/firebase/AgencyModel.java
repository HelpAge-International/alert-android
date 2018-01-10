package org.alertpreparedness.platform.alert.firebase;

import java.util.HashMap;

/**
 * Created by Tj on 09/01/2018.
 */

public class AgencyModel {

    private String name;

    private String logoPath;

//    private HashMap<String, Boolean> networks;

    public AgencyModel() {

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

//    public HashMap<String, Boolean> getNetworks() {
//        return networks;
//    }
//
//    public void setNetworks(HashMap<String, Boolean> networks) {
//        this.networks = networks;
//    }
}
