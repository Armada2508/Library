package frc.robot.lib.util.GearRatios;

public class GearBox {
    /**
     * 
     * @param isOverDrive
     * @param ratio
     * @return
     */
    public static double inputToOutputRatio(boolean isOverDrive, double ratio) {
        if (ratio == 0) throw new IllegalArgumentException("Cannot have a stage = 0");
        return isOverDrive ? ratio : 1.0 / ratio;
    }

    /**
     * 
     * @param isOverDrive
     * @param ratio
     * @return
     */
    public static double outputToInputRatio(boolean isOverDrive, double ratio) {
        if (ratio == 0) throw new IllegalArgumentException("Cannot have a stage = 0");
        return isOverDrive ? 1.0 / ratio : ratio;
    }
}

