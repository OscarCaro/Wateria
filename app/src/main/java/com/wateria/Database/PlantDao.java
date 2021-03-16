package com.wateria.Database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlantDao {

    @Query("SELECT * FROM plantstable")
    List<PlantsTable> getAll();

}
