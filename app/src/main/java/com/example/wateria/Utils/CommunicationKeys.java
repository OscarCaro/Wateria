package com.example.wateria.Utils;

public class CommunicationKeys {

    // The format to be used for communication between activities: Sender_Receiver_Attribute

    // MAIN - NEWPLANT
    public static final int Main_NewPlant_RequestCode = 1;
    public static final String NewPlant_Main_ExtraPlant = "intent_plant";

    // MAIN - EDITPLANT
    public static final int Main_EditPlant_RequestCode = 2;
    public static final String Main_EditPlant_ExtraPlantToEdit = "intent_plant_to_edit";
    public static final String Main_EditPlant_ExtraPlantPosition = "intent_plant_to_edit_position";
    public static final String EditPlant_Main_ExtraPlantEdited = "intent_edited_plant";
    public static final String EditPlant_Main_DaysRemChanged = "intent_days_rem";
    public static final String EditPlant_Main_ExtraPlantEditedPosition = "intent_edited_plant_position";
    public static final int EditPlant_Main_ResultDelete = 3;

    // NOTIFICATIONCLASS - WaterSinglePlantFromNotificationActionService
    public static final String NotificationClass_WaterSinglePlantService_PlantToWater = "intent_plant_to_water";


}
