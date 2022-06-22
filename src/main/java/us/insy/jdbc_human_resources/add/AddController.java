package us.insy.jdbc_human_resources.add;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import us.insy.jdbc_human_resources.DatabaseConnector;
import us.insy.jdbc_human_resources.MainApplication;

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
    public Button buttonAdd;
    public Button buttonReload;
    private ToggleGroup toggleGroupGender;
    private ToggleGroup toggleGroupFloor;
    private DatabaseConnector dbc;

    public void initialize() {
        dbc = DatabaseConnector.getInstance();
        buildControls();
    }

    /**
     * Baut alle dynamisch abgerufenen Eingabefelder auf
     */
    private void buildControls() {
        buildGenderControl();
        buildRoomControl();

        buildComboBox("t_city", "name", comboBoxLivingIn);
        buildComboBox("t_department", "name", comboBoxDepartment);
    }

    /**
     * Baut dynamisch die Eingabefelder für die Raumauswahl auf
     */
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

    /**
     * Baut RadioButtons aus den Daten der angegeben Relation auf
     *
     * @param tableName Name der Relation
     * @param colIndex  Name der Spalte, wessen Werte angezeigt werden sollen
     * @param comboBox  ComboBox, welche aufgebaut werden soll
     */
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

    /**
     * Baut RadioButtons aus den Daten der angegeben Relation auf
     *
     * @param tableName Name der Relation
     * @param colIndex  Name der Spalte, wessen Werte angezeigt werden sollen
     * @param parent    HBox, dem die Buttons hinzugefügt werden
     * @return ToggleGroup der RadioButtons
     */
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

    /**
     * baut dynamisch geladene ComboBocen und RadioButtons neu auf
     */
    public void reload() {
        buildControls();
    }

    /**
     * wird bei Klick des "Add"-Buttons aufgerufen
     * <p>
     * Prüft erst ob alle Pflichtfelder ordnungsgemäß ausgefüllt sind
     * dann wird das SQL-Statement aufgebaut und ausgeführt sowie ggf. addSalary() und addRoom() aufgerufen
     */
    public void add() {
        // Eingabefelder werden geprüft, nur Email, Gehalt und Raumnummer/Stockwerk müssen nicht angegeben sein
        String firstName = textFieldFirstName.getText();
        if (firstName.equals("")) {
            MainApplication.errorBox("First name not specified");
            return;
        }

        String lastName = textFieldLastName.getText();
        if (lastName.equals("")) {
            MainApplication.errorBox("Last name not specified");
            return;
        }

        String email = textFieldEmail.getText();
        boolean emailProvided = !email.equals("");

        String gender;
        try {
            gender = ((RadioButton) toggleGroupGender.getSelectedToggle()).getText();
        } catch (NullPointerException ignored) {
            MainApplication.errorBox("Gender not specified");
            return;
        }

        String dateOfBirth;
        try {
            dateOfBirth = datePickerBirthDate.getValue().toString();
        } catch (NullPointerException ignored) {
            MainApplication.errorBox("Date of birth not specified");
            return;
        }

        double salary = 0;
        boolean salaryProvided = !textFieldSalary.getText().equals("");
        if (salaryProvided) {
            try {
                salary = Double.parseDouble(textFieldSalary.getText());
            } catch (NumberFormatException e) {
                MainApplication.errorBox("Salary is NaN");
                return;
            }
        }

        int zip = -1;
        String cityName = comboBoxLivingIn.getValue();
        if (cityName == null) {
            MainApplication.errorBox("City not specified");
            return;
        }
        try {
            ResultSet resultSet = dbc.executeStatement("SELECT zip FROM t_city WHERE name = '" + cityName + "';");
            resultSet.next();
            zip = resultSet.getInt("zip");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (zip == -1) {
            MainApplication.errorBox("Something about the city went terribly wrong");
            return;
        }

        int departmentId = -1;
        String department = comboBoxDepartment.getValue();
        if (department == null) {
            MainApplication.errorBox("Deparment not specified");
            return;
        }
        try {
            ResultSet resultSet = dbc.executeStatement("SELECT department_id FROM t_department WHERE name = '" + department + "';");
            resultSet.next();
            departmentId = resultSet.getInt("department_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (departmentId == -1) {
            MainApplication.errorBox("Something about the department went terribly wrong");
            return;
        }

        int roomNr = 0;
        String floor = null;
        boolean roomProvided = false;
        try {
            floor = ((RadioButton) toggleGroupFloor.getSelectedToggle()).getText();
            roomNr = spinnerRoomNumber.getValue();
            roomProvided = true;
        } catch (NullPointerException ignored) {
        }

        // Ende Prüfen der Eingabefelder

        ArrayList<String> values = new ArrayList<>();
        StringBuilder statement = new StringBuilder();
        int id = 0;

        statement.append("INSERT INTO t_human_resources (first_name, last_name, ");
        statement.append((emailProvided ? "email, " : ""));
        statement.append("gender, date_of_birth, zip, department_id) VALUES (");


        // String concatination leichter im Array
        values.add(firstName);
        values.add(lastName);
        if (emailProvided) {
            values.add(textFieldEmail.getText());
        }
        values.add(gender);
        values.add(dateOfBirth);
        values.add(String.valueOf(zip));
        values.add(String.valueOf(departmentId));

        for (String value : values) {
            statement.append("'").append(value).append("', ");
        }

        statement.delete(statement.length() - 2, statement.length()).append(") RETURNING person_id;");

        // Query
        try {
            System.out.println(statement);
            ResultSet resultSet = dbc.executeStatement(statement.toString());
            resultSet.next();
            id = resultSet.getInt("person_id");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (salaryProvided) {
            addSalary(id, salary);
        }

        if (roomProvided) {
            addRoom(id, roomNr, floor);
        }

        clearAll();
    }

    /**
     * Fügt einen "Gehaltseintrag" in die Relation t_salary hinzu
     *
     * @param id     person_id des Mitarbeiters
     * @param salary Gehalt
     */
    private void addSalary(int id, double salary) {
        String statement = "INSERT INTO t_salary (person_id, salary) VALUES (" + id + ", " + salary + ");";
        System.out.println(statement);
        dbc.executeStatementNoError(statement);
    }

    /**
     * fügt eine Raum/Mitarbeiter-Zuweisung in die Relation t_hr_room ein
     *
     * @param id     person_id
     * @param roomNr romm_nr
     * @param floor  floor
     */
    private void addRoom(int id, int roomNr, String floor) {
        String statement = "INSERT INTO t_hr_room (person_id, room_floor, room_nr) VALUES (" + id + ", '" + floor + "', " + roomNr + ");";
        System.out.println(statement);
        dbc.executeStatementNoError(statement);
    }

    /**
     * Setzt alle Eingabefelder zurück
     */
    private void clearAll() {
        textFieldFirstName.clear();
        textFieldLastName.clear();
        textFieldEmail.clear();
        datePickerBirthDate.setValue(null);
        toggleGroupGender.selectToggle(null);
        textFieldSalary.clear();
        comboBoxLivingIn.setValue(null);
        comboBoxDepartment.setValue(null);
        spinnerRoomNumber.getValueFactory().setValue(0);
        toggleGroupFloor.selectToggle(null);
    }

}
