package com.wateria.Database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseInstance {

    private static final String DB_NAME = "my_database";
    private static PlantDatabase databaseInstance;

    public static PlantDatabase getInstance(Context context){

        if (databaseInstance == null){
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(), PlantDatabase.class, DB_NAME )
                    .createFromAsset("database.db")
                    .build();
        }
        return databaseInstance;
    }
}
