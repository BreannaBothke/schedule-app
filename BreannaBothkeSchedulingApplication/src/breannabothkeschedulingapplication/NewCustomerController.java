/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package breannabothkeschedulingapplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewCustomerController {


    @FXML
    private Label newCustomerLabel, streetLabel, cityLabel, zipLabel, phoneLabel, nameLabel, errorLabel, countryLabel, aptLabel;

    @FXML
    private TextField zipTextField, cityTextField, streetTextField, nameTextField, phoneTextField, aptTextField;

    @FXML
    private Button addButton, cancelButton;
    @FXML private ChoiceBox<?> countryChoiceBox;
    
    private Connection conn;
    private ZoneId zId;
    private String user;
    private AddAppointmentController addApp;
    private UpdateAppointmentController updateApp;


    
    
    
    
    
            /* <Button Actions> */
    
    
     /**    Add Button   *************************
     **   Creates a new Customer               ***
     *********************************************/ 
    @FXML
    void addButtonAction(ActionEvent event) throws Exception {

        PreparedStatement initStmt, ps, addressStmt, cityStmt, countryStmt;
        
         try{
            conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
            System.out.println("New Customer: Connected to database : Success!");
            
            
            //troubleShooting
            System.out.println("1");
            
            
            
            String name = nameTextField.getText();
            String street = streetTextField.getText();
            String apt = aptTextField.getText();
            String zip = zipTextField.getText();
            String city = cityTextField.getText();
            String phone = phoneTextField.getText();
            
            
            initStmt = conn.prepareStatement("SELECT customerName FROM customer WHERE customerName = ?");
            initStmt.setString(1, name);
            ResultSet rsName = initStmt.executeQuery();
            
            if(rsName.next()){
                errorLabel.setText("Customer already exists.");
                System.out.println("Error: Customer already exists.");
            }
            else {
            
            initStmt = conn.prepareStatement("SELECT cityId FROM city WHERE city = ?");
            initStmt.setString(1, city);
            ResultSet rs = initStmt.executeQuery();
            String cityId = "";
            String addressId = "";
            String countryId = "";
            ObservableList data;
            
            if(addApp == null)
                data = updateApp.getChoiceBoxData();
            else
                data = addApp.getChoiceBoxData();
            
            //TroubleShooting
            System.out.println("2");
            
            //if cityId exists, grab addressId 
            if(rs.next()){
                cityId = rs.getString("cityId");
                
                
                //TroubleShooting
                System.out.println("3 cityId exists.");
                
                initStmt = conn.prepareStatement("SELECT addressId FROM address WHERE address = ? AND postalCode = ?");
                initStmt.setString(1, street);
                initStmt.setString(2, zip);
                ResultSet rs2 = initStmt.executeQuery();
                
                //if addressId exists, create new customer
                if(rs2.next()){
                    
                    addressId = rs2.getString("addressId");
                    ps = conn.prepareStatement("INSERT INTO customer (customerName, addressId,"
                        + " active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES  (?, ?,"
                        + " 1, CURDATE(), ?, CURDATE(), ?)");
                    ps.setString(1, name);
                    ps.setString(2, addressId);
                    ps.setString(3, user);
                    ps.setString(4, user);
                    
                    ps.executeUpdate();
                    
                    
                    
                    //TroubleShooting
                    System.out.println("11 Customer Created.");
                }
                //else if addressId doesn't exist, create address and then create new customer
                else{
                    
                    //create address
                    addressStmt = conn.prepareStatement("INSERT INTO address (address, address2,"
                        + " cityId, postalCode, phone, createDate,"
                        + " createdBy, lastUpdateBy) VALUES (?, ?,"
                        + " ?, ?, ?, CURDATE(), ?, ?)");
                    
                    addressStmt.setString(1, street);
                    addressStmt.setString(2, apt);
                    addressStmt.setString(3, cityId);
                    addressStmt.setString(4, zip);
                    addressStmt.setString(5, phone);
                    addressStmt.setString(6, user);
                    addressStmt.setString(7, user);
                    
                    addressStmt.executeUpdate();
                    
                    
                    
                    //TroubleShooting
                    System.out.println("10 address created.");
                    
                    //select addressId
                    ps = conn.prepareStatement("SELECT addressId FROM address WHERE address = ? AND postalCode = ?");
                    ps.setString(1, street);
                    ps.setString(2, zip);
                    ResultSet rs3 = ps.executeQuery();
                    
                    if(rs3.next()){
                        
                        addressId = rs3.getString("addressId");                        
                        
                    }
                    
                    //create new customer
                    ps = conn.prepareStatement("INSERT INTO customer (customerName, addressId,"
                        + " active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES  (?, ?,"
                        + " 1, CURDATE(), ?, CURDATE(), ?)");
                    ps.setString(1, name);
                    ps.setString(2, addressId);
                    ps.setString(3, user);
                    ps.setString(4, user);
                    
                    ps.executeUpdate();
                    
                    //add all customers to observableList data
                    
                    ps = conn.prepareStatement("SELECT customerName FROM customer WHERE customerName = ?");
                    ps.setString(1, name);
                    ResultSet rsCustName = ps.executeQuery();
                    
                    while(rsCustName.next()){
                        data.add(rsCustName.getString("customerName"));
                    }
                }
                
                   
               //else if cityId doesn't exist, then create city 
            } else{
                
                
                    //TroubleShooting
                    System.out.println("4 cityId doesn't exist.");
                    
                    //grab countryId
                    countryStmt = conn.prepareStatement("SELECT countryId FROM country WHERE country = ?");
                    countryStmt.setString(1, countryChoiceBox.getSelectionModel().getSelectedItem().toString());
                    ResultSet rsCountry = countryStmt.executeQuery();
                    
                    if(rsCountry.next()){
                    
                        countryId = rsCountry.getString("countryId");

                    }
                    
                    //create city
                    cityStmt = conn.prepareStatement("INSERT INTO city (city, countryId, createDate, createdBy, lastUpdateBy) "
                        + "VALUES (?, ?, CURDATE(), ?, ?)");
                    cityStmt.setString(1, city);
                    cityStmt.setString(2, countryId);
                    cityStmt.setString(3, user);
                    cityStmt.setString(4, user);
                    
                    cityStmt.executeUpdate();
                    
                    
                    //TroubleShooting
                    System.out.println("7 City created.");
                    
                    //select cityId
                    initStmt = conn.prepareStatement("SELECT cityId FROM city WHERE city = ?");
                    initStmt.setString(1, city);
                    ResultSet rs4 = initStmt.executeQuery();
                    
                    if(rs4.next()){
                        
                      cityId = rs4.getString("cityId");
                      
                    }
                    
                    
                    //create address
                    addressStmt = conn.prepareStatement("INSERT INTO address (address, address2,"
                        + " cityId, postalCode, phone, createDate,"
                        + " createdBy, lastUpdateBy) VALUES (?, ?,"
                        + " ?, ?, ?, CURDATE(), ?, ?)");
                    
                    addressStmt.setString(1, street);
                    addressStmt.setString(2, apt);
                    addressStmt.setString(3, cityId);
                    addressStmt.setString(4, zip);
                    addressStmt.setString(5, phone);
                    addressStmt.setString(6, user);
                    addressStmt.setString(7, user);
                    
                    addressStmt.executeUpdate();
                    
                    
                    //TroubleShooting
                    System.out.println("8 Address created.");
                    
                    //select addressId
                    ps = conn.prepareStatement("SELECT addressId FROM address WHERE address = ? AND postalCode = ?");
                    ps.setString(1, street);
                    ps.setString(2, zip);
                    ResultSet rs5 = ps.executeQuery();
                    
                    if(rs5.next()){
                        addressId = rs5.getString("addressId");
                    }
                    
                    //create new customer
                    
                     ps = conn.prepareStatement("INSERT INTO customer (customerName, addressId,"
                        + " active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES  (?, ?,"
                        + " 1, CURDATE(), ?, CURDATE(), ?)");
                    ps.setString(1, name);
                    ps.setString(2, addressId);
                    ps.setString(3, user);
                    ps.setString(4, user);
                    
                    ps.executeUpdate();
                
                    
                    //TroubleShooting
                    System.out.println("9 Customer created.");
                
                
                    //add all customers to observableList data
                    
                    ps = conn.prepareStatement("SELECT customerName FROM customer WHERE customerName = ?");
                    ps.setString(1, name);
                    ResultSet rsCustName = ps.executeQuery();
                    
                    while(rsCustName.next()){
                        data.add(rsCustName.getString("customerName"));
                    }
                
            }
           
           
             //Add Customer window is closed
                    Stage stage;
                    Parent root, root1;
            
                    //gets reference to the button's stage
                    stage=(Stage)addButton.getScene().getWindow();
            
                    //load up Other FXML Document
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("newCustomer.fxml"));
                    root1 = loader.load();
                    
                    //FXMLLoader loader2 = new FXMLLoader(getClass().getResource("newAppointment.fxml"));
                    //root = loader2.load();
                    //AddAppointmentController addApp = loader2.getController();
                    //addApp.setCustomerChoiceBox(data);
                    
                    Scene scene = new Scene(root1);
                    stage.setScene(scene);
                    stage.hide();
            
            
            
            
            
            }
            
            
            
            
            
            
            
        } catch (SQLException e){
            System.out.println("New Customer: Error. Something went wrong.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
        }
         
        
        
        
    }
    
    
    
    
    
     /**    Cancel Button   *********************************************************
     **   Cancels Add Appointment Screen and returns to the Appointments screen   ***
     ********************************************************************************/ 
    @FXML
    void cancelButtonAction(ActionEvent event) throws Exception {
        
        
        
         //Alert Box
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cancellation Confirmation Dialog");
            alert.setHeaderText("Cancel");
            alert.setContentText("Are you sure you want to Cancel?");

            Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                // ... user chose OK
                    
                    Stage stage;
                    Parent root1;

                    //gets reference to the button's stage
                    stage=(Stage)cancelButton.getScene().getWindow();

                    //load up Other FXML Document
                    root1 = FXMLLoader.load(getClass().getResource("newCustomer.fxml"));
                    Scene scene = new Scene(root1);
                    stage.setScene(scene);
                    stage.hide();
                    
                } else {
                // ... user chose CANCEL or closed the dialog

                       }
            
    }

   
    
    
    
    
     /* <Setter and Getter Methods> */
    
    
    
     /** Setter Method for Add Appointment Controller addApp    ****
     *                                                          ****
     * @param a                                                 ****
     ***************************************************************/
    void setAddAppointmentController(AddAppointmentController a){
        addApp = a;
    }
    
    
    
    
    /** Setter Method for Update Appointment Controller updateApp    ****
     *                                                               ****
     * @param u                                                      ****
     ********************************************************************/
    void setUpdateAppointmentController(UpdateAppointmentController u){
        updateApp = u;
    }
    
    
    
    /** Setter Method for variable user  ****
     *                                   ****
     * @param input                      ****
     ****************************************/
    void setUser(String input){
        user = input;
    }
    
    
    
    
    /** Setter Method for variable ZoneId z    ****
     *                                         ****
     * @param z                                ****
     **********************************************/
    void setZoneId(ZoneId z){
        zId = z;
    }
    
    
    
    
    /** Setter Method for Country Choice Box    ****
     *                                          ****
     * @param data                              ****
     ***********************************************/
    void setCountryChoiceBox(ObservableList data){
        countryChoiceBox.setItems(data);
    }
    
    
    
    
}
