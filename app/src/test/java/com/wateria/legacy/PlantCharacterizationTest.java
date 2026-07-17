package com.wateria.legacy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.wateria.DataStructures.Plant;

import org.junit.Test;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;

public class PlantCharacterizationTest {

    @Test
    public void computeDaysRemainingClampsOverduePlantsToZero() {
        Plant plant = new Plant("Overdue", 1, 5, LocalDate.now().minusDays(3));

        plant.computeDaysRemaining();

        assertEquals(0, plant.getDaysRemaining());
    }

    @Test
    public void computedDaysRemainAStaleSnapshotUntilRecomputed() {
        Plant plant = new Plant("Snapshot", 1, 5, LocalDate.now().plusDays(2));
        plant.computeDaysRemaining();
        assertEquals(2, plant.getDaysRemaining());

        plant.setNextWateringDate(LocalDate.now().plusDays(8));

        assertEquals(2, plant.getDaysRemaining());
        plant.computeDaysRemaining();
        assertEquals(8, plant.getDaysRemaining());
    }

    @Test
    public void wateringSchedulesFromTodayAndSetsFrequencyAsDaysRemaining() {
        int frequency = 6;
        Plant plant = new Plant("Water me", 1, frequency, LocalDate.now().minusDays(1));
        LocalDate before = LocalDate.now();

        plant.water();

        LocalDate after = LocalDate.now();
        assertTrue(
                plant.getNextWateringDate().equals(before.plusDays(frequency))
                        || plant.getNextWateringDate().equals(after.plusDays(frequency))
        );
        assertEquals(frequency, plant.getDaysRemaining());
    }

    @Test
    public void naturalOrderingUsesOnlyDaysRemaining() {
        Plant later = new Plant("A", 1, 5, LocalDate.now());
        later.setDaysRemaining(7);
        Plant sooner = new Plant("Z", 2, 9, LocalDate.now());
        sooner.setDaysRemaining(1);
        ArrayList<Plant> plants = new ArrayList<>();
        plants.add(later);
        plants.add(sooner);

        Collections.sort(plants);

        assertEquals(sooner, plants.get(0));
        assertEquals(later, plants.get(1));
    }

    @Test
    public void plantsWithEqualDaysCompareAsEqualRegardlessOfOtherFields() {
        Plant first = new Plant("A", 1, 1, LocalDate.of(2026, 7, 17));
        Plant second = new Plant("Z", 999, 40, LocalDate.of(2030, 1, 1));
        first.setDaysRemaining(4);
        second.setDaysRemaining(4);

        assertEquals(0, first.compareTo(second));
        assertEquals(0, second.compareTo(first));
    }

    @Test
    public void equalityIncludesEveryMutablePersistedFieldAndDaysSnapshot() {
        LocalDate date = LocalDate.of(2026, 7, 17);
        Plant first = new Plant("Same", 10, 5, date);
        Plant second = new Plant("Same", 10, 5, date);
        first.setDaysRemaining(3);
        second.setDaysRemaining(3);

        assertTrue(first.equals(second));

        second.setDaysRemaining(2);

        assertFalse(first.equals(second));
    }
}
