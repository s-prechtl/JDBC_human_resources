package us.insy.jdbc_human_resources.add;

import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import us.insy.jdbc_human_resources.DatabaseConnector;
import us.insy.jdbc_human_resources.HelloApplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AddController {

    @FXML
    public TextField textFieldFirstName;
    @FXML
    public TextField textFieldLastName;
    @FXML
    public TextField textFieldEmail;
    @FXML
    public DatePicker datePickerBirthDate;
    @FXML
    public HBox hBoxGender;
    private ToggleGroup toggleGroupGender;
    @FXML
    public TextField textFieldSalary;
    @FXML
    public ComboBox<String> comboBoxLivingIn;
    @FXML
    public ComboBox<String> comboBoxDepartment;
    @FXML
    public Spinner<Integer> spinnerRoomNumber;
    @FXML
    public HBox hBoxFloor;
    private ToggleGroup toggleGroupFloor;

    public Button buttonAdd;
    public Button buttonReload;

    private DatabaseConnector dbc;

    public void initialize() {
        dbc = DatabaseConnector.getInstance();
        buildControls();
    }

    private void buildControls() {
        buildGenderControl();
        buildRoomControl();

        buildComboBox("t_city", "name", comboBoxLivingIn);
        buildComboBox("t_department", "name", comboBoxDepartment);
    }

    private void buildRoomControl() {
        toggleGroupFloor = buildRadioButtons("t_room", "floor", hBoxFloor);

        int minRoomNr = 0, maxRoomNr = 0;

        try {
            String colLabel = "room_nr";
            ResultSet resultSet = dbc.executeStatement("SELECT MIN(room_nr) AS room_nr FROM t_room;");
            resultSet.next();
            minRoomNr = resultSet.getInt(colLabel);

            resultSet = dbc.executeStatement("SELECT MAX(room_nr) AS room_nr FROM t_room;");
            resultSet.next();
            maxRoomNr = resultSet.getInt(colLabel);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        spinnerRoomNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minRoomNr, maxRoomNr));
    }

    private void buildGenderControl() {
        toggleGroupGender = buildRadioButtons("t_human_resources", "gender", hBoxGender);
    }

    void buildComboBox(String tableName, String colIndex, ComboBox<String> comboBox) {
        ArrayList<String> list = new ArrayList<>();

        try {
            ResultSet resultSet = dbc.executeStatement("SELECT " + colIndex + " FROM " + tableName + ";");
            while (resultSet.next()) {
                list.add(resultSet.getString(colIndex));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        comboBox.getItems().removeAll();
        comboBox.getItems().addAll(list);
    }

    ToggleGroup buildRadioButtons(String tableName, String colIndex, HBox parent) {
        ToggleGroup group = new ToggleGroup();
        parent.getChildren().removeAll();
        try {
            ResultSet resultSet = dbc.executeStatement("SELECT DISTINCT " + colIndex + " FROM " + tableName + " ORDER BY " + colIndex + ";");
            while (resultSet.next()) {
                RadioButton rb = new RadioButton(resultSet.getString(colIndex));
                rb.setToggleGroup(group);
                parent.getChildren().add(rb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return group;
    }

    public void reload() {
        buildControls();
    }

    public void add() {
        ArrayList<String> values = new ArrayList<>();
        StringBuilder statement = new StringBuilder();

        boolean emailProvided = !textFieldEmail.getText().equals("");

        statement.append("INSERT INTO t_human_resources (");
        statement.append("first_name, ");
        statement.append("last_name, ");
        statement.append("gender, ");
        statement.append("date_of_birth, ");
        if (emailProvided) {
            statement.append("email, ");
        }
        statement.append("department_id");
        statement.append(") VALUES (");

        values.add(textFieldFirstName.getText());
        values.add(textFieldLastName.getText());
        values.add(((RadioButton) toggleGroupGender.getSelectedToggle()).getText());
        values.add(datePickerBirthDate.getValue().toString());
        if (emailProvided) {
            values.add(textFieldEmail.getText());
        }

        for (String value : values) {
            statement.append("'").append(value).append("', ");
        }
        try {
            ResultSet resultSet = dbc.executeStatement("SELECT department_id FROM t_department WHERE name = '" + comboBoxDepartment.getValue() + "';");
            resultSet.next();
            statement.append(resultSet.getString("department_id")).append(");");
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            System.out.println(statement);
            //dbc.executeStatement("");
            clearAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        addSalary();


    }

    private void clearAll() {
        textFieldFirstName.clear();
        textFieldLastName.clear();
        textFieldEmail.clear();
        datePickerBirthDate.setValue(null);
        toggleGroupGender.selectToggle(null);
        textFieldSalary.clear();
        comboBoxLivingIn.setValue(null);
        comboBoxDepartment.setValue(null);
        spinnerRoomNumber.getValueFactory().setValue(1);
        toggleGroupFloor.selectToggle(null);
    }

    void addSalary() {
        String salaryStr = textFieldSalary.getText();
        double salaryDbl = 0;
        int id = 0;
        boolean addSalary = true;

        if (!salaryStr.equals("")) {
            try {
                salaryDbl = Double.parseDouble(salaryStr);
            } catch (NumberFormatException e) {
                addSalary = false;
                e.printStackTrace();
            }
            try {
                ResultSet resultSet = dbc.executeStatement("SELECT person_id FROM t_human_resources WHERE first_name = '" + textFieldFirstName.getText() + "' AND last_name = '" + textFieldLastName + "' ORDER BY person_id DESC;");
                resultSet.next();
                //id = resultSet.getInt("person_id");
                id = 23874;
            } catch (SQLException e) {
                addSalary = false;
                e.printStackTrace();
            }

            if (addSalary) {
                System.out.println("INSERT INTO t_salary (person_id, salary) VALUES (" + id + ", " + salaryDbl + ");");
            } else {
                System.out.println("no salary?!");
            }
        }
    }

}
