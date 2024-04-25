package frc.robot.lib.motion;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import edu.wpi.first.math.controller.LTVUnicycleController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.units.Distance;
import edu.wpi.first.units.Measure;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;


/**
 * Helper class to generate commands for following trajectories
 */
public class FollowTrajectory {

    /**
     * Prevent this class from being instantiated.
     */
    private FollowTrajectory() {}

    private static SimpleMotorFeedforward feedForward;
    private static DifferentialDriveKinematics diffKinematics;
    private static RamseteController ramseteController;
    private static RamseteController disabledController;
    private static PIDController leftPidController;
    private static PIDController rightPidController;

    /**
     * @param kS The kS constant(Feedforward)
     * @param kV The kV constant(Feedforward)
     * @param kA The kA constant(Feedforward)
     * @param b The B constant(RAMSETE)
     * @param zeta The Zeta constant(RAMSETE)
     * @param trackWidth The width of the drivetrain(Kinematics)
     * @param pidController The PID Controller to use
     */
    public static void config(double kS, double kV, double kA, double b, double zeta, Measure<Distance> trackWidth, PIDController pidController) {
        feedForward = new SimpleMotorFeedforward(kS, kV, kA);
        diffKinematics = new DifferentialDriveKinematics(trackWidth);
        ramseteController = new RamseteController(b, zeta);
        leftPidController = new PIDController(pidController.getP(), pidController.getI(), pidController.getD());
        rightPidController = new PIDController(pidController.getP(), pidController.getI(), pidController.getD());
        disabledController = new RamseteController() {
            @Override
            public ChassisSpeeds calculate(Pose2d currentPose, Pose2d poseRef, double linearVelocityRefMeters,
                    double angularVelocityRefRadiansPerSecond) {
                return new ChassisSpeeds(linearVelocityRefMeters, 0.0, angularVelocityRefRadiansPerSecond);
            }
        };
    }

    /**
     * Shorter config call for when using the Talon commands
     * @param b The B constant(RAMSETE)
     * @param zeta The Zeta constant(RAMSETE)
     * @param trackWidth The width of the drivetrain(Kinematics)
     */
    public static void config(double b, double zeta, Measure<Distance> trackWidth) {
        config(0, 0, 0, b, zeta, trackWidth, new PIDController(0, 0, 0));
    }

    /**
     * Returns a RamseteCommand that follows the specified trajectory using no feedback
     * @param driveSubsystem The DriveSubsystem to use
     * @param trajectory The Trajectory to follow
     * @param zeroPose The position to start relative to
     * @return Returns a RamseteCommand that will follow the specified trajectory with the specified driveSubsystem
     */
    public static Command getCommandFeedforward(Trajectory trajectory, Pose2d zeroPose, Supplier<Pose2d> poseSupplier, Supplier<DifferentialDriveWheelSpeeds> wheelSpeeds, BiConsumer<Double, Double> voltageConsumer, Subsystem driveSubsystem) {
        trajectory = trajectory.relativeTo(zeroPose);
        PIDController leftController = new PIDController(0, 0, 0);
        PIDController rightController = new PIDController(0, 0, 0);
        return new RamseteCommand(
                trajectory,
                poseSupplier,
                disabledController,
                feedForward,
                diffKinematics,
                wheelSpeeds,
                leftController,
                rightController,
                voltageConsumer,
                driveSubsystem);
    }

    /**
     * Returns a RamseteCommand that follows the specified trajectory
     * @param driveSubsystem The DriveSubsystem to use
     * @param trajectory The Trajectory to follow
     * @param zeroPose The position to start relative to
     * @return Returns a RamseteCommand that will follow the specified trajectory with the specified driveSubsystem
     */
    public static Command getCommand(Trajectory trajectory, Pose2d zeroPose, Supplier<Pose2d> poseSupplier, Supplier<DifferentialDriveWheelSpeeds> wheelSpeeds, BiConsumer<Double, Double> voltageConsumer, Subsystem driveSubsystem) {
        trajectory = trajectory.relativeTo(zeroPose);
        return new RamseteCommand(
                trajectory,
                poseSupplier,
                ramseteController,
                feedForward,
                diffKinematics,
                wheelSpeeds,
                leftPidController,
                rightPidController,
                voltageConsumer,
                driveSubsystem);
    }

    /**
     * Returns a RamseteCommand that follows the specified trajectory and uses the PID loop on the Talons
     * @param driveSubsystem The DriveSubsystem to use
     * @param trajectory The Trajectory to follow
     * @param zeroPose The position to start relative to
     * @return Returns a RamseteCommand that will follow the specified trajectory with the specified driveSubsystem
     */
    public static Command getCommandTalon(Trajectory trajectory, Pose2d zeroPose, Supplier<Pose2d> poseSupplier, BiConsumer<Double, Double> velocityConsumer, Subsystem driveSubsystem) {
        trajectory = trajectory.transformBy(new Transform2d(new Pose2d(), zeroPose));
        return new RamseteCommand(
                trajectory,
                poseSupplier,
                ramseteController,
                diffKinematics,
                velocityConsumer,
                driveSubsystem);
    }

    /**
     * Creates a command to follow a given trajectory using a LTVUnicycleController
     * @param trajectory trajectory to follow
     * @param pose drivetrain pose supplier
     * @param velocity consumer of left and right wheel velocities in meters per second
     * @param driveSubsystem 
     * @return a command to follow a given trajectory using a LTVUnicycleController
     */
    public static Command LTVControllerCommand(Trajectory trajectory, Supplier<Pose2d> pose, BiConsumer<Double, Double> velocity, Subsystem driveSubsystem) {
        LTVUnicycleController controller = new LTVUnicycleController(TimedRobot.kDefaultPeriod);
        Timer timer = new Timer();
        return driveSubsystem.runOnce(() -> {
            timer.restart();
        }) 
        .andThen(driveSubsystem.run(() -> {
            DifferentialDriveWheelSpeeds speeds = diffKinematics.toWheelSpeeds(controller.calculate(pose.get(), trajectory.sample(timer.get())));
            velocity.accept(speeds.leftMetersPerSecond, speeds.rightMetersPerSecond);
        }).until(() -> timer.hasElapsed(trajectory.getTotalTimeSeconds())));
    }
    
} 