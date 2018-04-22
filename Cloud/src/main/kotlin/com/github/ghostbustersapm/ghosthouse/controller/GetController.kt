package com.github.ghostbustersapm.ghosthouse.controller

import com.github.ghostbustersapm.ghosthouse.entities.Device
import com.github.ghostbustersapm.ghosthouse.entities.DeviceRepository
import com.github.ghostbustersapm.ghosthouse.services.DeviceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalTime

@RestController
class GetController{

    @Autowired
    lateinit  var deviceService: DeviceService;

    @GetMapping("testget")
    fun get()="Hello World";

    data class Data(val hour:Int, val power:Int);
    data class Wrapper(val data:List<Data>);
    @GetMapping("historic")
    fun getHistoric(): Wrapper {
        val now = LocalTime.now();
        return Wrapper((0..23).map { Data(it,(Math.random()*20).toInt())})
    }

    @GetMapping ("device")
    fun getDevices():MutableList<Device>{
        return deviceService.getDevices();
    }

    @PostMapping("device")
    fun createDevice(@RequestParam userId:String,@RequestParam name:String):Device{
        return deviceService.createDevice(userId,name,"","",1,1);
    }


}