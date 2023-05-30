package frc.robot.lib.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Used to log fields and methods(Can't have parameters 
 * and must return something) to network tables for viewing or editing(in the case of fields). <p>
 * <b> Static fields and methods will be automatically retrieved </b> but if 
 * you want instance fields or methods you must provide an instance to back the values.
 * Use {@link NTLogger#addInstance(Object obj)} to supply these to the Logger.
 * @author WispySparks
 * 
 */
public final class NTLogger {

    private static NetworkTable mainTable = NetworkTableInstance.getDefault().getTable("Logging");
    private static Map<Integer, Loggable> indexedLoggables = new HashMap<>();

    private NTLogger() {}

    /**
     * Call this in robot periodic to log everything to network tables.
     */
    public static void log() {
        indexedLoggables.forEach((index, loggable) -> {
            NetworkTable table = mainTable.getSubTable(loggable.getClass().getSimpleName() + "-" + index);
            loggable.log().forEach((name, val) -> {
                NetworkTableEntry entry = table.getEntry(name);
                try {
                    entry.setValue(val);
                } catch (IllegalArgumentException e) {
                    entry.setString(val.toString());
                }
            });
        });
    }

    public static void register(Loggable obj) {
        int index = indexedLoggables.values()
            .stream()
            .filter((value) -> value.getClass().equals(obj.getClass()))
            .collect(Collectors.toList())
            .size();
        indexedLoggables.put(index, obj);
    }

}
