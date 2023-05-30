package frc.robot.lib.motionmagic;

import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;

import edu.wpi.first.wpilibj2.command.Command;

public interface MotionMagicSubsystem {

    public void configMotors(TalonFXConfiguration config);
    public void configMotionMagic(double velocity, double acceleration);
    public void stop();
    public void setPosition(double position);
    public double getSensorPosition();
    public double getSensorTarget(); 
    /**
     * @param position SPECIFY UNITS
     * @param velocity SPECIFY UNITS
     * @param acceleration SPECIFY UNITS
     * @return A command that will tell the subsystem to go to a position with motion magic using the specified velocity and acceleration
     */
    public Command doMotionMagic(double position, double velocity, double acceleration);
    
}
