package com.wateria.Database;

import androidx.room.Query;

import java.util.List;

public interface PlantDao {

    @Query("SELECT * FROM PlantsTable")
    List<PlantsTable> getAll();
}
