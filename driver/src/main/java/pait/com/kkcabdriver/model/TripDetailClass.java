package pait.com.kkcabdriver.model;

import java.io.Serializable;

public class TripDetailClass implements Serializable {

    private String vehName, vehImgName;

    public String getVehName() {
        return vehName;
    }

    public void setVehName(String vehName) {
        this.vehName = vehName;
    }

    public String getVehImgName() {
        return vehImgName;
    }

    public void setVehImgName(String vehImgName) {
        this.vehImgName = vehImgName;
    }
}
