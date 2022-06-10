module us.insy.jdbc_human_resources {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens us.insy.jdbc_human_resources to javafx.fxml;
    exports us.insy.jdbc_human_resources;
}