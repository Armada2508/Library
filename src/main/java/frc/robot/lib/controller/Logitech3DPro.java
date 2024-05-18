package frc.robot.lib.controller;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * Logitech G Extreme 3D Pro Joystick
 */
public class Logitech3DPro extends Joystick {

    public Logitech3DPro(final int port) {
        super(port);
    }

    public Trigger b1() {return new Trigger(() -> getRawButton(1));}
    public Trigger b2() {return new Trigger(() -> getRawButton(2));}
    public Trigger b3() {return new Trigger(() -> getRawButton(3));}
    public Trigger b4() {return new Trigger(() -> getRawButton(4));}
    public Trigger b5() {return new Trigger(() -> getRawButton(5));}
    public Trigger b6() {return new Trigger(() -> getRawButton(6));}
    public Trigger b7() {return new Trigger(() -> getRawButton(7));}
    public Trigger b8() {return new Trigger(() -> getRawButton(8));}
    public Trigger b9() {return new Trigger(() -> getRawButton(9));}
    public Trigger b10() {return new Trigger(() -> getRawButton(10));}
    public Trigger b11() {return new Trigger(() -> getRawButton(11));}
    public Trigger b12() {return new Trigger(() -> getRawButton(12));}
    public Trigger trigger() {return b1();}

    public double getXInverted() {
        return -getX();
    }

    public double getYInverted() {
        return -getY();
    }

    public double getZInverted() {
        return -getZ();
    }

    public double getSliderInverted() {
        return -getThrottle();
    }

}
