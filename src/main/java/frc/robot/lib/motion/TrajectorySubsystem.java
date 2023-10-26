package frc.robot.lib.motion;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj2.command.Subsystem;

public interface TrajectorySubsystem extends Subsystem { 
    
    Pose2d getPose();
    DifferentialDriveWheelSpeeds getWheelSpeeds();
    void setVelocity(double velocityL, double velocityR);
    void setVoltage(double voltsL, double voltsR);
}


