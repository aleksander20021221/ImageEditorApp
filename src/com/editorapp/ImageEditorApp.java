package com.app.imageeditor;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

public class ImageEditorApp extends Application{
    @Override
    public void start(Stage stage) {

        Stage stage1 = new Stage();
        stage1.setTitle("File Chooser Sample");

        final FileChooser fileChooser = new FileChooser();

        final Button openButton = new Button("Choose Background Image");
        final StackPane stack = new StackPane();

        EventHandler<ActionEvent> event = e -> {
            File file = fileChooser.showOpenDialog(stage1);
            stage.setScene(new Scene(createContent(file)));
            stage.setTitle("Image filtering");
            stage.show();
        };
        openButton.setOnAction(event);

        stack.getChildren().add(openButton);
        stage1.setScene(new Scene(stack, 300, 300));
        stage1.show();

    }
    private Parent createContent(File file) {
        BorderPane root = new BorderPane();
        root.setPrefSize(800,600);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(193,250,255),
                new CornerRadii(0), Insets.EMPTY)));

        //String filePathBear = "src/main/java/BEAR.jpg";
        Image image = new Image(file.toURI().toString());

        ImageView view1 = new ImageView(image);   view1.setFitHeight(400);  view1.setFitWidth(400);
        ImageView view2 = new ImageView();  view2.setFitHeight(400); view2.setFitWidth(400);

        MenuBar bar = new MenuBar();
        Menu menu= new Menu("Filter");
        bar.setBackground(new Background(new BackgroundFill(Color.rgb(124,181,255),
                new CornerRadii(0), Insets.EMPTY)));

        {
            //Color filters
            filters.forEach(filter -> {
                MenuItem item = new MenuItem(filter.name);
                item.setOnAction(actionEvent -> view2.setImage(filter.apply(view1.getImage())));
                menu.getItems().add(item);

            });
            //Gaussian
            {
                MenuItem item = new MenuItem("Gaussian Blur");
                item.setOnAction(actionEvent -> {
                    FilterGaussian filterGaussian = new FilterGaussian(4, image);
                    Image img = null;
                    long m = System.currentTimeMillis();
                    try {
                        img = FilterGaussian.ffffff(image);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    System.out.println((double) (System.currentTimeMillis() - m));

                    view2.setImage(img);
                    view2.setFitHeight(400);
                    view2.setFitWidth(400);
                });
                menu.getItems().add(item);
            }
            //MiddleBlur
            {
                MenuItem item = new MenuItem("Blur");
                item.setOnAction(actionEvent -> {
                    try {
                        view2.setImage(Blur.apply(view1.getImage(), (byte) 1));
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                menu.getItems().add(item);
            }
        }

        bar.getMenus().add(menu);
        root.setTop(bar);
        root.setCenter(new HBox(view1,view2));

        return root;

    }
    private final List<Filter> filters = Arrays.asList(
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

    private static class Filter implements Function<Image, Image> {

        private final String name;
        private final Function<Color,Color> colorMap;
        Filter(String name, Function<Color, Color>colorMap){
            this.name=name;
            this.colorMap =colorMap;
        }

        public static int getLevel() {
            return -85;
        }


        @Override
        public Image apply(Image prototype) {
            int w = (int) prototype.getWidth();
            int h = (int) prototype.getHeight();
            WritableImage image = new WritableImage(w,h);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    Color c1 = prototype.getPixelReader().getColor(x,y);
                    Color c2 = colorMap.apply(c1);
                    image.getPixelWriter().setColor(x,y,c2);
                }
            }
            return image;
        }
    }

    private static class Blur  {

        private static void square(WritableImage proc, int a, int b, byte radius){
            float red=0;
            float green=0;
            float blue=0;
            for (int y = b-radius; y <=b+radius ; y++) {
                for (int x = a-radius; x <=a+radius ; x++) {
                    red+=proc.getPixelReader().getColor(x,y).getRed();
                    green+=proc.getPixelReader().getColor(x,y).getGreen();
                    blue+=proc.getPixelReader().getColor(x,y).getBlue();

                }
            }
            int degree= (int) Math.pow(2*radius+1,2);
            red= red /degree;
            green= green /degree;
            blue= blue /degree;
            if (red>1) red=1;
            if (green>1) green= 1;
            if (blue>1) blue=1;

            Color color= new Color(red,green,blue,1);
            for (int y = b-radius; y <b+radius+1 ; y++) {
                for (int x = a-radius; x <a+radius+1 ; x++) {
                    proc.getPixelWriter().setColor(x,y,color);
                }
            }
        }
        public static Image apply(Image source, byte radius) throws ExecutionException, InterruptedException {
            int w = (int) source.getWidth();
            int h = (int) source.getHeight();
            WritableImage image = new WritableImage(w,h);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    Color c1 = source.getPixelReader().getColor(x,y);
                    image.getPixelWriter().setColor(x,y,c1);
                }
            }


            ThreadPoolExecutor executor= new ThreadPoolExecutor(8,15,3L,
                    TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(515));
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());


            Future<WritableImage> future=executor.submit(() -> {
                for (int y = radius; y <(h-radius) ; y++) {
                    for (int x = radius; x <(w-radius) ; x++) {
                        square(image,x,y,radius);
                    }
                }
                return image;
            });
            return future.get();
//            return image;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private double valueOf (Color c){
        return c.getRed()+c.getGreen()+c.getBlue();
    }

    private static int truncate(int value) {
        if (value > 255) value = 255;
        if (value < 0) value = 0;
        return value;
    }
}