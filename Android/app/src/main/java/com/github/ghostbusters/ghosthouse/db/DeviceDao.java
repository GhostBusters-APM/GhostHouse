package com.github.ghostbusters.ghosthouse.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DeviceDao {
    @Query("SELECT * from Device")
    List<Device> getAll();

    @Query("SELECT * from Device WHERE userId = :userId")
    List<Device> getDevicesOfUser(String userId);

    @Insert
    void insertAll(Device... devices);

    @Update
    void update(Device device);

    @Delete
    void delete(Device device);
}
