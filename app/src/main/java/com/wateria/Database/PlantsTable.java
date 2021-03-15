package com.wateria.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PlantsTable {

    @PrimaryKey
    public int id;

    public String name;

    @ColumnInfo(name = "wat_freq")
    public int watFreq;
}
