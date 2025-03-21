package frc.robot.lib.util;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Rotations;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;

public class Encoder {

    public static final double degreesPerRotation = 360.0;

    /**
     * Prevent this class from being instantiated.
     */
    private Encoder() {}

    /**
     * Converts from encoder units to real-world units
     * @param sensorPosition The current value read from the sensor
     * @param encoderUnitsPerRev The number of encoder units sensed per revolution of the output shaft of the gearbox
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @return Distance traveled
     */
    public static double toDistance(double sensorPosition, int encoderUnitsPerRev, double gearRatio, double wheelDiameter) {
        return (sensorPosition / (encoderUnitsPerRev * gearRatio)) * Math.PI * wheelDiameter;
    }

    /**
     * Converts from rotations to real-world units for TalonFX
     * @param rotations The current value read from the sensor
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @return Distance traveled
     */
    public static double toDistance(double rotations, double gearRatio, double wheelDiameter) {
        return toDistance(rotations, 1, gearRatio, wheelDiameter);
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
        return (distance / ( Math.PI * wheelDiameter )) * encoderUnitsPerRev * gearRatio;
    }

    /**
     * Converts from real-world units to rotations for TalonFX
     * @param distance The distance traveled
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @return Distance in rotations
     */
    public static double fromDistance(double distance, double gearRatio, double wheelDiameter) {
        return fromDistance(distance, 1, gearRatio, wheelDiameter);
    }

    /**
     * Converts rotations per second to velocity for TalonFX
     * @param velocity The current velocity measured by the sensor
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
     * Converts from units per second to rotations per second for TalonFX
     * @param velocity The current velocity measured by the sensor
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
        return (velocity * 60.0 / encoderUnitsPerRev) / (gearRatio * time);
    }

    /**
     * Convenience fromRPM for TalonFX
     * @param velocity The input RPM
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
        return RPM * (encoderUnitsPerRev / 60.0) * time * gearRatio;
    }

    /**
     * Converts rotations of a motor shaft to distance traveled of a wheel.
     * @param rotations The number of rotations
     * @param gearRatio The ratio between rotations of the output shaft and rotations of the wheel, e.g. 10.71:1
     * @param wheelDiameter The diameter of the wheel
     * @return Distance traveled by the wheel
     */
    public static Distance angularToLinear(Angle rotations, double gearRatio, Distance wheelDiameter) {
        return Meters.of((rotations.in(Rotations) / gearRatio) * Math.PI * wheelDiameter.in(Meters));
    }

    /**
     * Converts an angular measurement to a linear measurement
     * @param rotations The number of rotations
     * @param wheelDiameter The Diameter of the wheel
     * @return Distance traveled by the wheel
     */
    public static Distance angularToLinear(Angle rotations, Distance wheelDiameter) {
        return angularToLinear(rotations, 1, wheelDiameter);
    }

    /**
     * Converts distance traveled by a wheel to rotations of a motor shaft.
     * @param distance The distance traveled
     * @param gearRatio The ratio between rotations of the output shaft and rotations of the wheel, e.g. 10.71:1
     * @param wheelDiameter The diameter of the wheel
     * @return Rotations of the motor shaft
     */
    public static Angle linearToAngular(Distance distance, double gearRatio, Distance wheelDiameter) {
        return Rotations.of(distance.in(Meters) / (Math.PI * wheelDiameter.in(Meters)) * gearRatio);
    }

    /**
     * Converts a linear measurement to an angular measurement
     * @param distance The distance traveled
     * @param wheelDiameter The diameter of the wheel
     * @return Rotations of the motor shaft
     */
    public static Angle linearToAngular(Distance distance, Distance wheelDiameter) {
        return linearToAngular(distance, 1, wheelDiameter);
    }

}
