package com.github.ghostbusters.ghosthouse.syncservice;

import java.util.Date;

import lombok.Data;

@Data
class DevicePowerDtoResponse {
    private int id;

    private double value;

    private Date from;
}
