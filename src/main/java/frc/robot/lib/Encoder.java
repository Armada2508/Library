package frc.robot.lib;

public class Encoder {

    /**
     * Converts from encoder units to real-world units
     * @param sensorPosition The current value read from the sensor
     * @param encoderUnitsPerRev The number of encoder units sensed per revolution of the output shaft of the gearbox
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @return Distance traveled
     */
    public static double toDistance(double sensorPosition, int encoderUnitsPerRev, double gearRatio, double wheelDiameter) {
        return ((double) sensorPosition / (double) (encoderUnitsPerRev * gearRatio)) * Math.PI * wheelDiameter;
    }

    /**
     * Converts from encoder units to real-world units for TalonFX
     * @param sensorPosition The current value read from the sensor
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @return Distance traveled
     */
    public static double toDistance(double sensorPosition, double gearRatio, double wheelDiameter) {
        return toDistance(sensorPosition, 1, gearRatio, wheelDiameter);
    }

    /**
     * Converts from real-world units to encoder units
     * @param distance The distance traveled
     * @param encoderUnitsPerRev The number of encoder units sensed per revolution of the output shaft of the gearbox
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @return Distance in encoder units
     */
    public static double fromDistance(double distance, int encoderUnitsPerRev, double gearRatio, double wheelDiameter) {
        return (distance / ( Math.PI * wheelDiameter )) * (double) encoderUnitsPerRev * gearRatio;
    }

    /**
     * Converts from real-world units to encoder units for TalonFX
     * @param distance The distance traveled
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @return Distance in encoder units
     */
    public static double fromDistance(double distance, double gearRatio, double wheelDiameter) {
        return fromDistance(distance, 1, gearRatio, wheelDiameter);
    }

    /**
     * Convenience toVelocity for TalonFX
     * @param velocity The current velocity measured by the sensor
     * @param encoderUnitsPerRev The number of encoder units sensed per revolution of the output shaft of the gearbox
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @return
     */

    public static double toVelocity(double velocity, double gearRatio, double wheelDiameter) {
        return toVelocity(velocity, 1, gearRatio, wheelDiameter, 1);
    }
    
    /**
     * Converts encoder units per time unit specified to velocity
     * @param velocity The current velocity measured by the sensor
     * @param encoderUnitsPerRev The number of encoder units sensed per revolution of the output shaft of the gearbox
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @param time The time period over which the encoder velocity was measured(e.g. 1s for Talons)
     * @return
     */
    public static double toVelocity(double velocity, int encoderUnitsPerRev, double gearRatio, double wheelDiameter, double time) {
        return toDistance(velocity, encoderUnitsPerRev, gearRatio, wheelDiameter) / time;
    }

    /**
     * Convenience fromVelocity for TalonFX
     * @param velocity The current velocity measured by the sensor
     * @param encoderUnitsPerRev The number of encoder units sensed per revolution of the output shaft of the gearbox
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @return
     */

    public static double fromVelocity(double velocity, double gearRatio, double wheelDiameter) {
        return fromVelocity(velocity, 1, gearRatio, wheelDiameter, 1);
    }
    
    /**
     * Converts from units per second specified to encoder units per time unit specified 
     * @param velocity The current velocity measured by the sensor
     * @param encoderUnitsPerRev The number of encoder units sensed per revolution of the output shaft of the gearbox
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @param time The time period over which the encoder velocity was measured(e.g. 1s for Talons)
     * @return
     */
    public static double fromVelocity(double velocity, int encoderUnitsPerRev, double gearRatio, double wheelDiameter, double time) {
        return fromDistance(velocity, encoderUnitsPerRev, gearRatio, wheelDiameter) * time;
    }

    /**
     * Convenience toRPM for TalonFX
     * @param velocity The encoder velocity
     * @param encoderUnitsPerRev The number of encoder units per revolution
     * @param gearRatio The gear ratio between the output and the motor
     * @return RPM of the output
     */
    public static double toRPM(double velocity, double gearRatio) {
        return toRPM(velocity, 1, gearRatio, 1);
    }


    /**
     * Converts encoder velocity to RPM
     * @param velocity The encoder velocity
     * @param encoderUnitsPerRev The number of encoder units per revolution
     * @param gearRatio The gear ratio between the output and the motor
     * @param time The time over which the encoder velocity was measured(e.g. 1s for Talons)
     * @return RPM of the output
     */
    public static double toRPM(double velocity, int encoderUnitsPerRev, double gearRatio, double time) {
        return (velocity * 60.0 / (double) encoderUnitsPerRev) / (gearRatio * time);
    }

    /**
     * Convenience fromRPM for TalonFX
     * @param velocity The input RPM
     * @param encoderUnitsPerRev The number of encoder units per revolution
     * @param gearRatio The gear ratio between the output and the motor
     * @return Encoder velocity
     */
    public static double fromRPM(double RPM, double gearRatio) {
        return fromRPM(RPM, 1, gearRatio, 1);
    }

    /**
     * Converts RPM to encoder velocity
     * @param velocity The input RPM
     * @param encoderUnitsPerRev The number of encoder units per revolution
     * @param gearRatio The gear ratio between the output and the motor
     * @param time The time over which the encoder velocity is measured(e.g. 1s for Talons)
     * @return Encoder velocity
     */
    public static double fromRPM(double RPM, int encoderUnitsPerRev, double gearRatio, double time) {
        return RPM * ((double) encoderUnitsPerRev / 60.0) * time * gearRatio;
    }

    /**
     * Converts sensor units to a rotational angle in degrees 
     * @param sensorPosition The current value read from the sensor
     * @param encoderUnitsPerRev The number of encoder units sensed per revolution of the output shaft of the gearbox
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @return Angle in degrees
     */
    public static double toRotationalAngle(double sensorPosition, double encoderUnitsPerRev, double gearRatio) {
        return sensorPosition * (360.0 / encoderUnitsPerRev) / gearRatio;
    }

    /**
     * Converts sensor units to a rotational angle in degrees for the TalonFX
     * @param sensorPosition The current value read from the sensor
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @return Angle in degrees
     */
    public static double toRotationalAngle(double sensorPosition, double gearRatio) {
        return toRotationalAngle(sensorPosition, 1, gearRatio);
    }

    /**
     * Converts a rotational angle in degrees to sensor units
     * @param sensorPosition The current value read from the sensor
     * @param encoderUnitsPerRev The number of encoder units sensed per revolution of the output shaft of the gearbox
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @return Angle in encoder units
     */
    public static double fromRotationalAngle(double angleDegrees, double encoderUnitsPerRev, double gearRatio) {
        return angleDegrees * (encoderUnitsPerRev / 360.0) * gearRatio;
    }

    /**
     * Converts a rotational angle in degrees to sensor units for the TalonFX
     * @param sensorPosition The current value read from the sensor
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @return Angle in encoder units
     */
    public static double fromRotationalAngle(double angleDegrees, double gearRatio) {
        return fromRotationalAngle(angleDegrees, 1, gearRatio);
    }

}