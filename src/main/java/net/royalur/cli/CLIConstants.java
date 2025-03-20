package net.royalur.cli;

import net.royalur.lut.buffer.ValueType;
import net.royalur.model.GameSettings;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Constants used for the CLI.
 */
public class CLIConstants {

    private CLIConstants() {}

    public static final GameSettings[] COMMONLY_PLAYED = {
            GameSettings.FINKEL,
            GameSettings.BLITZ,
            GameSettings.MASTERS,
            GameSettings.ASEB
    };

    public static final Map<String, GameSettings> SETTINGS_BY_CLI_NAME = Map.of(
            "finkel", GameSettings.FINKEL,
            "finkel2p", GameSettings.FINKEL_2P,
            "masters4d", GameSettings.MASTERS_FOUR_DICE,
            "masters", GameSettings.MASTERS,
            "aseb", GameSettings.ASEB
    );

    public static final Map<String, ValueType> VALUE_TYPE_BY_CLI_NAME = Map.of(
            "u8", ValueType.UINT8,
            "u16", ValueType.UINT16,
            "u32", ValueType.UINT32,
            "u64", ValueType.UINT64,
            "f32", ValueType.FLOAT32,
            "f64", ValueType.FLOAT64,
            "percent16", ValueType.PERCENT16
    );

    public static String getCLIName(GameSettings settings) {
        return findKey("settings", SETTINGS_BY_CLI_NAME, settings);
    }

    public static String getCLIName(ValueType valueType) {
        return findKey("value type", VALUE_TYPE_BY_CLI_NAME, valueType);
    }

    public static String getCLINameOrNull(GameSettings settings) {
        return findKeyOrNull("settings", SETTINGS_BY_CLI_NAME, settings);
    }

    public static String getCLINameOrNull(ValueType valueType) {
        return findKeyOrNull("value type", VALUE_TYPE_BY_CLI_NAME, valueType);
    }

    private static <T> String findKey(String mapName, Map<String, T> map, T value) {
        String key = findKeyOrNull(mapName, map, value);
        if (key == null) {
            throw new IllegalArgumentException(
                    "Could not find " + mapName + " by name " + value + ". "
                            + "Available values are: " + map.keySet()
            );
        }
        return key;
    }

    private static <T> @Nullable String findKeyOrNull(String mapName, Map<String, T> map, T value) {
        for (Map.Entry<String, T> entry : map.entrySet()) {
            if (entry.getValue().equals(value))
                return entry.getKey();
        }
        return null;
    }
}
