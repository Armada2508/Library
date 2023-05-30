package frc.robot.lib.config;

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class MotionMagicConfig {

    private static final int kTalonFrame1Period = 20;  // How often the Talon reports basic info(Limits, limit overrides, faults, control mode, invert)
    private static final int kTalonFrame2Period = 20;  // How often the Talon reports sensor info(Sensor position/velocity, current, sticky faults, profile)
    private static final int kTalonFrame3Period = 160;  // How often the Talon reports non selected quad info(Position/velocity, edges, quad a and b pin, index pin)
    private static final int kTalonFrame4Period = 160;  // How often the Talon reports additional info(Analog position/velocity, temperature, battery voltage, selected feedback sensor)
    private static final int kTalonFrame8Period = 160;  // How often the Talon reports more encoder info(Talon Idx pin, PulseWidthEncoded sensor velocity/position)
    private static final int kTalonFrame10Period = 160;  // How often the Talon reports info on motion magic(Target position, velocity, active trajectory point)
    private static final int kTalonFrame13Period = 160; // How often the Talon reports info on PID(Error, Integral, Derivative)

    private final int mCruiseVel;
    private final int mAccel;
    private final int mSmoothing;

    /**
     * Creates a new MotionMagicConfig object
     * @param cruiseVelocity The desired cruise velocity
     * @param acceleration The desired maximum acceleration
     * @param smoothing The desired smoothing or S-Curve strength
     */
    public MotionMagicConfig(int cruiseVelocity, int acceleration, int smoothing) {
        mCruiseVel = cruiseVelocity;
        mAccel = acceleration;
        mSmoothing = smoothing;
    }

    /**
     * @return The desired cruise velocity
     */
    public int getCruiseVelocity() {
        return mCruiseVel;
    }

    /**
     * @return The desired maximum acceleration
     */
    public int getAcceleration() {
        return mAccel;
    }

    /**
     * @return The desired smoothing or S-Curve strength
     */
    public int getSmoothing() {
        return mSmoothing;
    }

    /**
     * Configure a Talon SRX with the specified MotionMagicConfig
     * @param talon The talon to configure
     * @param config The MotionMagicConfig to apply
     */
    public static void config(TalonSRX talon, MotionMagicConfig config) {
        talon.configMotionCruiseVelocity(config.getCruiseVelocity());
        talon.configMotionAcceleration(config.getAcceleration());
        talon.configMotionSCurveStrength(config.getSmoothing());
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, kTalonFrame1Period);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, kTalonFrame2Period);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, kTalonFrame3Period);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, kTalonFrame4Period);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, kTalonFrame8Period);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_10_Targets, kTalonFrame10Period);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, kTalonFrame13Period);
    }

    /**
     * Configure a Talon FX with the specified MotionMagicConfig
     * @param talon The talon to configure
     * @param config The MotionMagicConfig to apply
     */
    public static void config(TalonFX talon, MotionMagicConfig config) {
        talon.configMotionCruiseVelocity(config.getCruiseVelocity());
        talon.configMotionAcceleration(config.getAcceleration());
        talon.configMotionSCurveStrength(config.getSmoothing());
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, kTalonFrame1Period);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, kTalonFrame2Period);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, kTalonFrame3Period);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, kTalonFrame4Period);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, kTalonFrame8Period);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_10_Targets, kTalonFrame10Period);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, kTalonFrame13Period);
    }
}