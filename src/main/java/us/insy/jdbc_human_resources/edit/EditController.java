package us.insy.jdbc_human_resources.edit;
/*-----------------------------------------------------------------------------
 *              Hoehere Technische Bundeslehranstalt STEYR
 *           Fachrichtung Elektronik und Technische Informatik
 *----------------------------------------------------------------------------*/

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.w3c.dom.Text;
import us.insy.jdbc_human_resources.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author David Hain
 */

public class EditController{
    public TextField textFieldFirstName;
    public TextField textFieldLastName;
    public TextField textFieldEmail;
    public DatePicker datePickerBirthDate;
    public TextField textFieldSalary;
    public ComboBox comboBoxResidence;
    public ComboBox comboBoxDepartment;
    public Spinner spinnerRoomNumber;

    private final DatabaseConnector db = DatabaseConnector.getInstance();
    private String statement;
    private ResultSet result;

    private void updateAll() throws SQLException {
        result = db.executeStatement("SELECT first_name FROM t_human_resources;");
        result.next();
        textFieldFirstName.setText(result.getString("first_name"));
    }
}
