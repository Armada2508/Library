package frc.robot.lib.motionmagic;

public interface MotionMagicSubsystem {

    void configMotionMagic(double velocity, double acceleration);
    void stop();
    void setPosition(double position);
    double getSensorPosition();
    double getSensorTarget(); 
    /**
     * @param position SPECIFY UNITS
     * @param velocity SPECIFY UNITS
     * @param acceleration SPECIFY UNITS
     * @return A command that will tell the subsystem to go to a position with motion magic using the specified velocity and acceleration
     */
    MotionMagicCommand doMotionMagic(double position, double velocity, double acceleration);
    
}
