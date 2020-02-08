package com.example.wateria.DataStructures;


import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.LocalDate;

public class Plant implements Comparable<Plant>, Parcelable {

    private String plantName;
    private int daysRemaining;
    private int iconIdx;
    private LocalDate nextWateringDate;
    private int wateringFrequency;
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
