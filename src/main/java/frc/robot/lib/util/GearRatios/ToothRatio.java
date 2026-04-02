package frc.robot.lib.util.GearRatios;

public class ToothRatio {
    public static double inputToOutputRatio(int inputToothCount, int outputToothCount) {
        if (inputToothCount == 0 || outputToothCount == 0) throw new IllegalArgumentException("Cannot have tooth count = 0");
        return (double) inputToothCount / outputToothCount; // Cast to double to prevent integer division bugs
    }

    public static double outputToInputRatio(int inputToothCount, int outputToothCount) {
        if (inputToothCount == 0 || outputToothCount == 0) throw new IllegalArgumentException("Cannot have tooth count = 0");
        return (double) outputToothCount / inputToothCount; // Cast to double to prevent integer divison bugs
    }
}
