package com.wateria.legacy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.wateria.DataStructures.Plant;
import com.wateria.Utils.JsonEncoder;

import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;

public class LegacyPreferencesFixtureTest {

    @Test
    public void representativeSnapshotUsesTheEightLegacyPreferenceKeysAndTypes() throws Exception {
        JSONObject preferences = new JSONObject(LegacyFixtureLoader.load("representative_preferences.json"));

        assertEquals(8, preferences.length());
        assertTrue(preferences.get("plantlistkey") instanceof String);
        assertTrue(preferences.getBoolean("notif_enabled"));
        assertEquals(18, preferences.getInt("notif_hour"));
        assertEquals(0, preferences.getInt("notif_minute"));
        assertEquals(1, preferences.getInt("notif_repetition"));
        assertFalse(preferences.getBoolean("first_time"));
        assertEquals(4, preferences.getInt("tip_idx"));
        assertEquals(198, preferences.getInt("last_day"));

        ArrayList<Plant> plants = JsonEncoder.readPlantList(preferences.getString("plantlistkey"));
        assertEquals(2, plants.size());
        assertEquals("Living room Monstera", plants.get(0).getPlantName());
        assertEquals("Áloe", plants.get(1).getPlantName());
    }

    @Test
    public void corruptSettingsSnapshotPreservesRawBoundaryInputsForMigrationTests() throws Exception {
        JSONObject preferences = new JSONObject(LegacyFixtureLoader.load("invalid_settings_preferences.json"));

        assertEquals(-1, preferences.getInt("notif_hour"));
        assertEquals(60, preferences.getInt("notif_minute"));
        assertEquals(24, preferences.getInt("notif_repetition"));
        assertEquals(-3, preferences.getInt("tip_idx"));
        assertEquals(367, preferences.getInt("last_day"));
    }
}
