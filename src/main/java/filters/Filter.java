package filters;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.function.Function;

public class Filter implements Function<Image, Image> {

    private final String name;
    private final Function<Color, Color> colorMap;

    public Filter(String name, Function<Color, Color> colorMap) {
        this.name = name;
        this.colorMap = colorMap;
    }

    public String getName() {
        return name;
    }

    public static int getLevel() {
        return -85;
    }


    @Override
    public Image apply(Image prototype) {
        int w = (int) prototype.getWidth();
        int h = (int) prototype.getHeight();
        WritableImage image = new WritableImage(w, h);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c1 = prototype.getPixelReader().getColor(x, y);
                Color c2 = colorMap.apply(c1);
                image.getPixelWriter().setColor(x, y, c2);
            }
        }
        return image;
    }
}
