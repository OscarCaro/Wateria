package com.example.userinterface.DataStructures;


import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collections;

public class PlantList extends ArrayList {
    private ArrayList<Plant> plantList;

    public PlantList(){
        plantList = new ArrayList<Plant>();
    }

    public int waterPlant (int position){
        Plant currentPlant = plantList.get(position);
        LocalDate date = LocalDate.now().plusDays(currentPlant.getWateringFrequency());
        currentPlant.setNextWateringDate(date);
        currentPlant.setDaysRemaining(currentPlant.getWateringFrequency());
        plantList.set(position, currentPlant);

        Collections.sort(plantList);

        return plantList.indexOf(currentPlant);
    }

    public int insertPlant (Plant plant){
        // Compute daysRemaining of new:
        plant.setDaysRemaining(((int) LocalDate.now().until(plant.getNextWateringDate(), ChronoUnit.DAYS)));
        // Insert to the list:
        plantList.add(plant);
        // Sort the list:
        Collections.sort(plantList);
        // Adapter:
        return plantList.indexOf(plant);
    }

    public int removePlant(int position) {
        if (position >= 0 && position < plantList.size()){
            plantList.remove(position);
            return position;
        }
        else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public int modifyPlant ( Plant plant, Integer prevPosInPlantList){
        if (prevPosInPlantList >= 0 && prevPosInPlantList < plantList.size()){
            // Compute daysRemaining of new:
            plant.setDaysRemaining(((int) LocalDate.now().until(plant.getNextWateringDate(), ChronoUnit.DAYS)));
            plantList.set(prevPosInPlantList, plant);
            Collections.sort(plantList);
            return plantList.indexOf(plant);
        }
        else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

}
