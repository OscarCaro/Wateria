package com.wateria.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.wateria.data.database.entity.PlantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants")
    fun observeAll(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants")
    suspend fun getAll(): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getById(id: String): PlantEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: PlantEntity)

    @Update
    suspend fun update(entity: PlantEntity)

    @Delete
    suspend fun delete(entity: PlantEntity)

    @Query("DELETE FROM plants WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM plants")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(entities: List<PlantEntity>)
}
