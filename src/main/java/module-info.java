module com.example.imageeditorappmain {
    requires javafx.controls;
    requires javafx.fxml;


    opens app.imageeditor to javafx.fxml;
    exports app.imageeditor;
}