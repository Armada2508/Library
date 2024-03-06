package frc.robot.lib.controller;

import java.util.HashSet;
import java.util.Set;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class SmartJoystick extends Joystick {

    private Set<Integer> boundTrueButtons = new HashSet<>();
    private Set<Integer> boundFalseButtons = new HashSet<>();
    
    public SmartJoystick(final int port) {
        super(port);
    }

    private void addOrThrow(int button, Set<Integer> set) throws IllegalArgumentException {
        if (set.contains(button)) throw new IllegalArgumentException();
        set.add(button);
    }

    public void bindButtons(int... buttons) {
        for (int i : buttons) {
            addOrThrow(i, boundTrueButtons);
        }
    }

    public Trigger onTrue(int button, Command command) {
        addOrThrow(button, boundTrueButtons);
        return new JoystickButton(this, button).onTrue(command);
    }

    public Trigger onFalse(int button, Command command) {
        addOrThrow(button, boundFalseButtons);
        return new JoystickButton(this, button).onFalse(command);
    }

    public Trigger whileTrue(int button, Command command) {
        addOrThrow(button, boundTrueButtons);
        return new JoystickButton(this, button).whileTrue(command);
    }

    public Trigger whileFalse(int button, Command command) {
        addOrThrow(button, boundFalseButtons);
        return new JoystickButton(this, button).whileFalse(command);
    }

    public Trigger toggleOnTrue(int button, Command command) {
        addOrThrow(button, boundTrueButtons);
        return new JoystickButton(this, button).toggleOnTrue(command);
    }

    public Trigger toggleOnFalse(int button, Command command) {
        addOrThrow(button, boundFalseButtons);
        return new JoystickButton(this, button).toggleOnFalse(command);
    }

    public double getXInverted() {
        return -getX();
    }

    public double getYInverted() {
        return -getY();
    }

    public double getZInverted() {
        return -getZ();
    }

}
