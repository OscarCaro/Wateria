package com.example.userinterface;


import org.threeten.bp.LocalDate;

public class Plant implements Comparable<Plant>{

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

    @Override
    public int compareTo(Plant comparePlant){
        int compareDays = ((Plant)comparePlant).getDaysRemaining();
        return this.daysRemaining - compareDays;
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
