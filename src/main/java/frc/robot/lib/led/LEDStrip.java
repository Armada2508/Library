package frc.robot.lib.led;

import static edu.wpi.first.wpilibj2.command.Commands.runOnce;
import static edu.wpi.first.wpilibj2.command.Commands.waitSeconds;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RepeatCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

/**
* Will be removed when https://github.com/wpilibsuite/allwpilib/pull/6344 is merged.
*/
public class LEDStrip {

    private final AddressableLED strip;
    private final AddressableLEDBuffer buffer;
    private int hue;
    private int bandIndex;

    /**
     * Creates a new LEDStrip Object
     * @param port The PWM port that the LED strip is connected to
     * @param length The length of the LED strip
     */
    public LEDStrip(int port, int length) {
        strip = new AddressableLED(port);
        buffer = new AddressableLEDBuffer(length);
        strip.setLength(length);
        strip.setData(buffer);
        strip.start();
    }

    /**
     * Set the entire strip to one color
     * @param color The color to set the strip to
     */
    public void set(Color color) {
        set(0, buffer.getLength(), color);
    }

    /**
     * Set an led to a color
     * @param index The index of the LED to set
     * @param color The color to set the specified LED to
     */
    public void set(int index, Color color) {
        buffer.setLED(index, color);
        strip.setData(buffer);
    }

    /**
     * Set a range of the LED strip to a certain color
     * @param start The starting index
     * @param end The ending index
     * @param color The color to set the range to
     */
    public void set(int start, int end, Color color) {
        for (int i = start; i < end; i++) {
            buffer.setLED(i, color);
        }
        strip.setData(buffer);
    }

    /**
     * Set the LED strip to a certain color
     * @param h The hue of the color
     * @param s The saturation of the color
     * @param v The value of the color
     */
    public void setHSV(int h, int s, int v) {
        setHSV(0, buffer.getLength(), h, s, v);
    }

    /**
     * Set an LED to a certain color
     * @param index The index of the LED to set
     * @param h The hue of the color
     * @param s The saturation of the color
     * @param v The value of the color
     */
    public void setHSV(int index, int h, int s, int v) {
        buffer.setHSV(index, h, s, v);
        strip.setData(buffer);
    }

    /**
     * Set a range of the LED strip to a certain color
     * @param start The starting index
     * @param end The ending index
     * @param h The hue of the color
     * @param s The saturation of the color
     * @param v The value of the color
     */
    public void setHSV(int start, int end, int h, int s, int v) {
        for (int i = start; i < end; i++) {
            buffer.setHSV(i, h, s, v);
        }
        strip.setData(buffer);
    }


    /**
     * @return The length of the LED Strip
     */
    public int getLength() {
        return buffer.getLength();
    }

    /**
     * Get the color of an led
     * @param led The index of the LED
     * @return The color of the LED
     */
    public Color getColor(int index) {
        return buffer.getLED(index);
    }

    /**
     * Make a rainbow effect
     * @param increment The hue increment(speed)
     */
    public void rainbow(double increment) {
        rainbow(increment, 255, 255);
    }

    /**
     * Make a rainbow effect
     * @param increment The hue increment(speed)
     * @param s The saturation of the color
     * @param v The value(brightness) of the color
     */
    public void rainbow(double increment, int s, int v) {
        hue += increment;
        hue %= 180;
        setHSV(hue, s, v);
    }

    /**
     * Make a rainbow band effect
     * @param increment The hue increment(speed)
     */
    public void rainbowBand(double increment) {
        rainbowBand(increment, 255, 255);
    }

    /**
     * Make a rainbow band effect
     * @param increment The hue increment(speed)
     * @param s The saturation of the color
     * @param v The value(brightness) of the color
     */
    public void rainbowBand(double increment, int s, int v) {
        hue += increment;
        hue %= 180;
        for (int i = 0; i < buffer.getLength(); i++) {
            int newHue = (hue + (i * 180 / buffer.getLength())) % 180;
            buffer.setHSV(i, newHue, s, v);
        }
        strip.setData(buffer);
    }

    /**
     * Creates a band effect
     * @param increment How much the effect increments every time this method is called(speed)
     * @param color The color of the effect
     */
    public void band(double increment, Color color) {
        band(increment, color, 1);
    }

    /**
     * Creates a band effect
     * @param increment How much the effect increments every time this method is called(speed)
     * @param color The color of the effect
     * @param nodes How many nodes the effect has
     */
    public void band(double increment, Color color, int nodes) {
        bandIndex += increment;
        for (int i = 0; i < buffer.getLength(); i++) {
            int halfWavelength = buffer.getLength() / (2 * nodes);
            int index = ( i + bandIndex ) % buffer.getLength();
            int brightness = (halfWavelength - Math.abs(index % (2*halfWavelength) - halfWavelength) ) / halfWavelength;
            Color ledColor = new Color(color.red * brightness, color.green * brightness, color.blue * brightness);
            buffer.setLED(index, ledColor);
        }
        strip.setData(buffer);
    }

    /**
     * @param color1
     * @param color2
     * @param pulseTimeSeconds
     * @return A command that flashes the led strip between two colors which takes pulseTimeSeconds to do.
     */
    public Command pulseCommand(Color color1, Color color2, double pulseTimeSeconds) {
        return new RepeatCommand(
            new SequentialCommandGroup(
                runOnce(() -> set(color1)),
                waitSeconds(pulseTimeSeconds),
                runOnce(() -> set(color2)),
                waitSeconds(pulseTimeSeconds)
            )
        );
    }

}
