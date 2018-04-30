package com.github.ghostbustersapm.ghosthouse.services

import com.github.ghostbustersapm.ghosthouse.entities.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Service
@Transactional(Transactional.TxType.SUPPORTS)
class DeviceService {

    @Autowired
    lateinit var deviceRepository: DeviceRepository;

    @Autowired
    lateinit var devicePowerDataRepository: DevicePowerDataRepository;

    @PersistenceContext
    lateinit var entityManager: EntityManager


    fun createDevice(userId: String, name: String, latitude: String, longitude: String, type: Int, state: Boolean): Device {

        var device = DeviceSwitch()
        device.userId = userId
        device.latitude = latitude
        device.longitude = longitude
        device.name = name
        device.state = state
        return deviceRepository.save(device)
    }

    fun getDevices(userId: String): List<Device> {

        return deviceRepository.findByUserId(userId)
    }


    fun registerPower(deviceId: Long, value: Double,from: Instant,to:Instant): DevicePowerData {
        var powerData = DevicePowerData()
        powerData.device = deviceRepository.findById(deviceId).get()
        powerData.value = value
        powerData.from = from;
        powerData.to = to;
        return devicePowerDataRepository.save(powerData)
    }


    fun getPower(userId: String, deviceId: Long): List<DevicePowerData> {
        return devicePowerDataRepository.getByDevice_DeviceIdOrderByInstant(deviceId);
    }

    fun getDevices(userId: String, deviceId: Long): List<Device> {
        return deviceRepository.findByUserIdAndDeviceId(userId, deviceId)
    }
}