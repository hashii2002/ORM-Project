module org.example.serenitytherapycenterorm {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires jakarta.persistence;
    requires static lombok;
    requires jbcrypt;
    requires mysql.connector.j;
    requires org.hibernate.orm.jcache;
    requires cache.api;



    opens org.example.serenitytherapycenterorm to javafx.fxml;
    opens org.example.serenitytherapycenterorm.controller to javafx.fxml;

    exports org.example.serenitytherapycenterorm;
    exports org.example.serenitytherapycenterorm.controller;
    exports org.example.serenitytherapycenterorm.entity;
}