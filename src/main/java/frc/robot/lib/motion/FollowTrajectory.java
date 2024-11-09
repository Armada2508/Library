package frc.robot.lib.motion;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import edu.wpi.first.math.controller.LTVUnicycleController;
import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform2d;
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

    private static RamseteController ramseteController;
    private static DifferentialDriveKinematics diffKinematics;

    /**
     * Configure FollowTrajectory for using its static methods, must be called.
     * @param b The B constant(RAMSETE)
     * @param zeta The Zeta constant(RAMSETE)
     * @param trackWidth The width of the drivetrain(Kinematics)
     */
    public static void config(double b, double zeta, Measure<Distance> trackWidth) {
        ramseteController = new RamseteController(b, zeta);
        diffKinematics = new DifferentialDriveKinematics(trackWidth);
    }

    /**
     * Returns a RamseteCommand that follows the specified trajectory and uses the PID loop on the Talons
     * @param trajectory The Trajectory to follow
     * @param zeroPose The position to start relative to
     * @param pose The supplier of the robot pose
     * @param velocity The consumer of ramsete's left and right wheel velocities in meters/second
     * @param driveSubsystem The subsystem to require during the command
     * @return Returns a RamseteCommand that will follow the specified trajectory with the specified driveSubsystem
     */
    public static Command ramseteControllerCommand(Trajectory trajectory, Pose2d zeroPose, Supplier<Pose2d> pose, BiConsumer<Double, Double> velocity, Subsystem driveSubsystem) {
        trajectory = trajectory.transformBy(new Transform2d(new Pose2d(), zeroPose));
        return new RamseteCommand(
            trajectory,
            pose,
            ramseteController,
            diffKinematics,
            velocity,
            driveSubsystem
        );
    }

    /**
     * Creates a command to follow a given trajectory using a LTVUnicycleController
     * @param trajectory The Trajectory to follow
     * @param pose The supplier of the robot pose
     * @param velocity The consumer of ramsete's left and right wheel velocities in meters/second
     * @param driveSubsystem The subsystem to require during the command
     * @return A command to follow a given trajectory using a LTVUnicycleController
     */
    public static Command LTVControllerCommand(Trajectory trajectory, Supplier<Pose2d> pose, BiConsumer<Double, Double> velocity, Subsystem driveSubsystem) {
        LTVUnicycleController controller = new LTVUnicycleController(TimedRobot.kDefaultPeriod);
        Timer timer = new Timer();
        return driveSubsystem.runOnce(timer::restart)
        .andThen(driveSubsystem.run(() -> {
            DifferentialDriveWheelSpeeds speeds = diffKinematics.toWheelSpeeds(controller.calculate(pose.get(), trajectory.sample(timer.get())));
            velocity.accept(speeds.leftMetersPerSecond, speeds.rightMetersPerSecond);
        }).until(() -> timer.hasElapsed(trajectory.getTotalTimeSeconds())));
    }

}
