package Pandoc.UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Pandoc.Native.Operations;

public class Main extends Application {

    static protected Stage stage;
    private static boolean isPandocFound;

    public static String[] arguments;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        if (isPandocFound) {
            Parent root = FXMLLoader.load(getClass().getResource("/MainWindow.fxml"));
            primaryStage.setTitle("Pandoc UI");
            primaryStage.setScene(new Scene(root, 800, 450));
        } else {
            Parent root = FXMLLoader.load(getClass().getResource("/Error.fxml"));
            primaryStage.setTitle("Pandoc UI - Error");
            primaryStage.setScene(new Scene(root, 600, 200));
        }
        primaryStage.show();
    }


    public static void main(String[] args) {
        arguments = args;
        if (Operations.checkForPandoc()) {
            isPandocFound = true;
        } else {
            isPandocFound = false;
        }
        launch(args);
    }
}
