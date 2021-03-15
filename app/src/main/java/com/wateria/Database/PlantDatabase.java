package com.wateria.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PlantsTable.class}, version = 1)
public abstract class PlantDatabase extends RoomDatabase {
    public abstract PlantDao plantDao();
}
