package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;

import edu.wpi.first.math.util.Units;
import frc.robot.lib.util.Util;

public class Constants {

    public static final int pigeonID = 8;
    public static final int timeoutMs = 30;
    
    public static final class Drive {
        
        // Motors
        public static final int RID = 0;
        public static final int RFID = 1;
        public static final int LID = 2;
        public static final int LFID = 3;
        
        // Driving
        public static final double speedAdjustment = 0.75; 
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
        public static final double motionMagickF = 0; 

        public static final int velocitySlot = 1; 
        public static final double velocitykP = 0.35; 
        public static final double velocitykI = 0; 
        public static final double velocitykD = 5;
        public static final double velocitykF = 0.047;

        public static final double maxOutput = 1;
        public static final double nominalOutputLeft = 0.03;
        public static final double nominalOutputRight = 0.03;

        // DriveBase
        public static final double wheelDiameterMeters = Units.inchesToMeters(6);
        public static final int encoderUnitsPerRev = 2048;
        public static final double gearboxRatio = 10.71;
        public static final double trackWidthMeters = Util.inchesToMeters(24.5);

    }

    public static final class Example {
        public static final TalonFXConfiguration talonConfig = new TalonFXConfiguration();
        static {
            talonConfig.slot0.kP = 0;
        }
    }

}
