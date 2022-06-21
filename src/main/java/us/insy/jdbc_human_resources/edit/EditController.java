package us.insy.jdbc_human_resources.edit;
/*-----------------------------------------------------------------------------
 *              Hoehere Technische Bundeslehranstalt STEYR
 *           Fachrichtung Elektronik und Technische Informatik
 *----------------------------------------------------------------------------*/

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import us.insy.jdbc_human_resources.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author David Hain
 */

public class EditController{
    public Label labelFirstName;
    public Label labelLastName;
    public Label labelEMail;
    public Label labelBirthDate;
    public Label labelGender;
    public TextField textFieldSalary;
    public Label labelCityName;
    public Label labelZIP;
    public Label labelDepartment;
    public Spinner<Integer> spinnerRoomNumber;
    public HBox hBoxRadioButtons;
    private ArrayList<RadioButton> radioButtonsFloor = new ArrayList<>();
    private final ToggleGroup toggleGroupFloor = new ToggleGroup();
    public Spinner<Integer> spinnerPersonID;

    private final DatabaseConnector db = DatabaseConnector.getInstance();
    private String statement;
    private ResultSet result;
    private int persID;

    /**
     * The spinnerRoomNumber gets set to be at a maximum of the floor with the highest amount of rooms and a minimum of 1
     * The spinnerPersonID gets set to be at a maximum of the highest person_id and a minimum of one
     * Then every label/textField/spinner/radioButton gets updated to the lowest ordered person_id
     * @throws SQLException SQL Error
     */
    public void initialize() throws SQLException{
        updateSpinnerRoomNumber();
        updateSpinnerPersonID();
        updateAll();
    }

    /**
     * Updates what it says in the "what" parameter
     * @param what what should be updated
     * @throws SQLException SQL Error
     */
    private void update(String what) throws SQLException{
        updateSQLStatement(what);
        result = db.executeStatement(statement);
        result.next();
    }

    /**
     * Update all labels/textFields/spinners/radioButtons
     * @throws SQLException SQL Error
     */
    private void updateAll() throws SQLException{
        update("first_name");
        labelFirstName.setText(result.getString("first_name"));
        update("last_name");
        labelLastName.setText(result.getString("last_name"));
        update("email");
        labelEMail.setText(result.getString("email"));
        update("date_of_birth");
        labelBirthDate.setText(result.getString("date_of_birth"));
        update("gender");
        labelGender.setText(result.getString("gender"));
        update("salary");
        try{
            textFieldSalary.setText(result.getString("salary"));
        }catch(org.postgresql.util.PSQLException e){
            textFieldSalary.setText("no salary available");
        }
        update("city_name");
        labelCityName.setText(result.getString("city_name"));
        update("zip");
        labelZIP.setText(result.getString("zip"));
        update("department_name");
        labelDepartment.setText(result.getString("department_name"));
        update("room_nr");
        try{
            updateSpinnerRoomNumber();
            spinnerRoomNumber.getValueFactory().setValue(result.getInt("room_nr"));
        }catch(org.postgresql.util.PSQLException e){
            spinnerRoomNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, -1));
            spinnerRoomNumber.getValueFactory().setValue(-1);
        }
        updateRadioButtonsFloor();
        update("person_id");
        spinnerPersonID.getValueFactory().setValue(result.getInt("person_id"));
    }

    /**
     * Updates the spinnerPersonID to be at a maximum of the highest person_id and a minimum of the lowest one
     * persID parameter gets set to the lowest person_id
     * @throws SQLException SQL Error
     */
    private void updateSpinnerPersonID() throws SQLException{
        ResultSet max = db.executeStatement("SELECT max(person_id) FROM t_human_resources");
        max.next();
        ResultSet minID = db.executeStatement("SELECT min(person_id) FROM t_human_resources");
        minID.next();
        spinnerPersonID.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minID.getInt("min"), max.getInt("max")));
        persID = minID.getInt("min");
    }

    /**
     * Updates the spinnerRoomNumber to be at a maximum of the highest person_id and a minimum of one
     * @throws SQLException SQL Error
     */
    private void updateSpinnerRoomNumber() throws SQLException{
        ResultSet res = db.executeStatement("SELECT max(room_nr) FROM t_hr_room");
        res.next();
        spinnerRoomNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, res.getInt("max")));
    }

    /**
     * Creates one RadioButton for each DISTINCT floor, adds them to the radioButtonsFloor ArrayList
     * and adds them to the toggleGroupFloor ToggleGroup
     * @throws SQLException SQL Error
     */
    private void updateRadioButtonsFloor() throws SQLException{
        ResultSet tempResult = db.executeStatement("SELECT DISTINCT floor FROM t_room ORDER BY floor");
        update("room_floor");
        while(tempResult.next()){
            RadioButton radioButton = new RadioButton(tempResult.getString("floor"));
            radioButtonsFloor.add(radioButton);
            toggleGroupFloor.getToggles().add(radioButton);
            try{
                if(radioButton.getText().equals(result.getString("room_floor"))){
                    toggleGroupFloor.selectToggle(radioButton);
                }
            }catch(org.postgresql.util.PSQLException e){
                toggleGroupFloor.selectToggle(null);
            }
        }
        hBoxRadioButtons.getChildren().addAll(radioButtonsFloor);
    }

    /**
     * Updates the class variable "statement" with an appropriate SELECT statement for the text in the "what" parameter
     * @param what what should be selected
     */
    private void updateSQLStatement(String what){
        statement = "SELECT hr.person_id, ";

        if(what.equals("first_name")){
            statement += what + ", ";
        }
        if(what.equals("last_name")){
            statement += what + ", ";
        }
        if(what.equals("email")){
            statement += what + ", ";
        }
        if(what.equals("date_of_birth")){
            statement += what + ", ";
        }
        if(what.equals("gender")){
            statement += what + ", ";
        }
        if(what.equals("salary")){
            statement += what + ", ";
        }
        if(what.equals("city_name")){
            statement += "city.name AS \"city_name\", ";
        }
        if(what.equals("zip")){
            statement += "zip, ";
        }
        if(what.equals("department_name")){
            statement += "dp.name AS \"department_name\", ";
        }
        if(what.equals("room_nr")){
            statement += "room_nr, ";
        }
        if(what.equals("room_floor")){
            statement += "room_floor, ";
        }

        statement = statement.substring(0, statement.length() - 2); //strip last comma
        statement += " FROM t_human_resources hr ";

        if(what.equals("salary")){
            statement += "INNER JOIN t_salary ON hr.person_id=t_salary.person_id ";
        }
        if(what.equals("city_name")){
            statement += "INNER JOIN t_city city ON hr.zip=city.zip ";
        }
        if(what.equals("department_name")){
            statement += "INNER JOIN t_department dp ON hr.department_id=dp.department_id ";
        }
        if(what.equals("room_nr") || what.equals("room_floor")){
            statement += "INNER JOIN t_hr_room hroom ON hr.person_id=hroom.person_id ";
        }

        if(persID > 0){
            statement += "WHERE hr.person_id=" + persID + " ";
        }

        statement += "ORDER BY hr.person_id;";
    }

    /**
     * Save the salary, room_nr and room_floor of the person if the person has a salary and is assigned a room
     * @throws SQLException  SQL Error
     */
    public void onButtonSaveClicked() throws SQLException{
        if(toggleGroupFloor.getSelectedToggle() == null || spinnerRoomNumber.getValueFactory().getValue() == -1){
            return;
        }
        float salary;
        try{
            salary = Integer.parseInt(textFieldSalary.getText());
        }catch(NumberFormatException e){
            return;
        }
        int room_nr = spinnerRoomNumber.getValueFactory().getValue();
        int person_id = result.getInt("person_id");
        String room_floor = ((RadioButton) toggleGroupFloor.getSelectedToggle()).getText();

        db.executeStatementNoError("UPDATE t_salary SET salary=" + salary + " WHERE person_id=" + person_id);
        db.executeStatementNoError("UPDATE t_hr_room SET room_nr=" + room_nr + ", room_floor='" + room_floor + "' WHERE person_id=" + person_id);
    }

    /**
     * Delete the entry in the t_salary, t_hr_room and t_human_resources tables and
     * update everything to the next entry (Ordered by person_id) if existing
     * @throws SQLException  SQL Error
     */
    public void onButtonDeleteClicked() throws SQLException{
        int person_id = result.getInt("person_id");

        db.executeStatementNoError("DELETE FROM t_salary WHERE person_id=" + person_id);
        db.executeStatementNoError("DELETE FROM t_hr_room WHERE person_id=" + person_id);
        db.executeStatementNoError("DELETE FROM t_human_resources WHERE person_id=" + person_id);

        // check if person_id +1 is valid, if so update all labels etc., if not check for person_id +2 ...
        ArrayList<Integer> person_ids = getAllPersonIDsInDatabase();
        person_id += 1;
        while(!person_ids.contains(person_id)){
            person_id += 1;
        }

        hBoxRadioButtons.getChildren().removeAll(radioButtonsFloor);
        radioButtonsFloor = new ArrayList<>();
        persID = person_id;
        updateSpinnerPersonID();
        spinnerPersonID.getValueFactory().setValue(result.getInt("person_id"));
        updateAll();
    }

    /**
     * Jump to the selected person_id if existent and updateAll()
     * @throws SQLException  SQL Error
     */
    public void onButtonJumpToClicked() throws SQLException{
        // if person_id in spinner does not exist in database => do nothing
        int person_id = spinnerPersonID.getValueFactory().getValue();
        ArrayList<Integer> person_ids = getAllPersonIDsInDatabase();
        if(!person_ids.contains(person_id)){
            return;
        }

        hBoxRadioButtons.getChildren().removeAll(radioButtonsFloor);
        radioButtonsFloor = new ArrayList<>();
        persID = person_id;
        updateAll();
    }

    /**
     * Returns an ArrayList of all available person_ids
     * @return ArrayList<Integer> => all available person_ids
     * @throws SQLException  SQL Error
     */
    private ArrayList<Integer> getAllPersonIDsInDatabase() throws SQLException{
        ResultSet resultSet = db.executeStatement("SELECT person_id FROM t_human_resources");
        ArrayList<Integer> person_ids = new ArrayList<>();
        while(resultSet.next()){
            person_ids.add(resultSet.getInt("person_id"));
        }

        return person_ids;
    }
}
