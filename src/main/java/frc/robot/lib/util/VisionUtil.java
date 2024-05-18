package frc.robot.lib.util;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.Pair;

public class VisionUtil {

    /**
     * Prevent this class from being instantiated.
     */
    private VisionUtil() {}

    /**
     * Converts a pixel input to an angle output(measured in degrees)
     *
     * @param pixel The pixel input
     * @param fov The full field of view of the camera in the desired direction
     * @param resolution The resolution of the camera in the desired direction
     * @return The angle in degrees that the input is relative to the center camera
     */
    public static double pixelsToAngles(double pixel, double fov, double resolution) {
        pixel = MathUtil.clamp(pixel, -resolution/2.0, resolution/2.0);
        return Math.toDegrees(Math.atan(Math.tan(Math.toRadians(fov/2.0))*2.0*pixel/resolution));
    }
    /**
     * Converts an angle input to a pixel output
     *
     * @param angle The angle input(measured in degrees)
     * @param fov The full field of view of the camera in the desired direction
     * @param resolution The resolution of the camera in the desired direction
     * @return The pixel value at which the specified angle will be found
     */
    public static double anglesToPixels(double angle, double fov, double resolution) {
        angle = MathUtil.clamp(angle, -fov/2.0, fov/2.0);
        return (Math.tan(Math.toRadians(angle))*(resolution/2.0))/Math.tan(Math.toRadians(fov/2.0));
    }

    /**
     * Convert pixels to normalized coordinates(-1 to +1) from a centered pixel coordinate system(e.g. -159.5-159.5 if there were 320 pixels horizontally)
     *
     * @param pixel The pixel value to convert
     * @param resolution The resolution of the camera in the desired direction
     *
     * @return The normalized coordinate
     *
     * @see {@link VisionUtil#centerPixels()}
     */
    public static double normalizePixels(double pixel, double resolution) {
        pixel = MathUtil.clamp(pixel, -resolution/2.0, resolution/2.0);
        return pixel / (resolution / 2.0);
    }

    /**
     * Convert pixels to centered coordinates(e.g. 159.5 to +159.5 for 320 resolution) from a pixel coordinate system(e.g. 0-319 for 320 resolution)
     *
     * @param pixel The pixel value to convert
     * @param resolution The resolution of the camera in the desired direction
     * @param inverted If the positive and negative directions are flipped
     * @return The centered coordinate
     */
    public static double centerPixels(double pixel, double resolution, boolean inverted) {
        pixel = MathUtil.clamp(pixel, 0, resolution - 1.0);
        if(inverted) {
            return (((resolution / 2.0) - 0.5) - (double)pixel);
        } else {
            return ((double)pixel - ((resolution / 2.0) - 0.5));
        }
    }

    /**
     * Convert angles to normalized coordinates(-1 to +1) from a centered angle coordinate system(e.g -30 degrees to +30 degrees)
     * @param angle The input angle
     * @param fov The full field of view of the camera in the desired direction
     * @return The normalized angle coordinate
     */
    public static double normalizeAngle(double angle, double fov) {
        angle = MathUtil.clamp(angle, -fov/2.0, fov/2.0);
        return angle / (fov / 2.0);
    }

    /**
     * Get the distance to the target based on its width
     * @param realTargetWidth The physical width of the target
     * @param resolution The resolution of the camera
     * @param fov The field of view of the camera
     * @param targetWidthAngle The measured width of the target(in pixels)
     * @param horizontalOffset The horizontal offset to the center of the target(in pixels)
     * @return The distance to the target in the same units as {@code targetWidth}
     */
    public static double getDistanceWidth(double realTargetWidth, Resolution resolution, FOV fov, double targetWidth, double horizontalOffset) {
        double x = horizontalOffset;
        double width = targetWidth;
        double angleLeft = VisionUtil.pixelsToAngles(VisionUtil.anglesToPixels(x, fov.x(), resolution.x())-width/2.0, fov.x(), resolution.x());
        double angleRight = VisionUtil.pixelsToAngles(VisionUtil.anglesToPixels(x, fov.x(), resolution.x())+width/2.0, fov.x(), resolution.x());
        double widthAngle = angleRight-angleLeft;
        double distance = (realTargetWidth / 2.0) / (Math.tan(Math.toRadians((widthAngle / 2.0))));
        return distance;
    }

    /**
     * Get the distance to the target based on its height
     * @param delta The difference between the height of the camera and the height of the target
     * @param targetAngle The measured angle of the target(in degrees)
     * @return The distance to the target in the same units as {@code delta}
     */
    public static double getDistanceHeight(double delta, double targetAngle) {
        return delta / Math.tan(Math.toRadians(targetAngle));
    }

    /**
     * Gets the angle of the target relative to the robot
     * @param leftCorner The left corner of the target in degrees
     * @param rightCorner The right corner of the target in degrees
     * @param realTargetWidth The physical distance from the left to the right corner
     * @param distance The distance to the target
     * @return The angle of the target relative to the robot in degrees
     */
    public static double getSkewAngle(Pair<Double, Double> leftCorner, Pair<Double, Double> rightCorner, double realTargetWidth, double distance) {
        double right = distance*Math.tan(Math.toRadians(rightCorner.getFirst()));
        double left = distance*Math.tan(Math.toRadians(leftCorner.getFirst()));
        double targetWidth = Math.abs(right - left);
        int directionMultiplier = (leftCorner.getSecond() > rightCorner.getSecond()) ? -1 : 1;
        if(targetWidth/realTargetWidth > 1.0) {
            return 0.0;
        }
        return Util.boundedAngleDegrees(Math.toDegrees(Math.acos(targetWidth/realTargetWidth))) * directionMultiplier;
    }

    public static Pair<Double, Double> midpoint(Pair<Double, Double> point1, Pair<Double, Double> point2) {
        return new Pair<>((point1.getFirst() + point2.getFirst()) / 2.0, (point1.getSecond() + point2.getSecond()) / 2.0);
    }

    public record Resolution(int x, int y) {}
    public record FOV(double x, double y) {}

}
