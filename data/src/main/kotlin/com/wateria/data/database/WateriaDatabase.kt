package com.wateria.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wateria.data.database.dao.PlantDao
import com.wateria.data.database.entity.PlantEntity

@Database(
    entities = [PlantEntity::class],
    version = 1,
    exportSchema = true
)
abstract class WateriaDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}
