open module com.example.forgalom {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires java.sql;
    requires com.fasterxml.jackson.databind;

    /*requires hibernate.jpa;
    requires hibernate.core;*/
    requires org.hibernate.orm.core;
    requires java.persistence;
    //requires java.xml.bind;
    requires javax.jws;
    requires java.xml.ws;
    requires java.xml.bind;
    requires weka.stable;
}
