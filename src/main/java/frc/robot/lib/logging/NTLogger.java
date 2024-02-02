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
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.WrapperCommand;

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
    private static Map<Loggable, Integer> indexedLoggables = new HashMap<>();

    private NTLogger() {}

    /**
     * Convenience method to start the data log manager in a directory and log driver station and joystick data.
     */
    public static void initDataLogger() {
        DataLogManager.start();
		DriverStation.startDataLog(DataLogManager.getLog());
    }

    /**
     * Call this in robot periodic to log all registered objects to network tables.
     */
    public static void log() {
        logDS();
        indexedLoggables.forEach((loggable, index) -> {
            NetworkTable table = mainTable.getSubTable(loggable.getClass().getSimpleName() + "-" + index);
            Map<String, Object> map = new HashMap<>();
            loggable.log(map).forEach((name, val) -> {
                if (name == null || val == null) return;
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
        indexedLoggables.put(obj, index);
    }

    /**
     * Use this method to get a hashmap with logged values of a talon to merge/add to your hashmap in your loggable's log method.
     * @param talon to log
     * @return
     */
    public static Map<String, Object> getTalonLog(TalonFX talon) {
        Map<String, Object> map = new HashMap<>();
        int ID = talon.getDeviceID();
        map.put("TalonFX " + ID + ": Control Mode", talon.getControlMode());
        map.put("TalonFX " + ID + ": Fwd Limit Switch", talon.getForwardLimit());
        map.put("TalonFX " + ID + ": Rev Limit Switch", talon.getReverseLimit());
        map.put("TalonFX " + ID + ": Position", talon.getPosition());
        map.put("TalonFX " + ID + ": Velocity", talon.getVelocity());
        map.put("TalonFX " + ID + ": Acceleration", talon.getAcceleration());
        map.put("TalonFX " + ID + ": Closed Loop Target", talon.getClosedLoopReference());
        map.put("TalonFX " + ID + ": Closed Loop Slot", talon.getClosedLoopSlot());
        map.put("TalonFX " + ID + ": Supply Voltage", talon.getSupplyVoltage());
        map.put("TalonFX " + ID + ": Supply Current", talon.getSupplyCurrent());
        map.put("TalonFX " + ID + ": Torque Current", talon.getTorqueCurrent());
        map.put("TalonFX " + ID + ": Temperature", talon.getDeviceTemp());
        return map;
    }

    public static Map<String, Object> getSubsystemLog(Subsystem subsystem) {
        Map<String, Object> map = new HashMap<>();
        Command currentCommand = subsystem.getCurrentCommand();
        Command innerCommand = Commands.none();
        String commandGroupCurrentCommand = "None";
        if (currentCommand instanceof WrapperCommand cmd) {
            innerCommand = LogUtil.getWrapperCommandInner(cmd);
        }
        if (innerCommand instanceof SequentialCommandGroup group) {
            commandGroupCurrentCommand = LogUtil.getSequentialCommandCurrentCommand(group).getName();
        }
        if (innerCommand instanceof ParallelCommandGroup group) {
            for (Command c : LogUtil.getParallelCommandCurrentCommands(group)) {
                commandGroupCurrentCommand += c.getName() + " ";
            }
        }
        map.put("Name", subsystem.getName());
        map.put("Default Command", subsystem.getDefaultCommand() == null ? "None" : subsystem.getDefaultCommand().getName());
        map.put("Current Command", currentCommand == null ? "None" : currentCommand.getName());
        map.put("Command Group Current Command", commandGroupCurrentCommand);
        return map;
    }

    private static void logDS() {
        String mode = "Unknown";
        if (DriverStation.isTeleop()) {
            mode = "Teleop";
        }
        else if (DriverStation.isAutonomous()) {
            mode = "Autonomous";
        } 
        else if (DriverStation.isTest()) {
            mode = "Test";
        }
        String alliance = DriverStation.getAlliance().isPresent() ? DriverStation.getAlliance().get().toString() : "Unknown";
        mainTable.getEntry("_Alliance").setString(alliance);
        mainTable.getEntry("_Alliance Station").setString(DriverStation.getRawAllianceStation().toString());
        mainTable.getEntry("_DS Mode").setString(mode);
        mainTable.getEntry("_Robot Enabled").setBoolean(DriverStation.isEnabled());
        mainTable.getEntry("_Match Time").setDouble(DriverStation.getMatchTime());
        mainTable.getEntry("_Match Type").setString(DriverStation.getMatchType().toString());
        mainTable.getEntry("_Match Number").setInteger(DriverStation.getMatchNumber());
        mainTable.getEntry("_is FMS Attached").setBoolean(DriverStation.isFMSAttached());
    }

}
