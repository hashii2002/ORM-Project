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
    opens org.example.serenitytherapycenterorm.entity to org.hibernate.orm.core, jakarta.persistence;
    opens org.example.serenitytherapycenterorm.dto to javafx.base;

    exports org.example.serenitytherapycenterorm;
    exports org.example.serenitytherapycenterorm.controller;
    exports org.example.serenitytherapycenterorm.entity;
    exports org.example.serenitytherapycenterorm.dto;
    exports org.example.serenitytherapycenterorm.exception;
}