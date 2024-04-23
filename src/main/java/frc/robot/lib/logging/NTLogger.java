package frc.robot.lib.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.WrapperCommand;

/**
 * Used to log fields to network tables and data log for viewing during testing and after a match. <p>
 * You must register your object using {@link NTLogger#register(Loggable obj)} for its values to be 
 * put onto network tables when running {@link NTLogger#log()}. <p>
 * {@link NTLogger#log()} should be called in Robot Periodic.
 * @author WispySparks
 * 
 */
public final class NTLogger {

    private static NetworkTable mainTable = NetworkTableInstance.getDefault().getTable("Logging");
    private static NetworkTable schedulerTable = mainTable.getSubTable("Command Scheduler");
    private static Map<Loggable, Integer> indexedLoggables = new HashMap<>();
    private static Map<Loggable, Map<String, Object>> loggablesMaps = new HashMap<>();

    private NTLogger() {}

    /**
     * Convenience method to start the data log manager and log driver station and joystick data.
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
        logCommandInterrupts();
        indexedLoggables.forEach((loggable, index) -> {
            NetworkTable table = (index == 0) ? mainTable.getSubTable(loggable.getClass().getSimpleName()) : 
                mainTable.getSubTable(loggable.getClass().getSimpleName() + "-" + index);
            loggable.log(loggablesMaps.get(loggable)).forEach((name, val) -> {
                if (name == null || val == null) return;
                if (val instanceof Pose2d pose) {
                    logPose2d(table, name, pose);
                    return;
                }
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
        loggablesMaps.put(obj, new HashMap<>());
    }

    /**
     * Use this method to fill your map with talon status signals
     * @param talon to log
     */
    public static void putTalonLog(TalonFX talon, String name, Map<String, Object> map) {
        map.put(name + ": Device ID", talon.getDeviceID());
        map.put(name + ": Control Mode", talon.getControlMode().getValue().toString());
        map.put(name + ": Rotor Polarity", talon.getAppliedRotorPolarity().getValue().name());
        map.put(name + ": Fwd Limit Switch", talon.getForwardLimit().getValue().toString());
        map.put(name + ": Rev Limit Switch", talon.getReverseLimit().getValue().toString());
        map.put(name + ": Position (Rots)", talon.getPosition().getValueAsDouble());
        map.put(name + ": Velocity (Rots\\s)", talon.getVelocity().getValueAsDouble());
        map.put(name + ": Acceleration (Rots\\s^2)", talon.getAcceleration().getValueAsDouble());
        map.put(name + ": Closed Loop Target", talon.getClosedLoopReference().getValueAsDouble());
        map.put(name + ": Closed Loop Slot", talon.getClosedLoopSlot().getValue().intValue());
        map.put(name + ": Supply Voltage (V)", talon.getSupplyVoltage().getValueAsDouble());
        map.put(name + ": Motor Voltage (V)", talon.getMotorVoltage().getValueAsDouble());
        map.put(name + ": Supply Current (A)", talon.getSupplyCurrent().getValueAsDouble());
        map.put(name + ": Torque Current (A)", talon.getTorqueCurrent().getValueAsDouble());
        map.put(name + ": Device Temperature (C)", talon.getDeviceTemp().getValueAsDouble());
        map.put(name + ": Has Reset Occurred", talon.hasResetOccurred());
    }

    public static void putTalonLog(TalonFX talon, Map<String, Object> map) {
        int ID = talon.getDeviceID();
        putTalonLog(talon, "TalonFX " + ID, map);
    }
    
    /**
     * Fills your map with subsystem logged values
     * @param subsystem to log
     */
    public static void putSubsystemLog(Subsystem subsystem, Map<String, Object> map) {
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
        map.put("_Name", subsystem.getName());
        map.put("Default Command", subsystem.getDefaultCommand() == null ? "None" : subsystem.getDefaultCommand().getName());
        map.put("Current Command", currentCommand == null ? "None" : currentCommand.getName());
        map.put("Command Group Current Command", commandGroupCurrentCommand);
    }

    private static void logPose2d(NetworkTable table, String name, Pose2d pose) {
        table.getStructTopic(name, Pose2d.struct).publish().set(pose);
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
        mainTable.getEntry("_DS Mode").setString(mode);
        mainTable.getEntry("_Robot Enabled").setBoolean(DriverStation.isEnabled());
        mainTable.getEntry("_Match Time").setDouble(DriverStation.getMatchTime());
        mainTable.getEntry("_is FMS Attached").setBoolean(DriverStation.isFMSAttached());
    }

    private static void logCommandInterrupts() {
        CommandScheduler.getInstance().onCommandInterrupt((interruptedCommand, interrupter) -> {
            Command interruptingCommand = interrupter.orElseGet(Commands::none);
            DataLogManager.log("Command: " + interruptedCommand.getName() + " was interrupted by " + interruptingCommand.getName() + ".");
            schedulerTable.getEntry("Last Interrupted Command").setString(interruptedCommand.getName());
            schedulerTable.getEntry("Last Interrupting Command").setString(interruptedCommand.getName());
        });
    }

}
