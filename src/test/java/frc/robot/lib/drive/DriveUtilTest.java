package frc.robot.lib.drive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DriveUtilTest {

    static final double EPSILON = 1e-3;

    @Test
    void testProcessDeadband() {
        assertEquals(0, DriveUtil.processDeadband(0, 0.5, 1), EPSILON);
        assertEquals(0, DriveUtil.processDeadband(0.15, 0.25, 1), EPSILON);
        assertEquals(0.3, DriveUtil.processDeadband(0.3, 0.1, 1.5), EPSILON);
        assertEquals(0.111, DriveUtil.processDeadband(0.15, 0.1, 2), EPSILON);
        assertEquals(0.777, DriveUtil.processDeadband(0.8, 0.1, 1), EPSILON);
    }

    @Test
    void testSquareInput() {
        assertEquals(0.25, DriveUtil.squareInput(0.5), EPSILON);
        assertEquals(-0.25, DriveUtil.squareInput(-0.5), EPSILON);
        assertEquals(1, DriveUtil.squareInput(1), EPSILON);
        assertEquals(0, DriveUtil.squareInput(0), EPSILON);
        assertEquals(-1, DriveUtil.squareInput(-1), EPSILON);
    }

    @Test
    void testConstantCurvature() {
        assertEquals(0, DriveUtil.constantCurvature(0, 1, 0), EPSILON);
        assertEquals(1, DriveUtil.constantCurvature(1, 1, 0), EPSILON);
        assertEquals(0.75, DriveUtil.constantCurvature(0.5, 0.5, 0.5), EPSILON);
        assertEquals(0.5, DriveUtil.constantCurvature(0, 1, 0.5), EPSILON);
        assertEquals(0.35, DriveUtil.constantCurvature(0.1, 1, 0.25), EPSILON);
        assertEquals(-0.5, DriveUtil.constantCurvature(1, -0.5, 0), EPSILON);
        assertEquals(-0.5, DriveUtil.constantCurvature(-1, -0.5, 0), EPSILON);
    }

    @Test
    void testNormalizeValues() {
        var pair1 = DriveUtil.normalizeValues(0.25, 0.25);
        assertEquals(0.25, pair1.getFirst(), EPSILON);
        assertEquals(0.25, pair1.getSecond(), EPSILON);
        var pair2 = DriveUtil.normalizeValues(0, 0);
        assertEquals(0.0, pair2.getFirst(), EPSILON);
        assertEquals(0.0, pair2.getSecond(), EPSILON);
        var pair3 = DriveUtil.normalizeValues(-1, -1);
        assertEquals(-1, pair3.getFirst(), EPSILON);
        assertEquals(-1, pair3.getSecond(), EPSILON);
        var pair4 = DriveUtil.normalizeValues(1, 1.5);
        assertEquals(1.0/1.5, pair4.getFirst(), EPSILON);
        assertEquals(1.0, pair4.getSecond(), EPSILON);
        var pair5 = DriveUtil.normalizeValues(1.4, 2.3);
        assertEquals(1.4/2.3, pair5.getFirst(), EPSILON);
        assertEquals(1.0, pair5.getSecond(), EPSILON);
        var pair6 = DriveUtil.normalizeValues(1.2, 0.5);
        assertEquals(1.0, pair6.getFirst(), EPSILON);
        assertEquals(0.5/1.2, pair6.getSecond(), EPSILON);
        var pair7 = DriveUtil.normalizeValues(-1.6, 0.3);
        assertEquals(-1.0, pair7.getFirst(), EPSILON);
        assertEquals(0.3/1.6, pair7.getSecond(), EPSILON);
    }
    
}
