package com.example.wateria.DataStructures;


import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.LocalDate;

public class Plant implements Comparable<Plant>, Parcelable {

    private String plantName;
    private int daysRemaining;
    private int imageCode;
    private LocalDate nextWateringDate;
    private int wateringFrequency;

    public Plant (String plantName, int imageCode, int wateringFrequency, LocalDate nextWateringDate){      // Sin daysRem
        this.plantName = plantName;
        this.imageCode = imageCode;
        this.wateringFrequency = wateringFrequency;
        this.nextWateringDate = nextWateringDate;
    }

    public Plant (Parcel source){
        this.plantName = source.readString();
        this.imageCode = source.readInt();
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
        dest.writeInt(imageCode);
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

    public int getImageCode(){
        return imageCode;
    }
    public void setImageCode(int imageCode){
        this.imageCode = imageCode;
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
