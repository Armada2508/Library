package frc.robot.lib.logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.geometry.Twist3d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.ProtobufPublisher;
import edu.wpi.first.networktables.ProtobufTopic;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.networktables.StructTopic;
import edu.wpi.first.util.protobuf.Protobuf;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.WrapperCommand;
import us.hebi.quickbuf.ProtoMessage;

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
    private static Map<String, Object> loggingMap = new HashMap<>();
    private static List<StructPublisher<?>> structPublishers = new ArrayList<>();
    private static List<ProtobufPublisher<?>> protobufPublishers = new ArrayList<>();

    private NTLogger() {}

    /**
     * Convenience method to start the data log manager, log driver station, joystick data and command interrupts.
     */
    public static void initDataLogger() {
        DataLogManager.start();
		DriverStation.startDataLog(DataLogManager.getLog());
        CommandScheduler.getInstance().onCommandInterrupt((interruptedCommand, interrupter) -> {
            Command interruptingCommand = interrupter.orElseGet(Commands::none);
            DataLogManager.log("Command: " + interruptedCommand.getName() + " was interrupted by " + interruptingCommand.getName() + ".");
            schedulerTable.getEntry("Last Interrupted Command").setString(interruptedCommand.getName());
            schedulerTable.getEntry("Last Interrupting Command").setString(interruptedCommand.getName());
        });
    }

     /**
     * Registers an object to the logger who's 'log' method will be called.
     * @param obj to register
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
     * Call this in robot periodic to log all registered objects, extra driver station data
     * and command interrupts to network tables.
     */
    public static void logEverything() {
        logDriverStation();
        indexedLoggables.forEach((loggable, index) -> {
            NetworkTable table = getLoggablesTable(loggable, index);
            loggingMap.clear();
            loggable.log(loggingMap).forEach((name, val) -> logValue(table, name, val));
        });
    }

    /**
     * Logs an invidual value to the loggable's network table. Useful for logging one off values inside of methods. 
     * @param loggable used to find the right network table
     * @param name for value
     * @param val to log
     */
    public static void log(Loggable loggable, String name, Object val) {
        int index = indexedLoggables.get(loggable);
        NetworkTable table = getLoggablesTable(loggable, index);
        logValue(table, name, val);
    }

    /**
     * Gets a loggable's network table to log to
     * @param loggable to get network table for
     * @param index of loggable, counts up for every instance
     * @return loggable's network table 
     */
    private static NetworkTable getLoggablesTable(Loggable loggable, int index) {
        return (index == 0) ? mainTable.getSubTable(loggable.getClass().getSimpleName()) : 
            mainTable.getSubTable(loggable.getClass().getSimpleName() + "-" + index);
    }

    /**
     * Logs a value into a network table, correctly logs structs and protobufs. 
     * If it's not a supported type it just calls {@link Object#toString()}. 
     * @param table to log to
     * @param name for value
     * @param val to log
     */
    private static void logValue(NetworkTable table, String name, Object val) {
        if (name == null || val == null) return;
        Optional<Struct<Object>> struct = getStruct(val);
        Optional<Protobuf<Object, ProtoMessage<?>>> protobuf = getProtobuf(val);
        if (struct.isPresent()) {
            logStruct(table, name, val, struct.get());
            return;
        }
        if (protobuf.isPresent()) {
            logProtobuf(table, name, val, protobuf.get());
            return;
        }
        NetworkTableEntry entry = table.getEntry(name);
        try {
            entry.setValue(val);
        } catch (IllegalArgumentException e) {
            entry.setString(val.toString());
        }
    }

    /**
     * Fills your map with TalonFX StatusSignals.
     * @param talon to log
     * @param name of talon for logging
     * @param map to fill
     * @return the map passed in for method chaining
     */
    public static Map<String, Object> putTalonLog(TalonFX talon, String name, Map<String, Object> map) {
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
        return map;
    }

    /**
     * Fills your map with TalonFX StatusSignals, name defaults to the TalonFX's device ID.
     * @param talon to log
     * @param map to fill
     * @return the map passed in for method chaining
     */
    public static Map<String, Object> putTalonLog(TalonFX talon, Map<String, Object> map) {
        int ID = talon.getDeviceID();
        return putTalonLog(talon, "TalonFX " + ID, map);
    }
    
    /**
     * Fills your map with values to log on a subsystem.
     * @param subsystem to log
     * @param map to fill
     * @return the map passed in for method chaining
     */
    public static Map<String, Object> putSubsystemLog(Subsystem subsystem, Map<String, Object> map) {
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
        map.put("Subsystem: _Name", subsystem.getName());
        map.put("Subsystem: Default Command", subsystem.getDefaultCommand() == null ? "None" : subsystem.getDefaultCommand().getName());
        map.put("Subsystem: Current Command", currentCommand == null ? "None" : currentCommand.getName());
        map.put("Subsystem: Command Group Current Command", commandGroupCurrentCommand);
        return map;
    }

    /**
     * Logs a value that uses structs to network tables. 
     * @param table network table to log value to
     * @param name for value
     * @param objToLog object that will be logged as a struct
     * @param struct implementation that will be used for network tables to log object
     */
    @SuppressWarnings("unchecked")
    private static <T> void logStruct(NetworkTable table, String name, T objToLog, Struct<T> struct) {
        StructTopic<T> topic = table.getStructTopic(name, struct);
        for (StructPublisher<?> publisher : structPublishers) {
            if (publisher.getTopic().equals(topic)) {
                ((StructPublisher<T>) publisher).set(objToLog);
                return;
            }
        }
        StructPublisher<T> publisher = topic.publish();
        publisher.set(objToLog);    
        structPublishers.add(publisher);
    }

    /**
     * Gets the corresponding struct implementation for an object e.g. Pose2d.struct for a Pose2d.
     * @param obj object to get struct for
     * @return An object's struct implementation or empty if there is none
     */
    @SuppressWarnings("unchecked")
    private static <T> Optional<Struct<T>> getStruct(T obj) {
        Struct<?> struct = null;
        if (obj instanceof Pose2d) struct = Pose2d.struct;
        if (obj instanceof Pose3d) struct = Pose3d.struct;
        if (obj instanceof Rotation2d) struct = Rotation2d.struct;
        if (obj instanceof Rotation3d) struct = Rotation3d.struct;
        if (obj instanceof Translation2d) struct = Translation2d.struct;
        if (obj instanceof Translation3d) struct = Translation3d.struct;
        if (obj instanceof Transform2d) struct = Transform2d.struct;
        if (obj instanceof Transform3d) struct = Transform3d.struct;
        if (obj instanceof Twist2d) struct = Twist2d.struct;
        if (obj instanceof Twist3d) struct = Twist3d.struct;
        return Optional.ofNullable((Struct<T>) struct);
    }

    /**
     * Logs a value that uses protobufs to network tables. 
     * @param table network table to log value to
     * @param name for value
     * @param objToLog object that will be logged as a protobuf
     * @param protobuf implementation that will be used for network tables to log object
     */
    @SuppressWarnings("unchecked")
    private static <T, U extends ProtoMessage<?>> void logProtobuf(NetworkTable table, String name, T objToLog, Protobuf<T, U> protobuf) {
        ProtobufTopic<T> topic = table.getProtobufTopic(name, protobuf);
        for (ProtobufPublisher<?> publisher : protobufPublishers) {
            if (publisher.getTopic().equals(topic)) {
                ((ProtobufPublisher<T>) publisher).set(objToLog);
                return;
            }
        }
        ProtobufPublisher<T> publisher = topic.publish();
        publisher.set(objToLog);    
        protobufPublishers.add(publisher);
    }

    /**
     * Gets the corresponding protobuf implementation for an object e.g. Pose2d.proto for a Pose2d.
     * @param obj object to get struct for
     * @return An object's protobuf implementation or empty if there is none
     */
    @SuppressWarnings("unchecked")
    private static <T, U extends ProtoMessage<?>> Optional<Protobuf<T, U>> getProtobuf(T obj) {
        Protobuf<?, ?> protobuf = null;
        if (obj instanceof Trajectory) protobuf = Trajectory.proto;
        return Optional.ofNullable((Protobuf<T, U>) protobuf);
    }

    /**
     * Logs common Driver Station data like robot mode and match time etc.
     */
    private static void logDriverStation() {
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

}
