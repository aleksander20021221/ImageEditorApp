package app.imageeditor;

import filters.Blur;
import filters.FilterGaussian;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class ImageEditorApp extends Application {
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
        root.setPrefSize(800, 600);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(193, 250, 255),
                new CornerRadii(0), Insets.EMPTY)));

        //String filePathBear = "src/main/java/BEAR.jpg";
        Image image = new Image(file.toURI().toString());

        ImageView view1 = new ImageView(image);
        view1.setFitHeight(400);
        view1.setFitWidth(400);
        ImageView view2 = new ImageView();
        view2.setFitHeight(400);
        view2.setFitWidth(400);

        MenuBar bar = new MenuBar();
        Menu menu = new Menu("Filter");
        bar.setBackground(new Background(new BackgroundFill(Color.rgb(124, 181, 255),
                new CornerRadii(0), Insets.EMPTY)));

        {
            //Color filters
            Content.getFilters().forEach(filter -> {
                MenuItem item = new MenuItem(filter.getName());
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
                        img = filterGaussian.ffffff(image);
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
        root.setCenter(new HBox(view1, view2));

        return root;

    }


    public static void main(String[] args) {
        launch(args);
    }

}