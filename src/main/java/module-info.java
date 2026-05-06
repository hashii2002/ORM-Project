module org.example.serenitytherapycenterorm {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;


    opens org.example.serenitytherapycenterorm to javafx.fxml;
    exports org.example.serenitytherapycenterorm;
}