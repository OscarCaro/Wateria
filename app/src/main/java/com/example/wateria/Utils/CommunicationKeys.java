package com.example.wateria.Utils;

public class CommunicationKeys {

    // The format to be used for communication between activities: Sender_Receiver_Attribute

    // MAIN - NEWPLANT
    public static final int Main_NewPlant_RequestCode = 1;
    public static final String NewPlant_Main_PlantPos = "intent_plant_pos";

    // MAIN - EDITPLANT
    public static final int Main_EditPlant_RequestCode = 2;
    public static final String Main_EditPlant_ExtraPlantPosition = "intent_plant_to_edit_position";
    public static final String EditPlant_Main_PlantPrevPosition = "intent_edited_plant_prev_position";
    public static final String EditPlant_Main_PlantNewPosition = "intent_edited_plant_new_position";
    public static final int EditPlant_Main_ResultDelete = 3;

    // MAIN - SETTINGS
    public static final int Main_Settings_RequestCode = 4;
    public static final int Settings_Main_ResultDeleteAll = 5;

    // NOTIFICATIONCLASS - WaterSinglePlantFromNotificationActionService
    public static final String NotificationClass_WaterSinglePlantService_PlantToWater = "intent_plant_to_water";


}
