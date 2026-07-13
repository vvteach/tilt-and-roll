package com.codex.gravitytilt.levels;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public final class LevelCatalog {
    private final Array<LevelDefinition> levels = new Array<>();

    public LevelCatalog(FileHandle file) {
        JsonValue root = new JsonReader().parse(file);
        JsonValue levelValues = root.get("levels");
        for (JsonValue level = levelValues.child; level != null; level = level.next) {
            float[] start = readVector(level.get("start"), 2);
            levels.add(new LevelDefinition(
                    start[0],
                    start[1],
                    readRectangles(level.get("platforms")),
                    readVectors(level.get("coins"), 2),
                    readRectangles(level.get("hazards")),
                    readOptionalRectangles(level.get("springs")),
                    readVector(level.get("goal"), 4)));
        }
    }

    public LevelDefinition get(int index) {
        return levels.get(index);
    }

    public int size() {
        return levels.size;
    }

    private static float[][] readRectangles(JsonValue values) {
        return readVectors(values, 4);
    }

    private static float[][] readOptionalRectangles(JsonValue values) {
        if (values == null) {
            return new float[0][];
        }
        return readRectangles(values);
    }

    private static float[][] readVectors(JsonValue values, int expectedSize) {
        float[][] result = new float[values.size][];
        int index = 0;
        for (JsonValue value = values.child; value != null; value = value.next) {
            result[index++] = readVector(value, expectedSize);
        }
        return result;
    }

    private static float[] readVector(JsonValue value, int expectedSize) {
        float[] result = new float[expectedSize];
        for (int i = 0; i < expectedSize; i++) {
            result[i] = value.getFloat(i);
        }
        return result;
    }
}
