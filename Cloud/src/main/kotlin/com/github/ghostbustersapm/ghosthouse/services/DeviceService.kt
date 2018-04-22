package com.github.ghostbustersapm.ghosthouse.services

import com.github.ghostbustersapm.ghosthouse.entities.Device
import com.github.ghostbustersapm.ghosthouse.entities.DeviceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.Id
import javax.transaction.Transactional

@Service
@Transactional(Transactional.TxType.SUPPORTS)
class DeviceService{

    @Autowired
    lateinit  var deviceRepository:DeviceRepository;

    fun createDevice(userId:String,name:String,latitude:String,longitude:String,type:Int,state:Int): Device {

        var device = Device(null,userId,name,latitude,longitude,type,state)
        return deviceRepository.save(device)
    }

    fun getDevices(userId:String): MutableList<Device> {

        return deviceRepository.findAll()
    }

    fun registerPower(deviceId:Int){

    }

    fun getPower(deviceId:Int){

    }
}