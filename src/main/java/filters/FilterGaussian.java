package filters;

import java.util.concurrent.*;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class FilterGaussian implements Runnable {
    private static int blurRadius;
    private static Image image;
    public static double[][] weightArr;

    public FilterGaussian(int blurRadius, javafx.scene.image.Image image) {
        this.blurRadius = blurRadius;
        this.image = image;
        weightArr = new double[blurRadius * 2 + 1][blurRadius * 2 + 1];
        calculateWeightMatrix();
        getFinalWeightMatrix();
    }

    public static int getBlurRadius() {
        return blurRadius;
    }

    private static double getR(int x, int y) {
        return image.getPixelReader().getColor(x, y).getRed();
    }

    private static double getG(int x, int y) {
        return image.getPixelReader().getColor(x, y).getGreen();
    }

    private static double getB(int x, int y) {
        return image.getPixelReader().getColor(x, y).getBlue();
    }

    private static double[][] getColorMatrix(int x, int y, int whichColor) {

        int startX = x - blurRadius;
        int startY = y - blurRadius;
        int counter = 0;

        int length = blurRadius * 2 + 1;
        double[][] arr = new double[length][length];

        for (int i = startX; i < startX + length; i++) {
            for (int j = startY; j < startY + length; j++) {
                if (whichColor == 1) {
                    arr[counter % length][counter / length] = getR(i, j);
                } else if (whichColor == 2) {
                    arr[counter % length][counter / length] = getG(i, j);
                } else if (whichColor == 3) {
                    arr[counter % length][counter / length] = getB(i, j);
                }
                counter++;
            }
        }

        return arr;
    }

    private void calculateWeightMatrix() {

        for (int i = 0; i < blurRadius * 2 + 1; i++) {
            for (int j = 0; j < blurRadius * 2 + 1; j++) {
                weightArr[i][j] = getWeight(j - blurRadius, blurRadius - i);
//                System.out.print(weightArr[i][j]+"\t");
            }
//            System.out.println();
        }

    }

    private static double getWeight(int x, int y) {

        double sigma = (blurRadius * 2 + 1) / 2;

        return (1 / (2 * Math.PI * sigma * sigma)) * Math.pow(Math.E, ((-(x * x + y * y)) / ((2 * sigma) * (2 * sigma))));
    }

    private void getFinalWeightMatrix() {

        int length = blurRadius * 2 + 1;
        double weightSum = 0;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                weightSum += weightArr[i][j];
            }
        }
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                weightArr[i][j] = weightArr[i][j] / weightSum;
//                System.out.print(weightArr[i][j]+"\t");
            }
            //System.out.print("\n");
        }

    }

    private static double getBlurColor(int x, int y, int whichColor) {

        double blurGray = 0;
        double[][] colorMap = getColorMatrix(x, y, whichColor);

        int length = blurRadius * 2 + 1;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                blurGray += weightArr[i][j] * colorMap[i][j];
            }
        }

        return blurGray;
    }

    public static Image ffffff(Image image) throws InterruptedException, ExecutionException {
        Image answer = getBluredImg(image);
        return answer;
    }


    private static WritableImage getBluredImg(Image image) throws InterruptedException, ExecutionException {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage bi = new WritableImage(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c1 = image.getPixelReader().getColor(x, y);
                bi.getPixelWriter().setColor(x, y, c1);
            }
        }


        //потоки созданные вручную
//        Thread wholeThread = new Thread(() -> {
//
//            Thread firstTread = new Thread(() -> {
//                painting(bi, 0, (int) ((width - 2 * blurRadius) / 4),
//                        0, (int) (height - 2 * blurRadius) / 2);
//            });
//            firstTread.start();
//
//            Thread secondThread = new Thread(() -> {
//                painting(bi, (int) ((width - 2 * blurRadius) / 4), (int) ((width - 2 * blurRadius) / 2),
//                        0, (int) (height - 2 * blurRadius) / 2);
//            });
//            secondThread.start();
//
//            Thread thirdThread = new Thread(() -> {
//                painting(bi, (int) ((width - 2 * blurRadius) / 2), (int) ((width - 2 * blurRadius) / 4) * 3,
//                        0, (int) (height - 2 * blurRadius) / 2);
//            });
//            thirdThread.start();
//
//            Thread forthThread = new Thread(() -> {
//                painting(bi, (int) ((width - 2 * blurRadius) / 4) * 3, (int) (width - 2 * blurRadius),
//                        0, (int) (height - 2 * blurRadius) / 2);
//            });
//            forthThread.start();
//
//            Thread firstTread1 = new Thread(() -> {
//                painting(bi, 0, (int) ((width - 2 * blurRadius) / 4),
//                        (int) (height - 2 * blurRadius) / 2, (int) (height - 2 * blurRadius));
//            });
//            firstTread1.start();
//
//            Thread secondThread1 = new Thread(() -> {
//                painting(bi, (int) ((width - 2 * blurRadius) / 4), (int) ((width - 2 * blurRadius) / 2),
//                        (int) (height - 2 * blurRadius) / 2, (int) (height - 2 * blurRadius));
//            });
//            secondThread1.start();
//
//            Thread thirdThread1 = new Thread(() -> {
//                painting(bi, (int) ((width - 2 * blurRadius) / 2), (int) ((width - 2 * blurRadius) / 4) * 3,
//                        (int) (height - 2 * blurRadius) / 2, (int) (height - 2 * blurRadius));
//            });
//            thirdThread1.start();
//
//            Thread forthThread1 = new Thread(() -> {
//                painting(bi, (int) ((width - 2 * blurRadius) / 4) * 3, (int) (width - 2 * blurRadius),
//                        (int) (height - 2 * blurRadius) / 2, (int) (height - 2 * blurRadius));
//            });
//            forthThread1.start();
//
//
//            try {
//                firstTread.join();
//                secondThread.join();
//                thirdThread.join();
//                forthThread.join();
//                firstTread1.join();
//                secondThread1.join();
//                thirdThread1.join();
//                forthThread1.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        wholeThread.start();
//        wholeThread.join();

//THREADPOOLEXECUTOR
        ThreadPoolExecutor executor= new ThreadPoolExecutor(8,15,3L,
                TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(515));
       executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());


        Future<WritableImage>future=executor.submit(() -> {
            painting(bi,0, (int) (width - 2 * blurRadius) ,
                    0,(int)(height-2 * blurRadius));
            return bi;
        });
        return future.get();

        //без многопоточности
//        painting(bi,0, (int) (width - 2 * blurRadius) ,
//                0,(int)(height-2 * blurRadius));


        //return bi;
    }

    private static void painting(WritableImage writableImage, int x1, int x2, int y1, int y2) {
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                double r = getBlurColor(blurRadius + x, blurRadius + y, 1);
                double g = getBlurColor(blurRadius + x, blurRadius + y, 2);
                double b = getBlurColor(blurRadius + x, blurRadius + y, 3);
                Color color = new Color(r, g, b, 1.0F);
                writableImage.getPixelWriter().setColor(x, y, color);
            }
        }
    }

    @Override
    public void run() {
    }
}