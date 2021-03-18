package com.wateria.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PlantEntity {

    // IMPORTANT: non primitive types are @nullable.
    // int     = notNull
    // INTEGER = nullable

    @PrimaryKey
    public int id;

    public String name;

    public Integer wat_freq;

    public Integer light;

    public String url;

}
