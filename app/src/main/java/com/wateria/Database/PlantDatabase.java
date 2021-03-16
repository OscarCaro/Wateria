package com.wateria.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PlantEntity.class}, version = 1)
public abstract class PlantDatabase extends RoomDatabase {
    public abstract PlantDao plantDao();
}
