package us.insy.jdbc_human_resources.show;
/*-----------------------------------------------------------------------------
 *              Hoehere Technische Bundeslehranstalt STEYR
 *           Fachrichtung Elektronik und Technische Informatik
 *----------------------------------------------------------------------------*/

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import us.insy.jdbc_human_resources.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Kurzbeschreibung
 *
 * @author :
 * @date :
 * @details Detailbeschreibung
 */
public class ViewController {

    private final DatabaseConnector db = DatabaseConnector.getInstance();

    public CheckBox checkBoxPersID;
    public CheckBox checkBoxFirstName;
    public CheckBox checkBoxLastname;
    public CheckBox checkBoxGender;
    public CheckBox checkBoxEmail;
    public CheckBox checkBoxBirthDate;
    public CheckBox checkBoxSalary;
    public CheckBox checkBoxLivingIn;
    public CheckBox checkBoxZipCode;
    public CheckBox checkBoxDepartment;
    public CheckBox checkBoxRoomNr;
    public CheckBox checkBoxFloorLevel;
    public CheckBox checkBoxRoomSize;
    public Label labelID;
    public Label labelFirstName;
    public Label labelLastName;
    public Label labelEmail;
    public Label labelBirthDate;
    public Label labelSalary;
    public Label labelCity;
    public Label labelZipCode;
    public Label labelDepartmentName;
    public Label labelLevel;
    public Label labelRoomNumber;
    public Label labelRoomSize;
    public Label labelGender;

    private String statement;
    private ResultSet result;

    public void initialize() throws SQLException {
        update();
    }

    private void update() throws SQLException {
        updateSQLStatement();
        result = db.executeStatement(statement);
        result.next();
        updateLabels();
    }

    private void updateLabels() throws SQLException {
        updateLabelInt(labelID, checkBoxPersID, "hr.person_id");
        updateLabelString(labelFirstName, checkBoxFirstName, "first_name");
        updateLabelString(labelLastName, checkBoxLastname, "last_name");
        updateLabelString(labelGender, checkBoxGender, "gender");
        updateLabelString(labelBirthDate, checkBoxBirthDate, "date_of_birth");
        updateLabelString(labelEmail, checkBoxEmail, "email");
        updateLabelInt(labelSalary, checkBoxSalary, "salary");
        updateLabelString(labelCity, checkBoxLivingIn, "city.name");
        updateLabelString(labelZipCode, checkBoxZipCode, "hr.zip");
        updateLabelString(labelDepartmentName, checkBoxDepartment, "dp.name");
        updateLabelInt(labelRoomNumber, checkBoxRoomNr, "hroom.room_nr");
        updateLabelString(labelLevel, checkBoxFloorLevel, "hroom.room_floor");
        updateLabelInt(labelRoomSize, checkBoxRoomSize, "room.size");
    }

    private void updateLabelInt(Label label, CheckBox shown, String columnLabel) throws SQLException {
        label.setVisible(shown.isSelected());
        if (shown.isSelected()){
            label.setText(String.valueOf(result.getInt(columnLabel)));
        }
    }

    private void updateLabelString(Label label, CheckBox shown, String columnLabel) throws SQLException {
        label.setVisible(shown.isSelected());
        if (shown.isSelected()){
            label.setText(String.valueOf(result.getString(columnLabel)));
        }
    }

    private void updateSQLStatement(){
        statement = "SELECT ";
        if (checkBoxPersID.isSelected()) {
            statement += "hr.person_id, ";
        }
        if (checkBoxFirstName.isSelected()) {
            statement += "first_name, ";
        }
        if (checkBoxLastname.isSelected()) {
            statement += "last_name, ";
        }
        if (checkBoxEmail.isSelected()) {
            statement += "email, ";
        }
        if (checkBoxBirthDate.isSelected()) {
            statement += "date_of_birth, ";
        }
        if (checkBoxZipCode.isSelected()) {
            statement += "hr.zip, ";
        }
        if (checkBoxDepartment.isSelected()) {
            statement += "hr.department_id, ";
        }
        if (checkBoxRoomSize.isSelected()){
            statement += "room.size, ";
        }
        if (checkBoxSalary.isSelected()) {
            statement += "salary, ";
        }
        if (checkBoxLivingIn.isSelected()) {
            statement += "city.name, ";
        }
        if (checkBoxRoomNr.isSelected()) {
            statement += "hroom.room_nr, ";
        }
        if (checkBoxFloorLevel.isSelected()) {
            statement += "hroom.room_floor, ";
        }

        statement = statement.substring(0, statement.length()-2); //strip last comma
        statement += " FROM t_human_resources hr ";

        if (checkBoxFloorLevel.isSelected() || checkBoxRoomNr.isSelected() || checkBoxRoomSize.isSelected()){
            statement += "INNER JOIN t_hr_room hroom ON hr.person_id=hroom.person_id ";
        }
        if (checkBoxRoomSize.isSelected()){
            statement += "INNER JOIN t_room room ON (hroom.room_nr=room.room_nr and hroom.room_floor=room.floor) ";
        }
        if(checkBoxSalary.isSelected()){
            statement += "INNER JOIN t_salary ON hr.person_id=t_salary.person_id ";
        }

        if(checkBoxLivingIn.isSelected()){
            statement += "INNER JOIN t_city city ON hr.zip=city.zip ";
        }

        if(checkBoxDepartment.isSelected()){
            statement += "INNER JOIN t_department dp ON hr.department_id=dp.department_id";
        }
        statement += ";";
        System.out.println(statement);
    }

    public void onCheckBoxClicked(ActionEvent actionEvent) throws SQLException {
        update();
    }
}
