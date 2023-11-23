open module com.example.forgalom {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.persistence;
    requires java.naming;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires com.fasterxml.jackson.databind;

    exports app;
}
