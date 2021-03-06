package com.github.ghostbusters.ghosthouse.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.List;

@Dao
@TypeConverters({DateConverter.class})
public interface DevicePowerDataDao {
    @Query("SELECT * from DevicePowerData")
    List<DevicePowerData> getAll();

    @Query("SELECT DPD.id, DPD.device_id, DPD.date, DPD.value " +
            "FROM DevicePowerData as DPD " +
            "INNER JOIN Device ON DPD.device_id = Device.id " +
            "WHERE Device.userId = :userId")
    List<DevicePowerData> getForUser(String userId);

    @Query("SELECT * FROM DevicePowerData " +
            "WHERE device_id = :deviceId")
    List<DevicePowerData> getForDeviceId(int deviceId);

    @Query("SELECT * FROM DevicePowerData " +
            "WHERE date >= :date")
    List<DevicePowerData> getAfterDate(@NonNull Date date);

    @Query("SELECT * FROM DevicePowerData " +
            "WHERE date <= :date")
    List<DevicePowerData> getBeforeDate(@NonNull Date date);

    @Query("SELECT DPD.id, DPD.device_id, DPD.date, DPD.value " +
            "FROM DevicePowerData as DPD " +
            "INNER JOIN Device ON DPD.device_id = Device.id " +
            "WHERE Device.userId = :userId " +
            "  AND Device.id = :deviceId")
    List<DevicePowerData> getForUserAndDeviceId(@NonNull String userId, int deviceId);

    @Query("SELECT DPD.id, DPD.device_id, DPD.date, DPD.value " +
            "FROM DevicePowerData as DPD " +
            "INNER JOIN Device ON DPD.device_id = Device.id " +
            "WHERE Device.userId = :userId " +
            "  AND DPD.date >= :date")
    List<DevicePowerData> getForUserAfterDate(@NonNull String userId, @NonNull Date date);

    @Query("SELECT DPD.id, DPD.device_id, DPD.date, DPD.value " +
            "FROM DevicePowerData as DPD " +
            "INNER JOIN Device ON DPD.device_id = Device.id " +
            "WHERE Device.userId = :userId " +
            "  AND DPD.date <= :date")
    List<DevicePowerData> getForUserBeforeDate(@NonNull String userId, @NonNull Date date);

    @Query("SELECT * FROM DevicePowerData " +
            "WHERE device_id = :deviceId " +
            "  AND date >= :date")
    List<DevicePowerData> getForDeviceIdAfterDate(int deviceId, @NonNull Date date);

    @Query("SELECT * FROM DevicePowerData " +
            "WHERE device_id = :deviceId " +
            "  AND date <= :date")
    List<DevicePowerData> getForDeviceIdBeforeDate(int deviceId, @NonNull Date date);


    @Query("SELECT * FROM DevicePowerData " +
            "WHERE date >= :startDate " +
            "  AND date <= :endDate")
    List<DevicePowerData> getBetweenDates(@NonNull Date startDate, @NonNull Date endDate);

    @Query("SELECT DPD.id, DPD.device_id, DPD.date, DPD.value " +
            "FROM DevicePowerData as DPD " +
            "INNER JOIN Device ON DPD.device_id = Device.id " +
            "WHERE Device.userId = :userId " +
            "  AND Device.id = :deviceId " +
            "  AND DPD.date >= :date")
    List<DevicePowerData> getForUserAndDeviceIdAfterDate(@NonNull String userId, int deviceId,
                                                         @NonNull Date date);

    @Query("SELECT DPD.id, DPD.device_id, DPD.date, DPD.value " +
            "FROM DevicePowerData as DPD " +
            "INNER JOIN Device ON DPD.device_id = Device.id " +
            "WHERE Device.userId = :userId " +
            "  AND Device.id = :deviceId " +
            "  AND DPD.date <= :date")
    List<DevicePowerData> getForUserAndDeviceIdBeforeDate(@NonNull String userId, int deviceId,
                                                          @NonNull Date date);

    @Query("SELECT DPD.id, DPD.device_id, DPD.date, DPD.value " +
            "FROM DevicePowerData as DPD " +
            "INNER JOIN Device ON DPD.device_id = Device.id " +
            "WHERE Device.userId = :userId " +
            "  AND DPD.date >= :startDate " +
            "  AND DPD.date <= :endDate")
    List<DevicePowerData> getForUserBetweenDates(@NonNull String userId, @NonNull Date startDate,
                                                 @NonNull Date endDate);

    @Query("SELECT * FROM DevicePowerData " +
            "WHERE device_id = :deviceId " +
            "  AND date >= :startDate " +
            "  AND date <= :endDate")
    List<DevicePowerData> getForDeviceIfBetweenDates(int deviceId, @NonNull Date startDate,
                                                     @NonNull Date endDate);

    @Query("SELECT DPD.id, DPD.device_id, DPD.date, DPD.value " +
            "FROM DevicePowerData as DPD " +
            "INNER JOIN Device ON DPD.device_id = Device.id " +
            "WHERE Device.userId = :userId " +
            "  AND Device.id = :deviceId " +
            "  AND DPD.date >= :startDate " +
            "  AND DPD.date <= :endDate")
    List<DevicePowerData> getForUserAndDeviceIdBetweenDates(@NonNull String userId, int deviceId,
                                                            @NonNull Date startDate,
                                                            @NonNull Date endDate);

    default List<DevicePowerData> getDevicePowerData(@Nullable String userId,
                                                     @Nullable Integer deviceId,
                                                     @Nullable Date startDate,
                                                     @Nullable Date endDate) {
        if (userId != null) {
            if (deviceId != null) {
                if (startDate != null) {
                    if (endDate != null) {
                        return getForUserAndDeviceIdBetweenDates(userId, deviceId,
                                startDate, endDate);
                    } else {
                        return getForUserAndDeviceIdAfterDate(userId, deviceId,
                                startDate);
                    }
                } else {
                    if (endDate != null) {
                        return getForUserAndDeviceIdBeforeDate(userId, deviceId,
                                endDate);
                    } else {
                        return getForUserAndDeviceId(userId, deviceId);
                    }
                }
            } else {
                if (startDate != null) {
                    if (endDate != null) {
                        return getForUserBetweenDates(userId, startDate, endDate);
                    } else {
                        return getForUserAfterDate(userId, startDate);
                    }
                } else {
                    if (endDate != null) {
                        return getForUserBeforeDate(userId, endDate);
                    } else {
                        return getForUser(userId);
                    }
                }
            }
        } else {
            if (deviceId != null) {
                if (startDate != null) {
                    if (endDate != null) {
                        return getForDeviceIfBetweenDates(deviceId, startDate, endDate);
                    } else {
                        return getForDeviceIdAfterDate(deviceId, startDate);
                    }
                } else {
                    if (endDate != null) {
                        return getForDeviceIdBeforeDate(deviceId, endDate);
                    } else {
                        return getForDeviceId(deviceId);
                    }
                }
            } else {
                if (startDate != null) {
                    if (endDate != null) {
                        return getBetweenDates(startDate, endDate);
                    } else {
                        return getAfterDate(startDate);
                    }
                } else {
                    if (endDate != null) {
                        return getBeforeDate(endDate);
                    } else {
                        return getAll();
                    }
                }
            }
        }
    }
}
