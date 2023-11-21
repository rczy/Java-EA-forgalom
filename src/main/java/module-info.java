module com.example.forgalom {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.persistence;
    requires java.naming;
    requires java.sql;
    requires org.hibernate.orm.core;

    opens app to javafx.fxml;
    opens app.models to org.hibernate.orm.core;

    exports app;
}
