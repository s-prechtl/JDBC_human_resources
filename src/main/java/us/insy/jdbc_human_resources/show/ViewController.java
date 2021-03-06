package us.insy.jdbc_human_resources.show;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import us.insy.jdbc_human_resources.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
    public HBox hBoxID;
    public HBox hBoxFirstname;
    public HBox hBoxLastname;
    public HBox hBoxGender;
    public HBox hBoxEmail;
    public HBox hBoxBirthDate;
    public HBox hBoxSalary;
    public HBox hBoxLiving;
    public HBox hBoxCity;
    public HBox hBoxZip;
    public HBox hBoxDepartment;
    public HBox hBoxDepName;
    public HBox hBoxLevel;
    public HBox hBoxRoomNr;
    public HBox hBoxRoomSize;
    public Spinner<String> spinnerID;
    public ComboBox<String> comboBoxDepartment;

    private String statement;
    private ResultSet result;


    private ObservableList<String> ids;

    public void initialize() throws SQLException {
        updateComboBoxList();
        comboBoxDepartment.setValue("All");
        update();
    }

    /**
     * used to update both UI as well as the SQL result
     *
     * @throws SQLException
     */
    private void update() throws SQLException {
        updateSQLStatement();
        if (!statement.contains("SELEC FROM")) { // because of strip string
            buildSpinner();
            result = db.executeStatement(statement);
            result.next();
        }
        updateUI();
    }

    /**
     * builds the spinner with the correct IDs for the hr people for jump to
     *
     * @throws SQLException
     */
    private void buildSpinner() throws SQLException {
        ids = FXCollections.observableArrayList();
        String idStatement;

        if (!comboBoxDepartment.getValue().equals("All")) {
            idStatement = "SELECT person_id FROM t_human_resources hr INNER JOIN t_department dp ON dp.department_id=hr.department_id WHERE dp.name='" + comboBoxDepartment.getValue() + "' ";
        } else {
            idStatement = "SELECT person_id FROM t_human_resources ";
        }

        idStatement += "ORDER BY person_id;";
        result = db.executeStatement(idStatement);
        while (result.next()) {
            ids.add(String.valueOf(result.getInt("person_id")));
        }
        spinnerID.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(ids));
    }

    /**
     * Updates Items in the Combobox for the Departments to select
     *
     * @throws SQLException
     */
    private void updateComboBoxList() throws SQLException {
        ResultSet departmentList = db.executeStatement("SELECT name FROM t_department;");
        ArrayList<String> dpList = new ArrayList<>();
        dpList.add("All");
        while (departmentList.next()) {
            dpList.add(departmentList.getString("name"));
        }
        comboBoxDepartment.setItems(FXCollections.observableList(dpList));
    }

    /**
     * updates the UI
     *
     * @throws SQLException
     */
    private void updateUI() throws SQLException {
        updateLabel(hBoxID, labelID, checkBoxPersID, "person_id");
        updateLabel(hBoxFirstname, labelFirstName, checkBoxFirstName, "first_name");
        updateLabel(hBoxLastname, labelLastName, checkBoxLastname, "last_name");
        updateLabel(hBoxGender, labelGender, checkBoxGender, "gender");
        updateLabel(hBoxBirthDate, labelBirthDate, checkBoxBirthDate, "date_of_birth");
        updateLabel(hBoxEmail, labelEmail, checkBoxEmail, "email");
        updateLabel(hBoxSalary, labelSalary, checkBoxSalary, "salary");
        updateLabel(hBoxCity, labelCity, checkBoxLivingIn, "cname");
        updateLabel(hBoxZip, labelZipCode, checkBoxZipCode, "zip");
        updateLabel(hBoxDepName, labelDepartmentName, checkBoxDepartment, "dpName");
        updateLabel(hBoxRoomNr, labelRoomNumber, checkBoxRoomNr, "room_nr");
        updateLabel(hBoxLevel, labelLevel, checkBoxFloorLevel, "room_floor");
        updateLabel(hBoxRoomSize, labelRoomSize, checkBoxRoomSize, "size");

        hBoxEmail.setVisible(result.getString("email") != null);
        hBoxDepartment.setVisible(checkBoxRoomSize.isSelected() || checkBoxRoomNr.isSelected() || checkBoxFloorLevel.isSelected() || checkBoxDepartment.isSelected());
        spinnerID.getValueFactory().setValue(labelID.getText());
        hBoxLiving.setVisible(checkBoxZipCode.isSelected() || checkBoxLivingIn.isSelected());
    }

    /**
     * Updates the value in the given label and hides the Hbox if the checkbox isnt checked
     *
     * @param hBox        hbox for the naming label and the value label
     * @param label       value label
     * @param shown       status if the values should be genereated or not
     * @param columnLabel what the column is called in the sql result
     * @throws SQLException
     */
    private void updateLabel(HBox hBox, Label label, CheckBox shown, String columnLabel) throws SQLException {
        hBox.setVisible(shown.isSelected());
        if (shown.isSelected()) {
            label.setText(result.getString(columnLabel));
        }
    }

    /**
     * updates the SQL statement according to all checkbox values
     */
    private void updateSQLStatement() {
        statement = "SELECT ";
        statement += "hr.person_id, ";
        if (checkBoxFirstName.isSelected()) {
            statement += "first_name, ";
        }
        if (checkBoxLastname.isSelected()) {
            statement += "last_name, ";
        }
        if (checkBoxEmail.isSelected()) {
            statement += "email, ";
        }
        if (checkBoxGender.isSelected()) {
            statement += "gender, ";
        }
        if (checkBoxBirthDate.isSelected()) {
            statement += "date_of_birth, ";
        }
        if (checkBoxZipCode.isSelected()) {
            statement += "hr.zip, ";
        }
        if (checkBoxDepartment.isSelected()) {
            statement += "dp.name AS dpName, ";
        }
        if (checkBoxRoomSize.isSelected()) {
            statement += "room.size, ";
        }
        if (checkBoxSalary.isSelected()) {
            statement += "salary, ";
        }
        if (checkBoxLivingIn.isSelected()) {
            statement += "city.name AS cname, ";
        }
        if (checkBoxRoomNr.isSelected()) {
            statement += "hroom.room_nr, ";
        }
        if (checkBoxFloorLevel.isSelected()) {
            statement += "hroom.room_floor, ";
        }

        statement = statement.substring(0, statement.length() - 2); //strip last comma
        statement += " FROM t_human_resources hr ";

        if (checkBoxFloorLevel.isSelected() || checkBoxRoomNr.isSelected() || checkBoxRoomSize.isSelected()) {
            statement += "INNER JOIN t_hr_room hroom ON hr.person_id=hroom.person_id ";
        }
        if (checkBoxRoomSize.isSelected()) {
            statement += "INNER JOIN t_room room ON (hroom.room_nr=room.room_nr and hroom.room_floor=room.floor) ";
        }
        if (checkBoxSalary.isSelected()) {
            statement += "INNER JOIN t_salary ON hr.person_id=t_salary.person_id ";
        }

        if (checkBoxLivingIn.isSelected()) {
            statement += "INNER JOIN t_city city ON hr.zip=city.zip ";
        }

        if (checkBoxDepartment.isSelected() || comboBoxDepartment.getValue() != null) {
            statement += "INNER JOIN t_department dp ON hr.department_id=dp.department_id ";
        }

        if (comboBoxDepartment.getValue() != null && !comboBoxDepartment.getValue().equals("All")) {
            statement += "WHERE dp.name='" + comboBoxDepartment.getValue() + "' ";
        }

        statement += "ORDER BY hr.person_id;";
    }

    /**
     * calls update whenever a checkbox is clicked
     *
     * @throws SQLException
     */
    public void onCheckBoxClicked() throws SQLException {
        update();
    }

    /**
     * goes to the next person of hr
     *
     * @throws SQLException
     */
    public void onButtonNextClicked() throws SQLException {
        int id;
        if (result.isLast()) {
            update();
            id = 1;
        } else {
            id = Integer.parseInt(ids.get(ids.indexOf(labelID.getText()) + 1));
        }
        jumpTo(id);
    }

    /**
     * goes to the previous person of hr
     *
     * @throws SQLException
     */
    public void onPreviousClicked() throws SQLException {
        int id = Integer.parseInt(labelID.getText()) - 1;
        if (id < Integer.parseInt(ids.get(0))) {
            id = Integer.parseInt(ids.get(ids.size() - 1));
        }
        jumpTo(id);
    }

    /**
     * jumps to a desired person of hr
     *
     * @param id tells which person needs to be jumped to
     * @throws SQLException
     */
    private void jumpTo(int id) throws SQLException {
        update();
        while (result.next()) {
            if (result.getInt("person_id") == id) {
                updateUI();
                break;
            }
        }
    }

    /**
     * gets the spinner's value and jumps to the person
     *
     * @throws SQLException
     */
    public void onJumpToClicked() throws SQLException {
        if (spinnerID.getValue() != null) {
            int id = Integer.parseInt(spinnerID.getValue());
            jumpTo(id);
        }
    }

    /**
     * updates the spinner and calls update for sql and ui update
     *
     * @throws SQLException
     */
    public void onComboBoxDepartmentChanged() throws SQLException {
        buildSpinner();
        update();
    }
}
