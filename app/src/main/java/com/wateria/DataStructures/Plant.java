package com.wateria.DataStructures;


import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;

import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

public class Plant implements Comparable<Plant>, Parcelable {

    private String plantName;
    private int iconId;
    private LocalDate nextWateringDate;
    private int wateringFrequency;
    // Not stored on sharedPrefs, computed dinamically:
    private int daysRemaining;
    private Drawable icon;

    public Plant (String plantName, int iconId, int wateringFrequency, LocalDate nextWateringDate){
        this.plantName = plantName;
        this.iconId = iconId;
        this.wateringFrequency = wateringFrequency;
        this.nextWateringDate = nextWateringDate;
    }

    public Plant (String plantName, int iconId, int wateringFrequency, LocalDate nextWateringDate, Drawable icon){
        this.plantName = plantName;
        this.iconId = iconId;
        this.wateringFrequency = wateringFrequency;
        this.nextWateringDate = nextWateringDate;
        this.icon = icon;
    }

    public Plant (Parcel source){
        this.plantName = source.readString();
        this.iconId = source.readInt();
        this.nextWateringDate = LocalDate.of(source.readInt(), source.readInt(), source.readInt());
        this.wateringFrequency = source.readInt();
        this.daysRemaining = source.readInt();
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(plantName);
        dest.writeInt(iconId);
        dest.writeInt(nextWateringDate.getYear());
        dest.writeInt(nextWateringDate.getMonthValue());
        dest.writeInt(nextWateringDate.getDayOfMonth());
        dest.writeInt(wateringFrequency);
        dest.writeInt(daysRemaining);
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

    @Override
    public boolean equals(@Nullable Object o) {             // For find function in plantList
        if (o instanceof Plant){
            if (((Plant) o).daysRemaining == this.daysRemaining && ((Plant) o).iconId == this.iconId
            && ((Plant) o).nextWateringDate.equals(this.nextWateringDate) && ((Plant) o).plantName.equals(this.plantName)
            && ((Plant) o).wateringFrequency == this.wateringFrequency){
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    @Override
    public int compareTo(Plant comparePlant){
        int compareDays = ((Plant)comparePlant).getDaysRemaining();
        return this.daysRemaining - compareDays;
    }

    public int describeContents(){
        return 0;
    }

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

    public int getIconId(){
        return iconId;
    }
    public void setIconId(int iconId){
        this.iconId = iconId;
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
