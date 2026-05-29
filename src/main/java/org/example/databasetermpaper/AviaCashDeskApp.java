package org.example.databasetermpaper;

import org.example.databasetermpaper.config.AppConfig;
import org.example.databasetermpaper.db.Database;
import org.example.databasetermpaper.repository.ReferenceRepository;
import org.example.databasetermpaper.repository.ReportRepository;
import org.example.databasetermpaper.ui.MainWindow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AviaCashDeskApp extends Application {
    @Override
    public void start(Stage stage) {
        AppConfig config = AppConfig.load();
        Database database = new Database(config);
        ReferenceRepository referenceRepository = new ReferenceRepository(database);
        ReportRepository reportRepository = new ReportRepository(database);

        MainWindow mainWindow = new MainWindow(referenceRepository, reportRepository);
        Scene scene = new Scene(mainWindow.createContent(), 1180, 760);

        stage.setTitle(config.appTitle());
        stage.setScene(scene);
        stage.setMinWidth(980);
        stage.setMinHeight(620);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
