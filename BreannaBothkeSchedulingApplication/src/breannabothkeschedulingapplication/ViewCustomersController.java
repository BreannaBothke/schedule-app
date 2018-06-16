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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;



  


public class ViewCustomersController {

    @FXML
    private TableView<Customer> customersTableView;

    @FXML
    private TableColumn<?, ?> customerIdColumn, customerNameColumn, addressColumn,phoneColumn;

    @FXML
    private TextField nameTextField,streetTextField, aptTextField, cityTextField, phoneTextField, zipTextField;

    @FXML
    private Label nameLabel, streetLabel, cityLabel, zipLabel, aptLabel, countryLabel, phoneLabel,newCustomerLabel,tableErrorLabel, customersLabel,errorLabel;

    @FXML
    private ChoiceBox<?> countryChoiceBox;

    @FXML
    private Button deleteButton, updateButton, createButton, backButton;
    
    private ObservableList <Customer> tableData;
    
    private ViewCustomersController viewCust;
    private PreparedStatement ps;
    private Connection conn;
    private String user;
    private ObservableList<schedule> masterData;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private ObservableList<schedule> appointmentData;
    
    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;
    
    
    // assumes the current class is called MyLogger
    private final static Logger LOGGER = Logger.getLogger(ViewCustomersController.class.getName());
    
    
    
    
    
    
    
    
    /******     Initializer for View Customers Controller   ****
     *    Sets up the LOGGER when the screen launches.      ****
     *                                                      ****
     ***********************************************************/
    void initialize() throws IOException{
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(fileTxt);
        SimpleFormatter formatter = new SimpleFormatter();
        fileTxt.setFormatter(formatter);
    }
    
  
    
    
           /* <Button Actions> */
    
    
     /**    Back Button   *********************************************************
     **   Closes View Customers Screen and returns to the Appointments screen   ***
     ******************************************************************************/  
    @FXML
    void backButtonAction(ActionEvent event) throws IOException, SQLException {
        
                conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");

        
        //Selects all appointments connected to the current user logged in.
                ps = conn.prepareStatement("SELECT appointmentId, title, customerName, location, description, start, end "
                + "FROM customer c RIGHT JOIN appointment a " 
                + "ON c.customerId = a.customerId WHERE contact = ? ORDER BY start");
               
                ps.setString(1, user);
                
                ResultSet rs =  ps.executeQuery();

                masterData = FXCollections.observableArrayList();
                
                //changes appointment time to current users timezone and adds each appointment to ObservableList master
                while(rs.next()) {
                 
                    String appID = rs.getString("appointmentId");
                    String title = rs.getString("title");
                    String customerName = rs.getString("customerName");
                    String location = rs.getString("location");
                    String description = rs.getString("description");
                    
                    Timestamp  tableStart = rs.getTimestamp("start");
                    Timestamp tableEndStart = rs.getTimestamp("end");
               
                   
                    
                    //Converting time to current users timezone
                    
                    ZonedDateTime newzdtStart = tableStart.toLocalDateTime().atZone(ZoneId.of("UTC"));
                    ZonedDateTime newzdtEnd = tableEndStart.toLocalDateTime().atZone(ZoneId.of("UTC"));

                    ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(ZonedDateTime.now().getZone());
                    ZonedDateTime newLocalEnd = newzdtEnd.withZoneSameInstant(ZonedDateTime.now().getZone());
                    
                    
                    String date = newLocalStart.toLocalDate().toString();
                    String time = newLocalStart.toLocalTime().toString();
                    String end = newLocalEnd.toLocalTime().toString();
                    
                    String[] timeSplit = new String[1];
                    String[] endSplit = new String[1];
                    
                    endSplit = end.split(":");
                    timeSplit = time.split(":");
                    
                    String tableEnd = getEndTime(endSplit);
                    String tableTime = getStartTime(timeSplit);
                    

                    
                   
                    
                    
                    
                    
                    
                   
                    
                    
                    masterData.add(new schedule(appID, title, customerName, location, description, date, tableTime, tableEnd));
                }
            appointmentData = FXCollections.observableArrayList();

           //filter appointments
            masterData.stream().forEach((s) -> {
                LocalDate today = LocalDate.now();
                LocalDate appDate = LocalDate.parse(s.getDate(),dateFormat);
                if (appDate.getMonth() == today.getMonth() && appDate.getYear() == today.getYear()) {
                    appointmentData.add(s);
                }
            });
            
            Parent root, root2;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("appointments.fxml"));
            root = loader.load();
                        
            AppointmentController appCon = loader.getController();
           
            appCon.setTableData(appointmentData);
            appCon.setGreetingLabel(user);
            appCon.setUser(user);
            appCon.setZoneId(ZonedDateTime.now().getZone());
            appCon.setCurrentLabel(LocalDate.now().getMonth().toString());
            appCon.setMasterData(masterData);
        
            Stage stage;
            Stage stage2 = new Stage();
            
            //gets reference to the button's stage
            stage=(Stage)backButton.getScene().getWindow();
            
            //load up Other FXML Document
            root2 = FXMLLoader.load(getClass().getResource("viewCustomers.fxml"));
            Scene scene2 = new Scene(root);
            Scene scene = new Scene(root2);
            stage.setScene(scene);
            stage2.setScene(scene2);
            stage2.show();
            stage.hide();
            
        
    }

    
    
    
    
    
    
     /**    Create Button   **********************
     **   Creates a new Customer               ***
     *********************************************/ 
    @FXML
    void createButtonAction(ActionEvent event) throws InterruptedException {

        
         ObservableList<Customer> data = getCustomerData();
       
            

        
         
                
        if(nameTextField.getText().isEmpty() || streetTextField.getText().isEmpty()
              
            || cityTextField.getText().isEmpty() || zipTextField.getText().isEmpty()
            || phoneTextField.getText().isEmpty() || countryChoiceBox.getSelectionModel().isEmpty())
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
                String country = countryChoiceBox.getSelectionModel().getSelectedItem().toString();
                String phone = phoneTextField.getText();
                int cityId = 0;
                int addressId = 0;
                
                conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
                
                //check to see if customer already exists.
                if(doesCustomerExist(customerName) == true)
                    errorLabel.setText("Customer already exists.");
                else{
                
                //Grab cityId
                cityId = grabCityId(city, country, cityId);
                
                
                //Grab addressId
                addressId = grabAddressId(cityId, address, address2, postal, phone, addressId);
                
                
                
                
                
                
                if(cityId != 0 && addressId != 0){
                    
                    //Create customer
                    ps = conn.prepareStatement("INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, "
                            + "lastUpdateBy) VALUES (?, ?, 1, CURDATE(), ?, CURDATE(), ?)");
                    ps.setString(1, customerName);
                    ps.setInt(2, addressId);
                    ps.setString(3, user);
                    ps.setString(4, user);
                   

                    int rs = ps.executeUpdate();

                    if(rs != 0){

                        ps = conn.prepareStatement("SELECT customerId, customerName, address,"
                     + " address2, city, country, postalCode, phone " 
                     + "FROM customer c LEFT JOIN address a ON c.addressId = a.addressid LEFT JOIN city "
                     + "ON a.cityId = city.cityid LEFT JOIN country ON city.countryId = country.countryid WHERE customerName = ?");
                        
                        ps.setString(1, customerName);
                        
                        ResultSet selectCust = ps.executeQuery();
                        
                        if(selectCust.next()){
                            String custId = Integer.toString(selectCust.getInt("customerId"));
                            String custName = selectCust.getString("customerName");
                            String custAddress = selectCust.getString("address") + ", " + selectCust.getString("address2")
                                    + ", " + selectCust.getString("city") + ", " + selectCust.getString("country") + ", " + selectCust.getString("postalCode");
                            String phoneNum = selectCust.getString("phone");
                            
                            data.add(new Customer(custId, custName, custAddress, phoneNum));

                        
                        


                        //text fields are cleared            
                        nameTextField.setText("");
                        streetTextField.setText("");
                        aptTextField.setText("");  
                        cityTextField.setText("");
                        zipTextField.setText("");
                        phoneTextField.setText("");
                        countryChoiceBox.getSelectionModel().clearSelection();
                        errorLabel.setText("");
                        
                        LOGGER.log(Level.INFO, "Customer: {0} was successfully created by {1} at {2}./n", new Object[]{custName, user, LocalDateTime.now()});

                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle(null);
                        alert.setHeaderText(null);
                        alert.setContentText("Customer was Successfully created!");

                        alert.show();
                       
                        System.out.println("Customer was created.");
                        }
                    }
                    else{
                        System.out.println("Customer was not created.");
                        errorLabel.setText("Customer was not created.");
                    }
                }else
                    System.out.println("addressId or cityId was equal to 0.");
                }
        
            }catch(SQLException e){
                
                System.out.println("View Customers/create customer: Something went wrong.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
                
                }
    
        }
        
    }

    
    
    
    
    
     /**    Delete Button   *******************************************************
     **   Deletes the selected customer from the table view and database        ***
     ******************************************************************************/
    @FXML
    void deleteButtonAction(ActionEvent event) {

        
         boolean isNotSelected = customersTableView.getSelectionModel().isEmpty();
         Customer selectedCust = customersTableView.getSelectionModel().getSelectedItem();
        
        
        if(isNotSelected == false){
            tableErrorLabel.setText("");
            
            String custID = customersTableView.getSelectionModel().getSelectedItem().getCustomerId();
        
        try{
            conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
            
            ps = conn.prepareStatement("DELETE FROM customer WHERE customerId = ?");
            ps.setString(1, custID);
            //ResultSet rs = st.executeQuery("SELECT * FROM appointment");
            
            //Alert Box
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deletion Confirmation Dialog");
            alert.setHeaderText("Deleting Customer");
            alert.setContentText("Are you sure you want to delete Customer with the name of " + selectedCust.getCustomerName() + "?");

            Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                // ... user chose OK
                    int resultNum = ps.executeUpdate();
                    
                    if(resultNum == 1){
                        
                        System.out.println("Deletion Successful!");
                                           
                        tableData.remove(selectedCust);
                        LOGGER.log(Level.INFO, "Customer: {0} was deleted by {1} at {2}./n", new Object[]{selectedCust.getCustomerName(), user, LocalDateTime.now()});
                    } else {
                        //Error Dialog
                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle("Error Dialog");
                        alert2.setHeaderText("ERROR");
                        alert2.setContentText("Deletion Failed.");

                        alert.showAndWait();
                    }
                    
                    
                    
                } else {
                // ... user chose CANCEL or closed the dialog

                       }
            
                
            
        } catch (SQLException e){
            System.out.println("Delete Customer: Something went wrong.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
        }
        }else{
              tableErrorLabel.setText("Select a Customer to delete.");

        }
        
        
        
    }

    
    
    
    
    
    /**    Update Button   ****************************************************************************************************
     **   Launches the Update Customer screen where you can modify/update an existing customer with new information.        ***
     **************************************************************************************************************************/
    
    @FXML
    void updateButtonAction(ActionEvent event) throws IOException {

        Boolean isNotSelected = customersTableView.getSelectionModel().isEmpty();
        
     /*If a customer is selected in the Customers Table View, then updateCustomer.fxml opens*/   
        if (isNotSelected == false){
        
              tableErrorLabel.setText("");
        
              
              
              setViewCustomersController(this);
              
              FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("updateCustomer.fxml"));
              Parent root1 = fxmlLoader.load();
              Stage stage = new Stage();
              stage.initModality(Modality.APPLICATION_MODAL);
              stage.setScene(new Scene(root1));
              stage.show();
            
              //grabs controller UpdateCustomerController.java
              UpdateCustomerController upCust = fxmlLoader.getController();
              
              //calls method setViewCustomersController from UpdateCustomerController.java and sets variable viewCust to ViewCustomersController.java
              upCust.setViewCustomersController(viewCust);
              
              //calls method setSelectedPartIndex from UpdateCustomerController.java and sets variable Index to the current selected customer
              upCust.setSelectedCustomerIndex(customersTableView.getSelectionModel().getSelectedIndex());
              
              //calls method setMasterData from UpdateCustomerController.java and sets variabel masterData to ObservableList of appointments in the database
              upCust.setMasterData(masterData);
              
              
              String[] address = new String[6];
              ObservableList<country> data = FXCollections.observableArrayList();
              address = customersTableView.getSelectionModel().getSelectedItem().getAddress().split(",");
              //calls methods from UpdateCustomerController.java and sets text fields text to the current selected customer's information
              upCust.setID(customersTableView.getSelectionModel().getSelectedItem().getCustomerId());
              upCust.setNameTextField(customersTableView.getSelectionModel().getSelectedItem().getCustomerName());
              upCust.setStreetTextField(address[0].trim());
              upCust.setAptTextField(address[1].trim());
              upCust.setCityTextField(address[2].trim());
              upCust.setZipTextField(address[4].trim());
              upCust.setPhoneTextField(customersTableView.getSelectionModel().getSelectedItem().getPhone());
              upCust.setUser(user);
              
              try{
              conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");

              ps = conn.prepareStatement("SELECT countryId, country FROM country");
              ResultSet rs = ps.executeQuery();
              
              while(rs.next()){
                  String countryId = rs.getString("countryId");
                  String country = rs.getString("country");
                  
                  data.add(new country(countryId, country));
              }
              } catch(SQLException e){
                    System.out.println("Failed to Connect to Database.");
                    System.out.println("SQLException: "+e.getMessage());
                    System.out.println("SQLState: "+e.getSQLState());
                    System.out.println("VendorError: "+e.getErrorCode());
              }
              upCust.setCountryComboBox(data, address[3]);
              
              

                 
   /*If a part is not selected in the Parts Table View, then an error label is set.*/     
        } else if(isNotSelected == true){
            tableErrorLabel.setText("Select a Customer to Update.");
        }
        
        
    }

    
    
    
    
    
    
    
       /* <Setter and Getter Methods> */
    
    
    
    
    
    /** Setter Method for Country Choice Box    ****
     *                                          ****
     * @param data                              ****
     ***********************************************/
    @FXML
    void setCountryChoiceBox(ObservableList data){
        
        countryChoiceBox.setItems(data);
        
    }
    
    
    
    
    
     /** Setter Method for Table View customersTableView     ****
     *                                                       ****
     * @param data                                           ****
     ************************************************************/
    @FXML
    void setTableData(ObservableList<Customer> data){
          tableData = data;
        customersTableView.setItems(data);
    }
    
    
    
    
    
    
    /** Setter Method for ObservableList masterData   ****
     *                                                ****
     * @param m                                       ****
     *****************************************************/
    @FXML
    void setMasterData(ObservableList<schedule> m){
        masterData = m;
    }
    
    
    
    
    
    
    
     /** Getter Method that returns customer table view data    ****
     *                                                          ****
     * @return tableData                                        ****
     ***************************************************************/
    @FXML
    ObservableList<Customer> getCustomerData(){
        return tableData;
    }
    
    
    
    
    
    /** Setter Method for View Customers Controller viewCust    ****
     *                                                          ****
     * @param v                                                 ****
     ***************************************************************/
    @FXML
    void setViewCustomersController (ViewCustomersController v){
        
        viewCust = v;
        
    }
 
    
    
    
    
    /** Setter Method for variable user    ****
     *                                     ****
     * @param u                            ****
     ******************************************/
    @FXML
    void setUser(String u){
        user = u;
    }









     /** Getter method that checks to see if a customer exists.   ****
     *   If it does, then a boolean value of true is returned.    ****
     * @param customerName                                        ****
     *****************************************************************/
    @FXML
    boolean doesCustomerExist(String customerName) throws SQLException{
        PreparedStatement ps;
        
        ps = conn.prepareStatement("SELECT customerName FROM customer WHERE customerName = ?");
        ps.setString(1, customerName);
        
        ResultSet rs = ps.executeQuery();
        
        if(rs.next())
            return true;
        else
            return false;
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
    

    
    
    
    
    
    
     /** Getter Method for endTime that accepts a String variable and converts it to civilian time.      ****
     *                                                                                                   ****
     * @param endSplit[]                                                                                 ****
     ********************************************************************************************************/
    @FXML
    String getEndTime(String endSplit[]){
    
        String tableEnd = "";
        //End time
                     switch (endSplit[0]) {
                        case "01":
                            tableEnd = "1" + ":" + endSplit[1] + " am";
                            break;
                        case "02":
                            tableEnd = "2" + ":" + endSplit[1] + " am";
                            break;
                        case "03":
                            tableEnd = "3" + ":" + endSplit[1] + " am";
                            break;
                        case "04":
                            tableEnd = "4" + ":" + endSplit[1] + " am";
                            break;
                        case "05":
                            tableEnd = "5" + ":" + endSplit[1] + " am";
                            break;
                        case "06":
                            tableEnd = "6" + ":" + endSplit[1] + " am";
                            break;
                        case "07":
                            tableEnd = "7" + ":" + endSplit[1] + " am";
                            break;
                        case "08":
                            tableEnd = "8" + ":" + endSplit[1] + " am";
                            break;
                        case "09":
                            tableEnd = "9" + ":" + endSplit[1] + " am";
                            break;
                        case "10":
                            tableEnd = "10" + ":" + endSplit[1] + " am";
                            break;
                        case "11":
                            tableEnd = "11" + ":" + endSplit[1] + " am";
                            break;
                        case "12":
                            tableEnd = "12" + ":" + endSplit[1] + " pm";
                            break;


                        case "13":
                            tableEnd = "1" + ":" + endSplit[1] + " pm";
                            break;
                        case "14":
                            tableEnd = "2" + ":" + endSplit[1] + " pm";
                            break;
                        case "15":
                            tableEnd = "3" + ":" + endSplit[1] + " pm";
                            break;
                        case "16":
                            tableEnd = "4" + ":" + endSplit[1] + " pm";
                            break;
                        case "17":
                            tableEnd = "5" + ":" + endSplit[1] + " pm";
                            break;
                        case "18":
                            tableEnd = "6" + ":" + endSplit[1] + " pm";
                            break;
                        case "19":
                            tableEnd = "7" + ":" + endSplit[1] + " pm";
                            break;
                        case "20":
                            tableEnd = "8" + ":" + endSplit[1] + " pm";
                            break;
                        case "21":
                            tableEnd = "9" + ":" + endSplit[1] + " pm";
                            break;
                        case "22":
                            tableEnd = "10" + ":" + endSplit[1] + " pm";
                            break;

                        case "23":
                          tableEnd = "11" + ":" + endSplit[1] + " pm";
                            break;
                        case "24":
                            tableEnd = "12" + ":" + endSplit[1] + " am";
                            break;
                        default:
                            break;
                    }
                     
        return tableEnd;
    }
    
    
    
    
    
     /** Getter Method for startTime that accepts a String variable and converts it to civilian time.    ****
     *                                                                                                   ****
     * @param timeSplit[]                                                                                ****
     ********************************************************************************************************/
    @FXML
    String getStartTime(String timeSplit[]){
        
        String tableTime = "";
         //Start Time
                    switch (timeSplit[0]) {
                        case "01":
                            tableTime = "1" + ":" + timeSplit[1] + " am";
                            break;
                        case "02":
                            tableTime = "2" + ":" + timeSplit[1] + " am";
                            break;
                        case "03":
                            tableTime = "3" + ":" + timeSplit[1] + " am";
                            break;
                        case "04":
                            tableTime = "4" + ":" + timeSplit[1] + " am";
                            break;
                        case "05":
                            tableTime = "5" + ":" + timeSplit[1] + " am";
                            break;
                        case "06":
                            tableTime = "6" + ":" + timeSplit[1] + " am";
                            break;
                        case "07":
                            tableTime = "7" + ":" + timeSplit[1] + " am";
                            break;
                        case "08":
                            tableTime = "8" + ":" + timeSplit[1] + " am";
                            break;
                        case "09":
                            tableTime = "9" + ":" + timeSplit[1] + " am";
                            break;
                        case "10":
                            tableTime = "10" + ":" + timeSplit[1] + " am";
                            break;
                        case "11":
                          tableTime = "11" + ":" + timeSplit[1] + " am";
                            break;
                        case "12":
                            tableTime = "12" + ":" + timeSplit[1] + " pm";
                            break;


                        case "13":
                            tableTime = "1" + ":" + timeSplit[1] + " pm";
                            break;
                        case "14":
                            tableTime = "2" + ":" + timeSplit[1] + " pm";
                            break;
                        case "15":
                            tableTime = "3" + ":" + timeSplit[1] + " pm";
                            break;
                        case "16":
                            tableTime = "4" + ":" + timeSplit[1] + " pm";
                            break;
                        case "17":
                            tableTime = "5" + ":" + timeSplit[1] + " pm";
                            break;
                        case "18":
                            tableTime = "6" + ":" + timeSplit[1] + " pm";
                            break;
                        case "19":
                            tableTime = "7" + ":" + timeSplit[1] + " pm";
                            break;
                        case "20":
                            tableTime = "8" + ":" + timeSplit[1] + " pm";
                            break;
                        case "21":
                            tableTime = "9" + ":" + timeSplit[1] + " pm";
                            break;
                        case "22":
                            tableTime = "10" + ":" + timeSplit[1] + " pm";
                            break;

                        case "23":
                          tableTime = "11" + ":" + timeSplit[1] + " pm";
                            break;
                        case "24":
                            tableTime = "12" + ":" + timeSplit[1] + " am";
                            break;
                        default:
                            break;
                    }
        return tableTime;
    }
}