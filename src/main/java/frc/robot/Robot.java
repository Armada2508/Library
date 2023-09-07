// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.lib.logging.NTLogger;

public class Robot extends TimedRobot {

	private RobotContainer container;

	@Override
	public void robotInit() {
		DataLogManager.start("C:\\Users\\armad\\Robotics\\LibDevTesting\\logs");
		DriverStation.startDataLog(DataLogManager.getLog(), false);
		DriverStation.silenceJoystickConnectionWarning(true);
		container = new RobotContainer();
	}

	@Override
	public void robotPeriodic() {
		CommandScheduler.getInstance().run();
		NTLogger.log();
	}

	@Override
	public void autonomousInit() {
		container.getAutoCommand().schedule();
	}

	@Override
	public void autonomousPeriodic() {}

	@Override
	public void teleopInit() {
		container.stopEverything();
	}

	@Override
	public void teleopPeriodic() {}

	@Override
	public void disabledInit() {
		container.stopEverything();
	}

	@Override
	public void disabledPeriodic() {}

	@Override
	public void testInit() {}

	@Override
	public void testPeriodic() {}

	@Override
	public void simulationInit() {}

	@Override
	public void simulationPeriodic() {}
	
}
