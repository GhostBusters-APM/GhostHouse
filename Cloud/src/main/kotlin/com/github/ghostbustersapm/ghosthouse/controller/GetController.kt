package com.github.ghostbustersapm.ghosthouse.controller

import com.github.ghostbustersapm.ghosthouse.entities.Device
import com.github.ghostbustersapm.ghosthouse.entities.DevicePowerData
import com.github.ghostbustersapm.ghosthouse.services.DeviceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.LocalTime
import java.util.Locale
import java.text.SimpleDateFormat



@RestController
class GetController {

    @Autowired
    lateinit var deviceService: DeviceService;

    @GetMapping("testget")
    fun get() = "Hello World";

    data class Data(val hour: Int, val power: Int);
    data class Wrapper(val data: List<Data>);
    @GetMapping("historic")
    fun getHistoric(): Wrapper {
        val now = LocalTime.now();
        return Wrapper((0..23).map { Data(it, (Math.random() * 20).toInt()) })
    }

    @GetMapping("device")
    fun getDevices(@RequestParam userId: String, @RequestParam(required = false) deviceId: Long?): List<Device> {
        return if (deviceId == null) {
            deviceService.getDevices(userId)
        } else
            deviceService.getDevices(userId, deviceId)
    }


    data class DeviceDto(val userId: String, val name: String, val latitude: String, val longitude: String, val type: Int, val state: Boolean)


    @PostMapping("device")
    fun createDevice(@RequestBody data: DeviceDto): Device {
        return deviceService.createDevice(data.userId, data.name, data.latitude, data.longitude, data.type, data.state);
    }

    data class DevicePowerDto(val deviceID: Long, val value: Double, val from:String, val to:String)

    @PostMapping("devicePower")
    fun createDevicePower(@RequestBody data: DevicePowerDto): DevicePowerData {
        val sdf = SimpleDateFormat("yyyyMMddHHmmss")

        val from=sdf.parse(data.from).toInstant();
        val to =sdf.parse(data.to).toInstant();
        return deviceService.registerPower(data.deviceID, data.value,from,to);
    }

    data class DevicePowerDtoResponse(val id: Long, val value: Double, val from: Instant,val to:Instant)

    @GetMapping("devicePower")
    fun getDevicePower(@RequestParam userId: String, @RequestParam deviceId: Long): List<DevicePowerDtoResponse> {
        return deviceService.getPower(userId, deviceId).map { DevicePowerDtoResponse(it.devicePowerDataId!!, it.value, it.from,it.to) }
    }


}