package frc.robot.lib;

public class Encoder {

    /**
     * Converts from revolutions to real-world units
     * @param revolutions The current value read from the sensor
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @return Distance traveled
     */
    public static double toDistance(double revolutions, double gearRatio, double wheelDiameter) {
        return (revolutions / gearRatio) * Math.PI * wheelDiameter;
    } //?CHECK
    
    /**
     * Converts from real-world units to revolutions
     * @param distance The distance traveled
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @return Distance in revolutions
     */
    public static double fromDistance(double distance, double gearRatio, double wheelDiameter) {
        return (distance / (Math.PI * wheelDiameter)) * gearRatio;
    } //?CHECK

    /**
     * Converts revolutions per time unit specified to unit per time unit specified
     * @param rotationalVelocity The current velocity measured by the sensor
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @param time The time period over which the rotational velocity was measured(e.g. 1s for TalonFX)
     * @return Velocity in unit specified
     */
    public static double toVelocity(double rotationalVelocity, double gearRatio, double wheelDiameter, double time) {
        return toDistance(rotationalVelocity, gearRatio, wheelDiameter) / time;
    } //?CHECK

    /**
     * Converts from units per time unit specified to revolutions per time unit specified 
     * @param velocity The current velocity measured by the sensor
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @param wheelDiameter The diameter of the wheel, input units will dictate output units
     * @param time The time period over which the rotational velocity was measured(e.g. 1s for TalonFX)
     * @return rotational velocity
     */
    public static double fromVelocity(double velocity, double gearRatio, double wheelDiameter, double time) {
        return fromDistance(velocity, gearRatio, wheelDiameter) * time;
    } //?CHECK

    /**
     * Converts rotational velocity to RPM
     * @param velocity The rotational velocity
     * @param gearRatio The gear ratio between the output and the motor
     * @param time The time over which the encoder velocity was measured(e.g. 1s for TalonFX)
     * @return RPM of the output
     */
    public static double toRPM(double velocity, double gearRatio, double time) {
        return (velocity * 60.0) / (gearRatio * time);
    } //?CHECK

    /**
     * Converts RPM to rotational velocity
     * @param RPM The input RPM
     * @param gearRatio The gear ratio between the output and the motor
     * @param time The time over which the encoder velocity is measured(e.g. 1s for TalonFX)
     * @return rotational velocity
     */
    public static double fromRPM(double RPM, double gearRatio, double time) {
        return RPM / 60.0 * time * gearRatio;
    } //?CHECK

    /**
     * Converts revolutions to a rotational angle in degrees 
     * @param revolutions The current value read from the sensor
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @return Angle in degrees
     */
    public static double toRotationalAngle(double revolutions, double gearRatio) {
        return revolutions * 360.0 / gearRatio;
    } //?CHECK

    /**
     * Converts a rotational angle in degrees to revolutions
     * @param sensorPosition The current value read from the sensor
     * @param gearRatio The ratio of gearing from the output shaft of the gearbox to the wheel
     * @return Angle in encoder units
     */ 
    public static double fromRotationalAngle(double angle, double gearRatio) {
        return angle / 360.0 * gearRatio;
    } //?CHECK

}