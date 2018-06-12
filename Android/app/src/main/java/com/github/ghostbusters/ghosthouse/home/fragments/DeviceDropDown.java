package com.github.ghostbusters.ghosthouse.home.fragments;

import java.io.Serializable;

/**
 * Created by brais on 12/6/18.
 */

public class DeviceDropDown implements Serializable {

        private int deviceId;
        private String nombre;

        public DeviceDropDown(int deviceId, String nombre){
            this.deviceId = deviceId;
            this.nombre = nombre;
        }

    public int getDeviceId() {
        return deviceId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String toString()
    {
        return nombre;
    }
}
