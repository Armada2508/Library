package frc.robot.lib.logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.Pair;
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

    private static final NetworkTable mainTable = NetworkTableInstance.getDefault().getTable("Logging");
    /**Holds a list of all objects that have logged values along with how many instances there are of that object. */
    private static final Map<Object, Integer> registeredObjects = new HashMap<>();
    private static final Map<TalonFX, List<Pair<String, StatusSignal<?>>>> talonfxSignals = new HashMap<>();
    private static final List<StructPublisher<?>> structPublishers = new ArrayList<>();
    private static final List<ProtobufPublisher<?>> protobufPublishers = new ArrayList<>();

    /**
     * Prevent this class from being instantiated.
     */
    private NTLogger() {}

    /**
     * Convenience method to start the data log manager, log driver station, joystick data and command interrupts.
     */
    public static void initDataLogger() {
        DataLogManager.start();
		DriverStation.startDataLog(DataLogManager.getLog());
        CommandScheduler.getInstance().onCommandInterrupt((interruptedCommand, interrupter) -> {
            NetworkTable schedulerTable = mainTable.getSubTable("Command Scheduler");
            Command interruptingCommand = interrupter.orElseGet(Commands::none);
            DataLogManager.log("Command: " + interruptedCommand.getName() + " was interrupted by " + interruptingCommand.getName() + ".");
            schedulerTable.getEntry("Last Interrupted Command").setString(interruptedCommand.getName());
            schedulerTable.getEntry("Last Interrupting Command").setString(interruptedCommand.getName());
        });
    }

    /**
     * Logs common Driver Station data like robot mode and match time etc.
     */
    public static void logDriverStation() {
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

    /** 
     * Logs a value to network tables. Supported types are those supported by network tables, 
     * types that have struct or protobuf implementations, TalonFX and Subsystem.
     * @param owner used to find the right network table to log under
     * @param name for value
     * @param val to log
     */
    public static void log(Object owner, String name, Object val) {
        if (!registeredObjects.containsKey(owner)) { // Adds object to map of not in it already
            int index = registeredObjects.values()
                .stream()
                .filter((value) -> value.getClass().equals(owner.getClass()))
                .collect(Collectors.toList())
                .size(); 
            registeredObjects.put(owner, index);
        }
        int index = registeredObjects.get(owner);
        NetworkTable table = (index == 0) ? mainTable.getSubTable(owner.getClass().getSimpleName()) : 
            mainTable.getSubTable(owner.getClass().getSimpleName() + "-" + index);
        logValue(table, name, val);
    }

    /**
     * Logs a value to network tables. Supported types are those supported by network tables, 
     * types that have struct or protobuf implementations, TalonFX and Subsystem.
     * If it's not a supported type it just calls {@link Object#toString()}. 
     * @param table to log to
     * @param name for value
     * @param val to log
     */
    private static void logValue(NetworkTable table, String name, Object val) {
        if (name == null || val == null) return;
        Optional<Struct<Object>> struct = getStructImpl(val);
        Optional<Protobuf<Object, ProtoMessage<?>>> protobuf = getProtobufImpl(val);
        if (struct.isPresent()) {
            logStruct(table, name, val, struct.get());
            return;
        }
        if (protobuf.isPresent()) {
            logProtobuf(table, name, val, protobuf.get());
            return;
        }
        if (val instanceof TalonFX talon) {
            logTalonFX(table, name, talon);
            return;
        }
        if (val instanceof Subsystem subsystem) {
            logSubystem(table, name, subsystem);
            return;
        }
        NetworkTableEntry entry = table.getEntry(name); // This creates an entry if it doesn't exist yet
        try {
            entry.setValue(val);
        } catch (IllegalArgumentException e) { 
            entry.setString(val.toString()); //? I probably should check type of val instead of waiting for an exception to be thrown
        }
    }

    /** 
     * Logs a TalonFX to network tables.
     * @param table to log to
     * @param name of talon for logging
     * @param talon to log
     */
    private static void logTalonFX(NetworkTable table, String name, TalonFX talon) {
        if (!talonfxSignals.containsKey(talon)) {
            talonfxSignals.put(talon, List.of(
                Pair.of("Control Mode", talon.getControlMode()),
                Pair.of("Rotor Polarity", talon.getAppliedRotorPolarity()),
                Pair.of("Fwd Limit Switch", talon.getForwardLimit()),
                Pair.of("Rev Limit Switch", talon.getReverseLimit()),
                Pair.of("Position (Rots)", talon.getPosition()),
                Pair.of("Velocity (Rots\\\\s)", talon.getVelocity()),
                Pair.of("Acceleration (Rots\\\\s^2)", talon.getAcceleration()),
                Pair.of("Closed Loop Target", talon.getClosedLoopReference()),
                Pair.of("Closed Loop Slot", talon.getClosedLoopSlot()),
                Pair.of("Supply Voltage (V)", talon.getSupplyVoltage()),
                Pair.of("Motor Voltage (V)", talon.getMotorVoltage()),
                Pair.of("Supply Current (A)", talon.getSupplyCurrent()),
                Pair.of("Torque Current (A)", talon.getTorqueCurrent()),
                Pair.of("Device Temperature (C)", talon.getDeviceTemp()),
                Pair.of("Firmware Version", talon.getVersion())
            ));
        }
        var signals = talonfxSignals.get(talon);
        // Turning list of pairs of strings and signals into just an array of signals
        BaseStatusSignal[] arr = new BaseStatusSignal[signals.size()];
        for (int i = 0; i < arr.length; i++) { 
            arr[i] = signals.get(i).getSecond();
        }
        BaseStatusSignal.refreshAll(arr); // Refresh all status signals for a TalonFX at once
        for (var pair : signals) { // Log each signal appropriately
            NetworkTableEntry entry = table.getEntry(name + ": " + pair.getFirst());
            Object value = pair.getSecond().getValue();
            if (value instanceof Double d) {
                entry.setDouble(d);
            }
            else if (value instanceof Integer i) {
                entry.setInteger(i);
            }
            else if (value instanceof Boolean b) {
                entry.setBoolean(b);
            }
            else {
                entry.setString(value.toString());
            }
        }
        table.getEntry(name + ": Device ID").setInteger(talon.getDeviceID());
        table.getEntry(name + ": Has Reset Occurred").setBoolean(talon.hasResetOccurred());
    }

    /**
     * Logs a subsystem to network tables.
     * @param table to log to
     * @param name for subsystem
     * @param subsystem to log
     */
    private static void logSubystem(NetworkTable table, String name, Subsystem subsystem) {
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
        table.getEntry(name + ": _Name").setString(subsystem.getName());
        table.getEntry(name + ": Default Command").setString(subsystem.getDefaultCommand() == null ? "None" : subsystem.getDefaultCommand().getName());
        table.getEntry(name + ": Current Command").setString(currentCommand == null ? "None" : currentCommand.getName());
        table.getEntry(name + ": Command Group Current Command").setString(commandGroupCurrentCommand);
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
     * If an object has both struct and protobuf, prefer struct as it's faster.
     * Gets the corresponding struct implementation for an object e.g. Pose2d.struct for a Pose2d.
     * @param obj object to get struct for
     * @return An object's struct implementation or empty if there is none
     */
    @SuppressWarnings("unchecked")
    private static <T> Optional<Struct<T>> getStructImpl(T obj) {
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
     * If an object has both struct and protobuf, prefer struct as it's faster.
     * Gets the corresponding protobuf implementation for an object e.g. Pose2d.proto for a Pose2d.
     * @param obj object to get struct for
     * @return An object's protobuf implementation or empty if there is none
     */
    @SuppressWarnings("unchecked")
    private static <T, U extends ProtoMessage<?>> Optional<Protobuf<T, U>> getProtobufImpl(T obj) {
        Protobuf<?, ?> protobuf = null;
        if (obj instanceof Trajectory) protobuf = Trajectory.proto;
        return Optional.ofNullable((Protobuf<T, U>) protobuf);
    }

}
