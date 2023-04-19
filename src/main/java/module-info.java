module scheduling_app.scheduling_app {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens scheduling_app.scheduling_app to javafx.fxml;
    exports scheduling_app.scheduling_app;

    opens controller to javafx.fxml;
    exports controller;

    opens model to javafx.fxml;
    exports model;

    opens helper to javafx.fxml;
    exports helper;
}