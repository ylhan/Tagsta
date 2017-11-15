package app;


import app.view.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * An Image Tagging application
 */
public class Tagsta extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Tagsta");
        this.primaryStage.setMaximized(true);

        // Initializes the root elements of the application (border pane, and menu bar)
        initRootLayout();
    }

    /**
     * Initializes the root layout (border pane, and menu bar)
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            // Initialize the root controller and give it a reference to this app
            RootLayoutController rls = loader.getController();
            rls.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @return the stages for this application
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
