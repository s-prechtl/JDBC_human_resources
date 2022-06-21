package us.insy.jdbc_human_resources.edit;
/*-----------------------------------------------------------------------------
 *              Hoehere Technische Bundeslehranstalt STEYR
 *           Fachrichtung Elektronik und Technische Informatik
 *----------------------------------------------------------------------------*/

import javafx.scene.control.*;
import us.insy.jdbc_human_resources.DatabaseConnector;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    private final DatabaseConnector db = DatabaseConnector.getInstance();
    private String statement;
    private ResultSet result;

    public void initialize() throws SQLException{
        spinnerRoomNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100));
        updateAll();
    }

    private void update(String what) throws SQLException{
        updateSQLStatement(what);
        result = db.executeStatement(statement);
        while(result.next() && !result.isLast()) ;
        spinnerRoomNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, result.getRow()));
        result = db.executeStatement(statement);
        result.next();
    }

    private void update() throws SQLException{
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
        //TODO: room floor
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
        if(what.equals("room_nr")){
            statement += "INNER JOIN t_hr_room hroom ON hr.person_id=hroom.person_id ";
        }

        statement += "ORDER BY hr.person_id;";
    }

    public void onButtonSaveClicked() throws SQLException{
        float salary = Integer.parseInt(textFieldSalary.getText());
        int room_nr = spinnerRoomNumber.getValueFactory().getValue();
        int person_id = result.getInt("person_id");
        //TODO: ROOM FLOOR

        statement = "UPDATE t_salary SET salary=" + salary + " WHERE person_id=" + person_id;
        db.executeStatementNoError(statement);
        statement = "UPDATE t_hr_room SET room_nr=" + room_nr + " WHERE person_id=" + person_id;
        db.executeStatementNoError(statement);
    }

    public void onButtonDeleteClicked(){
        statement = "DELETE FROM t_hr_room WHERE person_id=";
    }
}
