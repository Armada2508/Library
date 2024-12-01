package frc.robot.lib.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.BooleanTopic;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.DoubleTopic;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;

public class LogUtil {

    /**
     * Prevent this class from being instantiated.
     */
    private LogUtil() {}

    // RFC2822
    private static SimpleDateFormat kDateFormat = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
    private static SimpleDateFormat kSessionNameFormat = new SimpleDateFormat("dd-MMM-yyyy-HH-mm-ss", Locale.US);

    public static String getTimestamp() {
        return kDateFormat.format(new Date());
    }

    public static String getSessionName() {
        return kSessionNameFormat.format(new Date());
    }

    public static String getSessionNameEvent() {
        return DriverStation.getMatchType().toString() + DriverStation.getMatchNumber() + "-" + DriverStation.getAlliance().toString() + DriverStation.getLocation() + (DriverStation.getEventName() == null ? "" : "-") + Optional.ofNullable(DriverStation.getEventName()).orElse("");
    }

    public static String getSessionNameAuto() {
        if (DriverStation.isFMSAttached()) {
            return getSessionNameEvent();
        } else {
            return getSessionName();
        }
    }

    public static String boolToString(boolean bool) {
        return bool ? "1" : "0";
    }

    public static double boolToDouble(boolean bool) {
        return bool ? 1.0 : 0.0;
    }

    /**
     * Used for getting tunable doubles for quick iteration. Don't call this method in a loop as it will create NT subscribers and publishers.
     * @param name of value in network tables
     * @param defaultValue
     * @return A subscriber that can be used to get the value from network tables
     */
    public static DoubleSubscriber getTunableDouble(String name, double defaultValue) {
        DoubleTopic topic = NetworkTableInstance.getDefault().getTable("Tuning").getDoubleTopic(name);
        topic.publish().set(defaultValue);
        return topic.subscribe(defaultValue);
    }

    /**
     * Used for getting tunable booleans for quick iteration. Don't call this method in a loop as it will create NT subscribers and publishers.
     * @param name of value in network tables
     * @param defaultValue
     * @return A subscriber that can be used to get the value from network tables
     */
    public static BooleanSubscriber getTunableBoolean(String name, boolean defaultValue) {
        BooleanTopic topic = NetworkTableInstance.getDefault().getTable("Tuning").getBooleanTopic(name);
        topic.publish().set(defaultValue);
        return topic.subscribe(defaultValue);
    }

    public static void logDriverStation() {
        // String mode = "Unknown";
        // if (DriverStation.isTeleop()) {
        //     mode = "Teleop";
        // }
        // else if (DriverStation.isAutonomous()) {
        //     mode = "Autonomous";
        // }
        // else if (DriverStation.isTest()) {
        //     mode = "Test";
        // }
        // mainTable.getEntry("_DS Mode").setString(mode);
        // mainTable.getEntry("_Robot Enabled").setBoolean(DriverStation.isEnabled());
        // mainTable.getEntry("_Match Time").setDouble(DriverStation.getMatchTime());
        // mainTable.getEntry("_is FMS Attached").setBoolean(DriverStation.isFMSAttached());
    }

    public static void logCommandInterrupts() {
        // CommandScheduler.getInstance().onCommandInterrupt((interruptedCommand, interrupter) -> {
        //     Command interruptingCommand = interrupter.orElseGet(Commands::none);
        //     DataLogManager.log("Command: " + interruptedCommand.getName() + " was interrupted by " + interruptingCommand.getName() + ".");
            // schedulerTable.getEntry("Last Interrupted Command").setString(interruptedCommand.getName());
            // schedulerTable.getEntry("Last Interrupting Command").setString(interruptingCommand.getName());
        // });
    }

}
