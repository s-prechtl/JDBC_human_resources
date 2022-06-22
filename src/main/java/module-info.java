module us.insy.jdbc_human_resources {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens us.insy.jdbc_human_resources to javafx.fxml;
    exports us.insy.jdbc_human_resources;

    exports us.insy.jdbc_human_resources.add;
    opens us.insy.jdbc_human_resources.add to javafx.fxml;

    exports us.insy.jdbc_human_resources.show;
    exports us.insy.jdbc_human_resources.edit;
    opens us.insy.jdbc_human_resources.show to javafx.fxml;
}