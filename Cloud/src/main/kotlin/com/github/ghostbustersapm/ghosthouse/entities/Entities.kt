package com.github.ghostbustersapm.ghosthouse.entities

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import javax.persistence.*

enum class Type { SWITCH }

@Entity
@Table(name = "device")
@Inheritance
@DiscriminatorColumn(name = "TYPE")
abstract class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    open var deviceId: Long? = null
    open var userId: String = ""
    open var name: String = ""
    open var latitude: String = ""
    open var longitude: String = ""
    open var ip: String = ""

}

@Entity
@DiscriminatorValue("S")
class DeviceSwitch() : Device() {
    var state: Boolean = false
}


@Repository
interface DeviceRepository : JpaRepository<Device, Long> {

    fun findByUserId(userId: String): List<Device>;
    fun findByUserIdAndDeviceId(userId: String, deviceId: Long): List<Device>
}

@Entity
@Table(name = "device_power_data")
class DevicePowerData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var devicePowerDataId: Long? = null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    var device: Device? = null
    var instantFrom: Instant = Instant.now()
    var to: Instant = Instant.now()
    var value: Double = 0.0

}

@Repository
interface DevicePowerDataRepository : JpaRepository<DevicePowerData, Long> {
    fun getByDevice_DeviceIdOrderByInstantFrom(deviceId: Long): List<DevicePowerData>
}