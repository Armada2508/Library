package frc.robot.lib.util;
import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Feet;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Rotations;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EncoderTest {

    private static final double EPSILON = 1e-3;

    @Test
    void testToDistanceMeasure() {
        var rotations = Rotations.of(3);
        double gearRatio = 1;
        var wheelDiameter = Meters.of(2);
        var result = Encoder.angularToLinear(rotations, gearRatio, wheelDiameter);
        assertEquals(18.85, result.in(Meters), EPSILON);
        gearRatio = 10.71;
        result = Encoder.angularToLinear(rotations, gearRatio, wheelDiameter);
        assertEquals(1.76, result.in(Meters), EPSILON);
        rotations = Rotations.of(10);
        wheelDiameter = Inches.of(6);
        result = Encoder.angularToLinear(rotations, gearRatio, wheelDiameter);
        assertEquals(0.447, result.in(Meters), EPSILON);
        rotations = Degrees.of(90);
        gearRatio = 2;
        wheelDiameter = Inches.of(18);
        result = Encoder.angularToLinear(rotations, gearRatio, wheelDiameter);
        assertEquals(0.179, result.in(Meters), EPSILON);
    }

    @Test
    void testToRotationsMeasure() {
        var distance = Meters.of(3);
        double gearRatio = 1;
        var wheelDiameter = Meters.of(2);
        var result = Encoder.linearToAngular(distance, gearRatio, wheelDiameter);
        assertEquals(0.477, result.in(Rotations), EPSILON);
        distance = Feet.of(8);
        gearRatio = 12.75;
        wheelDiameter = Meters.of(3);
        result = Encoder.linearToAngular(distance, gearRatio, wheelDiameter);
        assertEquals(3.298, result.in(Rotations), EPSILON);
        distance = Meters.of(5.5);
        gearRatio = 6;
        wheelDiameter = Inches.of(3);
        result = Encoder.linearToAngular(distance, gearRatio, wheelDiameter);
        assertEquals(137.85, result.in(Rotations), EPSILON);
    }

}
