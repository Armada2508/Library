package frc.robot;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.constraint.DifferentialDriveKinematicsConstraint;
import edu.wpi.first.math.util.Units;
import frc.robot.lib.util.Util;

public class Constants {

    public static final int pigeonID = 8;
    public static final int timeoutMs = 30;
    
    public static final class Drive {
        public static final int RID = 0;
        public static final int RFID = 1;
        public static final int LID = 2;
        public static final int LFID = 3;
        public static final double speedAdjustment = 0.75; // For Fredy
        public static final double turnAdjustment = 0.5;
        public static final double trimAdjustment = .23;
        public static final double joystickDeadband = 0.07;
        public static final double slewRate = 1.5; // This is what helps you not stop abruptly, higher value = stop faster
        public static final double slowSpeed = 0.2;

        // Closed Loop Driving
        public static final int motionMagicSlot = 0; 
        public static final double motionMagickP = 0.3; 
        public static final double motionMagickI = 0;
        public static final double motionMagickD = 0;
        public static final double motionMagickF = 0; // Probably keep this at 0

        public static final int velocitySlot = 1; 
        public static final double velocitykP = 0.35; 
        public static final double velocitykI = 0; 
        public static final double velocitykD = 5;
        public static final double velocitykF = 0.047;

        public static final double maxOutput = 1;
        public static final double nominalOutputLeft = 0.03;
        public static final double nominalOutputRight = 0.03;

        // Closed Loop Turning (WPILib not on Falcons)
        public static final double turnkP = 0.015;
        public static final double turnkI = 0;
        public static final double turnkD = 0.001;
        public static final double maxTurnSpeed = 0.2;
        
        // DriveBase
        public static final double wheelDiameterMeters = Units.inchesToMeters(6);
        public static final int encoderUnitsPerRev = 2048;
        public static final double gearboxRatio = 10.71;
        public static final double trackWidthMeters = Util.inchesToMeters(24.5);

        // Trajectories
        public static final double ramseteB = 2.0;
        public static final double ramseteZeta = 0.7;
        public static final TrajectoryConfig trajectoryConfig = new TrajectoryConfig(0.75, 0.5);
        static {
            trajectoryConfig.addConstraint(new DifferentialDriveKinematicsConstraint(
            new DifferentialDriveKinematics(Drive.trackWidthMeters), 0.5) // 1.5
            );
        }
    }

    public static final class Vision {
        // Robot Frame
        public static final Translation3d cameraTranslationOffset = new Translation3d(0, -1.282, Units.inchesToMeters(13.5));
        public static final Rotation3d cameraRotationOffset = new Rotation3d(Units.degreesToRadians(-53.17), 0, 0);
        public static final Pose3d cameraPoseRobotFrame = new Pose3d(cameraTranslationOffset, cameraRotationOffset);
        public static final double centerToFront = Units.inchesToMeters(14.5);
        // Target Heights
        public static final double coneHeightMeters = 0;
        public static final double cubeHeightMeters = 0;
        public static final double aprilTagHeightMeters = 0;
        // Camera
        public static final double cameraHeightMeters = Units.inchesToMeters(50.3);
        public static final double cameraPitchRadians = Units.degreesToRadians(0.1); 
    }

    // ========================================
    //    Global Motor Controller Constants
    // ========================================A
    public static final class MotorController {

        // Status frames are sent over CAN that contain data about the Talon.
        // They are broken up into different pieces of data and the frequency
        // at which they are sent can be changed according to your needs.
        // The period at which their are sent is measured in ms

        public static final int kTalonFrame1Period = 20;  // How often the Talon reports basic info(Limits, limit overrides, faults, control mode, invert)
        public static final int kTalonFrame2Period = 20;  // How often the Talon reports sensor info(Sensor position/velocity, current, sticky faults, profile)
        public static final int kTalonFrame3Period = 160;  // How often the Talon reports non selected quad info(Position/velocity, edges, quad a and b pin, index pin)
        public static final int kTalonFrame4Period = 160;  // How often the Talon reports additional info(Analog position/velocity, temperature, battery voltage, selected feedback sensor)
        public static final int kTalonFrame8Period = 160;  // How often the Talon reports more encoder info(Talon Idx pin, PulseWidthEncoded sensor velocity/position)
        public static final int kTalonFrame10Period = 160;  // How often the Talon reports info on motion magic(Target position, velocity, active trajectory point)
        public static final int kTalonFrame13Period = 160; // How often the Talon reports info on PID(Error, Integral, Derivative)

    }

}
