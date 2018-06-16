/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package breannabothkeschedulingapplication;

import breannabothkeschedulingapplication.Models.schedule;
import breannabothkeschedulingapplication.Models.country;
import breannabothkeschedulingapplication.Models.Customer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 *
 * @author Admin
 */
public class UpdateCustomerController {
    
    
    
    
    
    @FXML
    private Label newCustomerLabel, streetLabel, cityLabel, zipLabel, phoneLabel,nameLabel, countryLabel, aptLabel, errorLabel;

    @FXML
    private TextField customerIdTextField,nameTextField, streetTextField, cityTextField, zipTextField,phoneTextField,aptTextField;

    @FXML
    private ComboBox<String> countryComboBox; 

    @FXML
    private Button cancelButton, updateButton;
    
    private ViewCustomersController viewCust;
    private int Index;
    private int customerId;
    private PreparedStatement ps;
    private Connection conn;
    private String user;
    private ObservableList<schedule> masterData = FXCollections.observableArrayList();
    
    
    
    
    
    
    
    
    
    
    
    
    
               /* <Button Actions> */
    
    
    
       
     /**    Cancel Button   *********************************************************
     **   Cancels Update Customer Screen and returns to the View Customers screen ***
     *******************************************************************************/    

    @FXML
    void cancelButtonAction(ActionEvent event) throws IOException {
        
            Stage stage;
            Parent root;
            
            //gets reference to the button's stage
            stage=(Stage)cancelButton.getScene().getWindow();
            
            //load up Other FXML Document
            root = FXMLLoader.load(getClass().getResource("updateCustomer.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.hide();
        
    }

    
    
    
    
     
    /**    Update Button    **************************************************
     **   Updates/modifies existing customer with the new data.            ***
     *************************************************************************/ 
    @FXML
    void updateButtonAction(ActionEvent event) throws IOException {

        
        Parent r = FXMLLoader.load(getClass().getResource("viewCustomers.fxml"));

        ObservableList<Customer> data = viewCust.getCustomerData();
       
            

        
         
                
        if(nameTextField.getText().isEmpty() || streetTextField.getText().isEmpty()
              
            || cityTextField.getText().isEmpty() || zipTextField.getText().isEmpty()
            || phoneTextField.getText().isEmpty() || countryComboBox.getSelectionModel().isEmpty())
        {
                //Error Label
                errorLabel.setText("Fill in the text fields please.");
        }else{
                
            
            try{
                
            
                String customerName = nameTextField.getText();
                String address = streetTextField.getText();
                String address2 = aptTextField.getText();
                String city = cityTextField.getText();
                String postal = zipTextField.getText();
                String country = countryComboBox.getValue();
                String phone = phoneTextField.getText();
                int cityId = 0;
                int addressId = 0;
                
                conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
                
                
                
                //Grab cityId
                cityId = grabCityId(city, country, cityId);
                
                
                //Grab addressId
                addressId = grabAddressId(cityId, address, address2, postal, phone, addressId);
                
                
                
                
                
                
                if(cityId != 0 && addressId != 0){
                    
                    //Update customer
                    ps = conn.prepareStatement("UPDATE customer c JOIN address a ON c.addressId = a.addressId "
                            + "JOIN city ON a.cityId = city.cityId "
                            + "SET customerName = ?, c.addressId = ?, a.cityId = ?, postalCode = ?,"
                            + " city.countryId = (SELECT countryId FROM country WHERE country = ?), phone = ? WHERE customerId = ?");
                    ps.setString(1, customerName);
                    ps.setInt(2, addressId);
                    ps.setInt(3, cityId);
                    ps.setString(4, postal);
                    ps.setString(5, country);
                    ps.setString(6, phone);
                    ps.setInt(7, customerId);

                    int rs = ps.executeUpdate();

                    if(rs != 0){

                        //Customer being modified is deleted
                        data.remove(Index);

                        //Customer being modified is re-added with modified information
                        data.add(Index, new Customer (Integer.toString(customerId), customerName,
                                address + ", " + address2 + ", " + city + ", " + country + ", " + postal, phone));
                        
                        /* int i = 0;
                        for (schedule m : masterData) {
                        i++;
                        String id = m.getID();
                        String cName = m.getCustomerName();
                        String title = m.getTitle();
                        String date = m.getDate();
                        String time = m.getTime();
                        String endTime = m.getEndTime();
                        String location = m.getLocation();
                        String description = m.getDescription();
                        if(data.get(Index).getCustomerName().equalsIgnoreCase(cName)){
                        masterData.remove(i);
                        masterData.add(i, new schedule(id, title, data.get(Index).getCustomerName(), location, description, date, time, endTime));
                        }
                        }*/
                        
                        


                        //text fields are cleared            
                        nameTextField.setText("");
                        streetTextField.setText("");
                        aptTextField.setText("");  
                        cityTextField.setText("");
                        zipTextField.setText("");
                        phoneTextField.setText("");
                        errorLabel.setText("");

                        
                       
                        
                        //Update Customer window is closed
                        Stage stage;
                        Parent root1;

                        //gets reference to the button's stage
                        stage=(Stage)updateButton.getScene().getWindow();

                        //load up Other FXML Document
                        root1 = FXMLLoader.load(getClass().getResource("updateCustomer.fxml"));
                        Scene scene = new Scene(root1);
                        stage.setScene(scene);
                        stage.hide();
                        System.out.println("Customer was Updated.");
                    }
                    else{
                        System.out.println("Customer was not updated.");
                        errorLabel.setText("Customer was not updated.");
                    }
                }else
                    System.out.println("addressId or cityId was equal to 0.");
        
            }catch(SQLException e){
                
                System.out.println("Update Customer: Something went wrong.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
                
                }
    
        }
    }
    
        
    
    
    
    
    
    
       /* <Setter and Getter Methods> */
    
        
    
    
    
     /** Setter Method for ViewCustomer Controller viewCust   ***
     *                                                       ****
     * @param v                                              ****
     ************************************************************/
    @FXML
    void setViewCustomersController (ViewCustomersController v){
        viewCust = v;
    }
    
    
    
    
     /** Setter Method for variable user                      ***
     *                                                       ****
     * @param u                                              ****
     ************************************************************/
    @FXML
    void setUser (String u){
        user = u;
    }
    
    
     /** Setter Method for variable Index                     ***
     *                                                       ****
     * @param index                                          ****
     ************************************************************/
    public void setSelectedCustomerIndex(int index){
         Index = index;
    }
    
      /** Setter Method for modifyIdTextField                  ***
     *                                                       ****
     * @param input                                          ****
     ************************************************************/
    public void setID(String input){
       customerId = Integer.parseInt(input);
    }

    
    
     /** Setter Method for nameTextField                ***
     *                                                       ****
     * @param input                                          ****
     ************************************************************/
    public void setNameTextField(String input){
       nameTextField.setText(input);
    }

    
    
     /** Setter Method for streetTextField                 ***
     *                                                       ****
     * @param input                                          ****
     ************************************************************/
    public void setStreetTextField(String input){
       streetTextField.setText(input);
    }

    
     /** Setter Method for aptTextField                 ***
     *                                                       ****
     * @param input                                          ****
     ************************************************************/
    public void setAptTextField(String input){
       aptTextField.setText(input);
    }

    
     /** Setter Method for cityTextField                       ***
     *                                                       ****
     * @param input                                          ****
     ************************************************************/
    public void setCityTextField(String input){
       cityTextField.setText(input);
    }

    
     /** Setter Method for zipTextField                       ***
     *                                                       ****
     * @param input                                          ****
     ************************************************************/
    public void setZipTextField(String input){
       zipTextField.setText(input);
    }
       
    
    
     /** Setter Method for countryComboBox                   ****
     * @param data                                           ****
     * @param input                                          ****
     ************************************************************/
    public void setCountryComboBox(ObservableList<country> data, String input){
        country country = null;
        for(int i = 0; i < data.size(); i++){
            
            countryComboBox.getItems().add(data.get(i).getName());

            if(data.get(i).getName().equals(input.trim())){
                country = data.get(i);
                
            }
            
        }
        countryComboBox.getSelectionModel().select(country.getName());

    }
    
    
    
     /** Setter Method for phoneTextField                 *******
     *                                                       ****
     * @param input                                          ****
     ************************************************************/
    public void setPhoneTextField(String input){
           phoneTextField.setText(input);
    }
       
  
    /** Setter Method for ObservabelList masterData            **
     *                                                         **
     * @param m                                                **
     ************************************************************/                                                        
       public void setMasterData(ObservableList<schedule> m){
           masterData = m;
    }
    
 
       
       
       
     /** Getter Method that checks to see if a city exists in the database **
     * and if not, then creates it and returns the cityId                  **
     *                                                                     **
     * @param city                                                         **
     * @param country                                                      **
     * @param cityId                                                       **
     * @return cityId                                                      **
     ************************************************************************/   
    @FXML
     int grabCityId(String city, String country, int cityId) throws SQLException{
        
        //SELECT cityId from database
                ps = conn.prepareStatement("SELECT cityId FROM city WHERE city = ?");
                ps.setString(1, city);
                
                ResultSet rsCity = ps.executeQuery();
                
                //if city exists, grab cityId
                if(rsCity.next()){
                    cityId = rsCity.getInt("cityId");
                    System.out.println("cityId was grabbed.");
                }
                
                //if city doesn't exist, create city
                else{
                    ps = conn.prepareStatement("INSERT INTO city (city, countryId, createDate, createdBy, lastUpdateBy)"
                            + " VALUES (?, (SELECT countryId FROM country WHERE country = ?), CURDATE(), ?, ?)");
                    
                    ps.setString(1, city);
                    ps.setString(2, country);
                    ps.setString(3, user);
                    ps.setString(4, user);
                    
                    int result = ps.executeUpdate();
                    
                    //if city was created, grab cityId
                    if(result != 0){
                        
                        ps = conn.prepareStatement("SELECT cityId FROM city WHERE city = ?");
                        ps.setString(1, city);
                        
                        rsCity = ps.executeQuery();
                        if(rsCity.next()){
                            cityId = rsCity.getInt("cityId");
                            System.out.println("City was created and cityId was grabbed.");
                        }else{
                            System.out.println("City could not be created.");
                        }
                    }
                }
                return cityId;
        
    }

    
     
     
     
     
    /**** Getter Method that checks to see if an address exists in the database **
     * and if not, then creates it and returns the addressId                    **
     *                                                                          **
     * @param cityId                                                            **
     * @param address                                                           **
     * @param address2                                                          **
     * @param postal                                                            **
     * @param phone                                                             **
     * @param addressId                                                         **
     * @return addressId                                                        **
     *****************************************************************************/   
    @FXML
    int grabAddressId(int cityId, String address, String address2, String postal, String phone, int addressId) throws SQLException{
         //SELECT addressId from database
                ps = conn.prepareStatement("SELECT addressId FROM address WHERE address = ? AND address2 = ? AND cityId = ? AND postalCode = ?");
                ps.setString(1, address);
                ps.setString(2, address2);
                ps.setInt(3, cityId);
                ps.setString(4, postal);
                
                ResultSet rsAddress = ps.executeQuery();
                
                //if address exists, grab addressId
                if(rsAddress.next()){
                    addressId = rsAddress.getInt("addressId");
                    System.out.println("AddressId was grabbed.");
                }
                
                //if address doesn't exist, create address
                else{
                    System.out.println("Creating Address.");
                    ps = conn.prepareStatement("INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) "
                            + "VALUES (?, ?, ?, ?, ?, CURDATE(), ?, ?)");
                    ps.setString(1, address);
                    ps.setString(2, address2);
                    ps.setInt(3, cityId);
                    ps.setString(4, postal);
                    ps.setString(5, phone);
                    ps.setString(6, user);
                    ps.setString(7, user);
                    
                    int result = ps.executeUpdate();
                    
                    //if address was created, grab addressId
                    if(result != 0){
                        
                        ps = conn.prepareStatement("SELECT addressId FROM address WHERE address = ? AND address2 = ? AND cityId = ? AND postalCode = ?");
                        ps.setString(1, address);
                        ps.setString(2, address2);
                        ps.setInt(3, cityId);
                        ps.setString(4, postal);
                        
                        rsAddress = ps.executeQuery();
                        if(rsAddress.next()){
                            addressId = rsAddress.getInt("addressId");
                            System.out.println("Address was created and AddressId was grabbed.");
                        }
                        else{
                            System.out.println("Address could not be created.");
                        }
                        
                    }
                    
                }
                return addressId;
    }
    
}
