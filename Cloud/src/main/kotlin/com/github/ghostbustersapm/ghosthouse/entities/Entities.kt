package com.github.ghostbustersapm.ghosthouse.entities

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name="device")
data class Device(
        @Id @GeneratedValue(strategy=GenerationType.AUTO) val deviceId: Long? = null,
        val userId : String= "",
        val name: String = "",
        val latitude: String = "",
        val longitude: String = "",
        val type: Int= 1,
        val state: Int = 1)

@Repository
interface DeviceRepository : JpaRepository<Device, Long>

@Entity
@Table(name="device_power_data")
data class DevicePowerData(
        @Id @GeneratedValue(strategy=GenerationType.AUTO) val devicePowerDataId: Long? = null,
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "device_id") val device: Device? = null,
        val instant: Instant = Instant.now(),
        val value: Double = 1.0)

@Repository
interface DevicePowerDataRepository : JpaRepository<DevicePowerData, Long>