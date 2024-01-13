package frc.robot.lib.logging;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LogUtil {

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
		if(DriverStation.isFMSAttached()) {
			return getSessionNameEvent();
		} else {
			return getSessionName();
		}
	}

	public static String boolToString(boolean bool) {
		return bool ? "1" : "0";
	}

	public static double boolToDouble(boolean bool) {
		return bool ? 1d : 0d;
	}

    /**
     * Formats a list of names with their associated variables.
     * @param names space separated names for each variable, number should match number of variables. ex: yaw pitch roll 
     * @param variables to print out
     */
    public static String getFormatted(String names, Object... variables) {
        String formatted = "";
        String[] namesArr = names.split(" +");
        if (namesArr.length != variables.length) throw new IllegalArgumentException("Number of names doesn't match number of variables.");
        for (int i = 0; i < namesArr.length; i++) {
            String currentName = namesArr[i];
            Object currentVar = variables[i];
            if (currentName.length() > 1) {
                String n = (currentName.charAt(0) + "").toUpperCase() + currentName.substring(1);
                formatted += n + ": " + currentVar + ", ";
            } 
            else {
                formatted += currentName.toUpperCase() + ": " + currentVar + ", ";
            }
        }
        return formatted;
    }

    /**
     * Pretty prints a list of names with their associated variables.
     * @param names space separated names for each variable, number should match number of variables. ex: yaw pitch roll 
     * @param variables to print out
     */
    public static void printFormatted(String names, Object... variables) {
        System.out.println(getFormatted(names, variables));
    }

	public static void logSubsystems(SubsystemBase[] subsystems) {
        try {
            final Field fieldIndex = SequentialCommandGroup.class.getDeclaredField("m_currentCommandIndex");
            fieldIndex.setAccessible(true);
            final Field fieldCommands = SequentialCommandGroup.class.getDeclaredField("m_commands");
            fieldCommands.setAccessible(true);
            SubsystemBase loggerSubsystem = new SubsystemBase() {};
            loggerSubsystem.setDefaultCommand(Commands.run(() -> {
                System.out.println("\nDEBUG: Subsystem Logger");
                for (int i = 0; i < subsystems.length; i++) {
                    String name = "None";
                    Command command = subsystems[i].getCurrentCommand();
                    if (command != null) {
                        name = command.getName();
                        if (command instanceof SequentialCommandGroup) {
                            try {
                                @SuppressWarnings("unchecked")
                                List<Command> list = (List<Command>) fieldCommands.get(command);
                                name += " - " + list.get(fieldIndex.getInt(command)).getName();
                            } catch (IllegalArgumentException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    System.out.println(subsystems[i].getName() + ": " + name);
                }
            }, loggerSubsystem));
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }

}