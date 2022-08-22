package filters;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.concurrent.*;

public class Blur {
    private static void square(WritableImage proc, int a, int b, byte radius) {
        float red = 0;
        float green = 0;
        float blue = 0;
        for (int y = b - radius; y <= b + radius; y++) {
            for (int x = a - radius; x <= a + radius; x++) {
                red += proc.getPixelReader().getColor(x, y).getRed();
                green += proc.getPixelReader().getColor(x, y).getGreen();
                blue += proc.getPixelReader().getColor(x, y).getBlue();

            }
        }
        int degree = (int) Math.pow(2 * radius + 1, 2);
        red = red / degree;
        green = green / degree;
        blue = blue / degree;
        if (red > 1) red = 1;
        if (green > 1) green = 1;
        if (blue > 1) blue = 1;

        Color color = new Color(red, green, blue, 1);
        for (int y = b - radius; y < b + radius + 1; y++) {
            for (int x = a - radius; x < a + radius + 1; x++) {
                proc.getPixelWriter().setColor(x, y, color);
            }
        }
    }

    public static Image apply(Image source, byte radius) throws ExecutionException, InterruptedException {
        int w = (int) source.getWidth();
        int h = (int) source.getHeight();
        WritableImage image = new WritableImage(w, h);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c1 = source.getPixelReader().getColor(x, y);
                image.getPixelWriter().setColor(x, y, c1);
            }
        }


        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 15, 3L,
                TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(515));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());


        Future<WritableImage> future = executor.submit(() -> {
            for (int y = radius; y < (h - radius); y++) {
                for (int x = radius; x < (w - radius); x++) {
                    square(image, x, y, radius);
                }
            }
            return image;
        });
        return future.get();
//            return image;
    }
}
