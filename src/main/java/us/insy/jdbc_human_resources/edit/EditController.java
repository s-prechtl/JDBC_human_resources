package us.insy.jdbc_human_resources.edit;
/*-----------------------------------------------------------------------------
 *              Hoehere Technische Bundeslehranstalt STEYR
 *           Fachrichtung Elektronik und Technische Informatik
 *----------------------------------------------------------------------------*/

import javafx.collections.ObservableList;
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

    public void initialize() throws SQLException{
        ResultSet res = db.executeStatement("SELECT max(room_nr) FROM t_hr_room");
        res.next();
        spinnerRoomNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, res.getInt("max")));
        res = db.executeStatement("SELECT person_id FROM t_human_resources ORDER BY person_id");
        res.next();
        persID = res.getInt("person_id");

        updateSpinnerPersonID();

        updateAll();
    }

    private void update(String what) throws SQLException{
        updateSQLStatement(what);
        result = db.executeStatement(statement);
        result.next();
    }

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
        textFieldSalary.setText(result.getString("salary"));
        update("city_name");
        labelCityName.setText(result.getString("city_name"));
        update("zip");
        labelZIP.setText(result.getString("zip"));
        update("department_name");
        labelDepartment.setText(result.getString("department_name"));
        update("room_nr");
        spinnerRoomNumber.getValueFactory().setValue(result.getInt("room_nr"));
        updateRadioButtonsFloor();
        spinnerPersonID.getValueFactory().setValue(result.getInt("person_id"));
    }

    private void updateSpinnerPersonID() throws SQLException{
        ResultSet max = db.executeStatement("SELECT max(person_id) FROM t_human_resources");
        max.next();
        ResultSet minID = db.executeStatement("SELECT min(person_id) FROM t_human_resources");
        minID.next();
        spinnerPersonID.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minID.getInt("min"), max.getInt("max")));
        persID = minID.getInt("min");
    }

    private void updateRadioButtonsFloor() throws SQLException{
        ResultSet tempResult = db.executeStatement("SELECT DISTINCT floor FROM t_room ORDER BY floor");
        update("room_floor");
        while(tempResult.next()){
            RadioButton radioButton = new RadioButton(tempResult.getString("floor"));
            radioButtonsFloor.add(radioButton);
            toggleGroupFloor.getToggles().add(radioButton);
            if(radioButton.getText().equals(result.getString("room_floor"))){
                toggleGroupFloor.selectToggle(radioButton);
            }
        }
        hBoxRadioButtons.getChildren().addAll(radioButtonsFloor);
    }

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

    public void onButtonSaveClicked() throws SQLException{
        float salary = Integer.parseInt(textFieldSalary.getText());
        int room_nr = spinnerRoomNumber.getValueFactory().getValue();
        int person_id = result.getInt("person_id");
        String room_floor = ((RadioButton) toggleGroupFloor.getSelectedToggle()).getText();

        db.executeStatementNoError("UPDATE t_salary SET salary=" + salary + " WHERE person_id=" + person_id);
        db.executeStatementNoError("UPDATE t_hr_room SET room_nr=" + room_nr + ", room_floor='" + room_floor + "' WHERE person_id=" + person_id);
    }

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

    private ArrayList<Integer> getAllPersonIDsInDatabase() throws SQLException{
        ResultSet resultSet = db.executeStatement("SELECT person_id FROM t_human_resources");
        ArrayList<Integer> person_ids = new ArrayList<>();
        while(resultSet.next()){
            person_ids.add(resultSet.getInt("person_id"));
        }

        return person_ids;
    }
}
