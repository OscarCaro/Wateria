package com.wateria.DataStructures;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

/** Frozen test fixture for the legacy preference format. Not part of the production runtime. */
public final class Plant implements Comparable<Plant> {
    private String plantName;
    private int iconId;
    private LocalDate nextWateringDate;
    private int wateringFrequency;
    private int daysRemaining;

    public Plant(String plantName, int iconId, int wateringFrequency, LocalDate nextWateringDate) {
        this.plantName = plantName;
        this.iconId = iconId;
        this.wateringFrequency = wateringFrequency;
        this.nextWateringDate = nextWateringDate;
    }

    @Override
    public boolean equals(Object value) {
        if (!(value instanceof Plant)) {
            return false;
        }
        Plant other = (Plant) value;
        return daysRemaining == other.daysRemaining
                && iconId == other.iconId
                && nextWateringDate.equals(other.nextWateringDate)
                && plantName.equals(other.plantName)
                && wateringFrequency == other.wateringFrequency;
    }

    @Override
    public int compareTo(Plant other) {
        return daysRemaining - other.daysRemaining;
    }

    public void water() {
        nextWateringDate = LocalDate.now().plusDays(wateringFrequency);
        daysRemaining = wateringFrequency;
    }

    public void computeDaysRemaining() {
        daysRemaining = Math.max(0, (int) LocalDate.now().until(nextWateringDate, ChronoUnit.DAYS));
    }

    public String getPlantName() { return plantName; }
    public void setPlantName(String value) { plantName = value; }
    public int getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(int value) { daysRemaining = value; }
    public int getIconId() { return iconId; }
    public void setIconId(int value) { iconId = value; }
    public LocalDate getNextWateringDate() { return nextWateringDate; }
    public void setNextWateringDate(LocalDate value) { nextWateringDate = value; }
    public int getWateringFrequency() { return wateringFrequency; }
    public void setWateringFrequency(int value) { wateringFrequency = value; }
}
