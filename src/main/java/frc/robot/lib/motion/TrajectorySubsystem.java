package frc.robot.lib.motion;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj2.command.Subsystem;

public interface TrajectorySubsystem { 
    
    Pose2d getPose();
    DifferentialDriveWheelSpeeds getWheelSpeeds();
    void setVelocity(double velocityL, double velocityR);
    void setVoltage(double voltsL, double voltsR);
    // Extremely janky, so I don't have to write this method in my subsystems that use trajectories.
    default Subsystem getRequirements() {
        Object o = this;
        return (Subsystem) o;
    }
}


