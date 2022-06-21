package us.insy.jdbc_human_resources.add;

import us.insy.jdbc_human_resources.HelloApplication;

import java.sql.SQLException;

public class AddException extends SQLException {
    AddException(String message) {
        super(message);
        //HelloApplication.errorBox(message);
    }
}
