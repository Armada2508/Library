package frc.robot.lib.logging;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WrapperCommand;

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

    public static GenericEntry getTuner(String name, Object defaultValue) {
        GenericEntry entry = NetworkTableInstance.getDefault().getTable("Tuning")
            .getTopic(name).getGenericEntry(new PubSubOption[0]);
        entry.setValue(defaultValue);
        return entry;
    }

    static Command getSequentialCommandCurrentCommand(SequentialCommandGroup command) {
        try {
            final Field fieldIndex = SequentialCommandGroup.class.getDeclaredField("m_currentCommandIndex");
            fieldIndex.setAccessible(true);
            final Field fieldCommands = SequentialCommandGroup.class.getDeclaredField("m_commands");
            fieldCommands.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Command> list = (List<Command>) fieldCommands.get(command);
            return list.get(fieldIndex.getInt(command));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            return Commands.none();
        }
    }

    static List<Command> getParallelCommandCurrentCommands(ParallelCommandGroup command) {
        try {
            List<Command> list = new ArrayList<>();
            final Field fieldCommands = ParallelCommandGroup.class.getDeclaredField("m_commands");
            fieldCommands.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Command, Boolean> map = (Map<Command, Boolean>) fieldCommands.get(command);
            map.forEach((cmd, running) -> {
                if (running) list.add(cmd);
            });
            return list;
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            return List.of(Commands.none());
        }
    }

    static Command getWrapperCommandInner(WrapperCommand command) {
        try {
            final Field cmd = WrapperCommand.class.getDeclaredField("m_command");
            cmd.setAccessible(true);
            return (Command) cmd.get(command);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            return Commands.none();
        }
    } 

}