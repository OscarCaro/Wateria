package com.example.wateria.DataStructures;


import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

public class Plant implements Comparable<Plant>, Parcelable {
    // Expose: indicates which attributes should be stored in the Gson.toJson for the sharedPreferences
    @Expose
    private String plantName;
    @Expose
    private int iconIdx;
    @Expose
    private LocalDate nextWateringDate;     // Has it's own GsonSerializer bc internal attributes don't have @Expose
    @Expose
    private int wateringFrequency;

    // Don't expose:
    private int daysRemaining;
    private Drawable icon;

    public Plant (String plantName, int iconIdx, int wateringFrequency, LocalDate nextWateringDate){
        this.plantName = plantName;
        this.iconIdx = iconIdx;
        this.wateringFrequency = wateringFrequency;
        this.nextWateringDate = nextWateringDate;
    }

    public Plant (Parcel source){
        this.plantName = source.readString();
        this.iconIdx = source.readInt();
        this.nextWateringDate = LocalDate.of(source.readInt(), source.readInt(), source.readInt());
        this.wateringFrequency = source.readInt();
    }

    @Override
    public int compareTo(Plant comparePlant){
        int compareDays = ((Plant)comparePlant).getDaysRemaining();
        return this.daysRemaining - compareDays;
    }

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(plantName);
        dest.writeInt(iconIdx);
        dest.writeInt(nextWateringDate.getYear());
        dest.writeInt(nextWateringDate.getMonthValue());
        dest.writeInt(nextWateringDate.getDayOfMonth());
        dest.writeInt(wateringFrequency);
    }
    public static final Parcelable.Creator<Plant> CREATOR = new Parcelable.Creator<Plant>(){
      @Override
      public Plant createFromParcel(Parcel source){
          return new Plant(source);
      }
      @Override
        public Plant[] newArray(int size){
          return new Plant[size];
      }
    };

    public void water(){
        LocalDate date = LocalDate.now().plusDays(getWateringFrequency());
        setNextWateringDate(date);
        setDaysRemaining(getWateringFrequency());
    }
    
    public void computeDaysRemaining(){
        int daysRem = ((int) LocalDate.now().until(getNextWateringDate(), ChronoUnit.DAYS));
        if (daysRem < 0){
            daysRem = 0;
        }
        setDaysRemaining(daysRem);
    }

    public Drawable getIcon(){
        return this.icon;
    }

    public void setIcon(Drawable drawable){
        this.icon = drawable;
    }

    public String getPlantName(){
        return plantName;
    }
    public void setPlantName(String plantName){
        this.plantName = plantName;
    }

    public int getDaysRemaining(){
        return daysRemaining;
    }
    public void setDaysRemaining(int daysRemaining){
        this.daysRemaining = daysRemaining;
    }

    public int getIconIdx(){
        return iconIdx;
    }
    public void setIconIdx(int iconIdx){
        this.iconIdx = iconIdx;
    }

    public LocalDate getNextWateringDate() {
        return nextWateringDate;
    }
    public void setNextWateringDate(LocalDate nextWateringDate) {
        this.nextWateringDate = nextWateringDate;
    }

    public int getWateringFrequency() {
        return wateringFrequency;
    }
    public void setWateringFrequency(int wateringFrequency) {
        this.wateringFrequency = wateringFrequency;
    }
}
