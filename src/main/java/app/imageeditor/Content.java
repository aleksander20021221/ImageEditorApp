package app.imageeditor;

import filters.Filter;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

public class Content {

    private static final List<Filter> filters = Arrays.asList(
            new Filter("Original", c -> c),
            new Filter("Invert", Color::invert),
            new Filter("GreyScale", Color::grayscale),
            new Filter("Black&White", c -> valueOf(c) < 1.45 ? Color.BLACK : Color.WHITE),
            new Filter("Red", c -> Color.color(0.5, c.getGreen(), c.getBlue())),
            new Filter("Green", c -> Color.color(c.getRed(), 0.5, c.getBlue())),
            new Filter("Blue", c -> Color.color(c.getRed(), c.getGreen(), 0.5)),
            new Filter("Brightness", c -> Color.rgb(truncate((int) (c.getRed() * 255) + Filter.getLevel()),
                    truncate((int) (c.getGreen() * 255) + Filter.getLevel()),
                    truncate((int) (c.getBlue() * 255) + Filter.getLevel())))
    );

    public static List<Filter> getFilters() {
        return filters;
    }

    private static double valueOf(Color c) {
        return c.getRed() + c.getGreen() + c.getBlue();
    }

    private static int truncate(int value) {
        if (value > 255) value = 255;
        if (value < 0) value = 0;
        return value;
    }
}
