package frc.robot.lib.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;

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
     * Convenience method to start the data log manager in a good directory and log driver station and joystick data.
     */
    public static void initDataLogger() {
        DataLogManager.start();
		DriverStation.startDataLog(DataLogManager.getLog());
    }

    /**
     * Call this in robot periodic to log all registered objects to network tables.
     */
    public static void log() {
        indexedLoggables.forEach((index, loggable) -> {
            NetworkTable table = mainTable.getSubTable(loggable.getClass().getSimpleName() + "-" + index);
            Map<String, Object> map = new HashMap<>();
            loggable.log(map).forEach((name, val) -> {
                NetworkTableEntry entry = table.getEntry(name);
                try {
                    entry.setValue(val);
                } catch (IllegalArgumentException e) {
                    entry.setString(val.toString());
                }
            });
        });
    }

    /**
     * Registers an object to the logger who's 'log' method will be called.
     * @param obj
     */
    public static void register(Loggable obj) {
        int index = indexedLoggables.values()
            .stream()
            .filter((value) -> value.getClass().equals(obj.getClass()))
            .collect(Collectors.toList())
            .size();
        indexedLoggables.put(index, obj);
    }

    /**
     * Use this method to get a hashmap with logged values of a talon to merge/add to your hashmap in your loggable's log method.
     * @param talon to log
     * @return
     */
    public static Map<String, Object> getTalonLog(TalonFX talon) {
        Map<String, Object> map = new HashMap<>();
        int ID = talon.getDeviceID();
        map.put("TalonFX " + ID + ": ControlMode", talon.getControlMode());
        map.put("TalonFX " + ID + ": FwdLimitSwitchClosed", talon.getForwardLimit());
        map.put("TalonFX " + ID + ": RevLimitSwitchClosed", talon.getReverseLimit());
        map.put("TalonFX " + ID + ": SensorPosition", talon.getPosition());
        map.put("TalonFX " + ID + ": SensorVelocity", talon.getVelocity());
        map.put("TalonFX " + ID + ": ClosedLoopTarget", talon.getClosedLoopReference());
        map.put("TalonFX " + ID + ": SupplyCurrent", talon.getSupplyCurrent());
        map.put("TalonFX " + ID + ": Temperature", talon.getDeviceTemp());
        return map;
    }

}
