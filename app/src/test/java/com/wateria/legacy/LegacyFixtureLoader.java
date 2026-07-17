package com.wateria.legacy;

import java.io.InputStream;
import java.util.Scanner;

final class LegacyFixtureLoader {

    private LegacyFixtureLoader() {
    }

    static String load(String fileName) {
        String resourceName = "legacy/" + fileName;
        InputStream inputStream = LegacyFixtureLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (inputStream == null) {
            throw new AssertionError("Missing test fixture: " + resourceName);
        }

        try (Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
