/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package breannabothkeschedulingapplication;

import breannabothkeschedulingapplication.Models.schedule;
import breannabothkeschedulingapplication.Models.Customer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AppointmentController {

    @FXML
    private Label appointmentLabel, errorLabel, greetingLabel, currentLabel;

    @FXML
    private TableView<schedule> appointmentTableView;

    @FXML
    private TableColumn<schedule, String> titleColumn, customerNameColumn, descColumn, locationColumn;
    @FXML
    private TableColumn<schedule, Date> dateColumn;

    @FXML
    private TableColumn<schedule, Time> timeColumn; 
    private ObservableList<schedule> tableData, masterData;

    @FXML
    private Button deleteButton, addButton, previousButton, nextButton, logoutButton, updateButton;
    
    @FXML private MenuButton viewMenuButton;

    @FXML
    private ToggleGroup group;
    
    @FXML
    private ToggleButton monthToggleButton, weekToggleButton;
    
    private String user, password;
    private LoginController logCon;
    private final ZonedDateTime ztd = ZonedDateTime.now();
    private final ZoneId zone = ztd.getZone(), zone1 =  ZoneId.of("Europe/Berlin");
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");   
    private Connection conn;
    private PreparedStatement ps;
    private Statement stmt;
    private ZoneId zId;
    private ObservableList<schedule> data, prevWeekData, prevMonthData, data2, nextWeekData, nextMonthData;
    private ObservableList<Customer> cust;
    private int currentYearWeek;
    private LocalDate previousWeekDate, previousMonthDate, nextWeekDate, nextMonthDate;
    private int monthNum = LocalDate.now().getMonthValue();
    private int year = LocalDate.now().getYear();
    private int week, yearWeek, prevNextButtonCount = 0;

    private String months[] = {"Empty", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        

    
     /******     Initializer for Appointment Controller       **********************
     *    Sets variable user to the value of user from the login controller     ****
     *                                                                          ****
     *******************************************************************************/    
    void initialize() throws SQLException{
        user = logCon.getUsername();   
        
       
    }
   
    
    
    
    
    
    
    /* <Button Actions> */
    
    
    
    /**    View Customers Menu Button   *******************************************************************
     **   Launches the View Customers screen where you can view customer info and create new customers. ***
     ******************************************************************************************************/
    @FXML
    void viewCustomerAction (ActionEvent event) throws Exception{
        ObservableList countries= FXCollections.observableArrayList();
        
        try{
            conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
            if(!zId.equals(ZoneId.of("Europe/Berlin"))){
            System.out.println("View Customers: Connected to database : Success!");
            } 
            
            //grab countries from database
            ps = conn.prepareStatement("SELECT country from country");
            ResultSet rs = ps.executeQuery();
            
          
                //add country names to observableList countries
                while(rs.next()){
                    String country = rs.getString("country");
                    countries.add(country);
                }
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("appointments.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("viewCustomers.fxml"));
            Parent root = fxmlLoader.load();
            Parent root2 = loader2.load();
            
            ViewCustomersController viewCust = fxmlLoader.getController();
            viewCust.setCountryChoiceBox(countries);
            viewCust.setUser(user);
            viewCust.setMasterData(masterData);

            
            
            
            
             ps = conn.prepareStatement("SELECT customerId, customerName, address,"
                     + " address2, city, country, postalCode, phone " 
                     + "FROM customer c LEFT JOIN address a ON c.addressId = a.addressid LEFT JOIN city "
                     + "ON a.cityId = city.cityid LEFT JOIN country ON city.countryId = country.countryid ORDER BY customerId;");
                
                rs =  ps.executeQuery();

                int num = 0;
                cust = FXCollections.observableArrayList();

                while(rs.next()) {
                 
                    String customerId = Integer.toString(rs.getInt("customerId"));
                    String customerName = rs.getString("customerName");
                    String address = rs.getString("address") + ", " + rs.getString("address2") + ", " + rs.getString("city") + ", " + rs.getString("country") + ", " + rs.getString("postalCode");
                    String phone = rs.getString("phone");
                   
                    cust.add(new Customer(customerId, customerName, address, phone));
                }
                viewCust.setTableData(cust);
            
            
            
            //open newCustomer FXML file in new window
            Stage stage = new Stage();
            Stage stage2 = new Stage();
            stage2=(Stage)viewMenuButton.getScene().getWindow();
            stage2.setScene(new Scene(root2));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
            stage2.hide();
        } catch (SQLException e){
            System.out.println("Failed to Connect to Database.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
        }
        
    }
    
    
    
    
    
    
    
    
    
    
    
    /**    View Log Button   **********************************************************************************
     **   Launches the View Log screen where you can read the log that contains info about users logging in.***
     **********************************************************************************************************/
    @FXML
    void viewLogAction (ActionEvent event) throws IOException{
            
            String fileName = "..\\BreannaBothkeSchedulingApplication\\schedapp-log.txt";
             Stage stage = new Stage();
            
            //FileChooser fileChooser = new FileChooser();
            
            //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files(*.txt)", "*.txt");
            
            //fileChooser.getExtensionFilters().add(extFilter);
            
           // File file = fileChooser.showOpenDialog(stage);
           // if(file != null){
                 FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("viewLog.fxml"));
            Parent root = fxmlLoader.load();
            
            ViewLogController viewLog = fxmlLoader.getController();
            viewLog.setTextArea(fileName);
            //System.out.println(file.toPath().toString());
            
             //open viewLog FXML file in new window
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
           // }
        
           
        
    }
    
    
    
    
    
    
    
    
    
    
    
    /**    View Reports Button   ************************************************************
     **   Launches the View Reports screen where you can view reports on                 ****
     ** how many appointment types there are per month, the schedule of each consultant, ****
     ** and how many appointments there are for each location                            ****
     *********************************************************************************&******/
    @FXML
    void viewReportsAction (ActionEvent event) throws IOException{
        
        
            Stage stage = new Stage();
            
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("viewReports.fxml"));
            Parent root = fxmlLoader.load();
            ViewReportsController viewRep = fxmlLoader.getController();
            viewRep.setZoneId(zId);
            
       
            
             //open viewLog FXML file in new window
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        
    }
    
    
    
    
    
    
    
    /**    Add Button   ***********************************************************************************
     **   Launches Add Appointment screen where you can create a new appointment with the current user. ***
     ******************************************************************************************************/
    @FXML
    void addButtonAction(ActionEvent event) throws Exception{

         //Connection conn;
        PreparedStatement ps2, ps3;
        ObservableList custName = FXCollections.observableArrayList();
        //ObservableList times = FXCollections.observableArrayList();
        try{
            conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
            if(!zId.equals(ZoneId.of("Europe/Berlin"))){
            System.out.println("Appointments: Connected to database : Success!");
            } else{
            
            System.out.println("Termine: Verbunden mit der Datenbank : Erfolg!");
            }
            
            //grab customerName from database
            ps3 = conn.prepareStatement("SELECT customerName from customer ORDER BY customerName");
            ResultSet rs = ps3.executeQuery();
            
          
                //add customer names to observableList custName
                while(rs.next()){
                    String name = rs.getString("customerName");
                    custName.add(name);
                }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("appointments.fxml"));
            Parent root = loader.load();
            //Load newAppointment FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("newAppointment.fxml"));
            Parent root1 = fxmlLoader.load();
              
            //grab newAppointment controller
            AddAppointmentController addApp = fxmlLoader.getController();
            //populate the customerNameComboBox in AddAppointmentController FXML file with ObservableList custName
            addApp.setCustNameData(custName);
            addApp.setZoneId(zId);
            addApp.setUser(user);
            addApp.setTimeComboBox();
            addApp.setLoginController(logCon);
            addApp.setMasterData(masterData);
            
            System.out.println("Opening New Appointment Window...");
            System.out.println(".....");
            
            System.out.println(masterData.size());
             
            //open newAppointment FXML file in new window
            Stage stage = new Stage(), stage2 = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage2.setScene(new Scene(root));
            stage2=(Stage)addButton.getScene().getWindow();
            stage2.hide();
            stage.setScene(new Scene(root1));
            stage.show();
            
            
            
        
            

                
            
        } catch (SQLException e){
            System.out.println("Appointments: Failed to Connect to Database.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
        }
        
    }

    
    
    
    
    
    
    /**    Update Button   ****************************************************************************************************
     **   Launches the Update Appointment screen where you can modify/update an existing appointment with the current user. ***
     **************************************************************************************************************************/
    @FXML
    void updateButtonAction(ActionEvent event) throws IOException, SQLException{
        
        boolean isNotSelected = appointmentTableView.getSelectionModel().isEmpty();
        schedule selectedApp = appointmentTableView.getSelectionModel().getSelectedItem();
        
        if(isNotSelected == false){
            
            ObservableList custName = FXCollections.observableArrayList();
            try{
                conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
                System.out.println("Update Appointments: Connected to database : Success!");



                //grab customerName from database
                ps = conn.prepareStatement("SELECT customerName from customer ORDER BY customerName");
                ResultSet rs = ps.executeQuery();


                    //add customer names to observableList custName
                    while(rs.next()){
                        String name = rs.getString("customerName");
                        custName.add(name);
                    }
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("updateAppointment.fxml"));
                Parent root = fxmlLoader.load();

                UpdateAppointmentController updateApp = fxmlLoader.getController();
                updateApp.setUpdateAppointmentController(updateApp);
                updateApp.setUser(user);
                updateApp.setTableIndex(appointmentTableView.getSelectionModel().getSelectedIndex());
                updateApp.setMasterData(masterData);
                updateApp.setZoneId(zId);
                updateApp.setSelectedApp(selectedApp);
                updateApp.setCustNameData(custName);
                updateApp.setDatePicker();
                updateApp.setTitleTextField();
                updateApp.setLocationComboBox();
                updateApp.setTimeComboBox();
                updateApp.setDescriptionTextArea();
                updateApp.setMinuteButton();
               
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("appointments.fxml"));
                Parent root2 = loader2.load();
                 //open viewLog FXML file in new window
                Stage stage = new Stage();
                Stage stage2 = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage2.setScene(new Scene(root2));
                stage2=(Stage)updateButton.getScene().getWindow();
                stage.show();
                stage2.hide();
                errorLabel.setText("");
            }catch(SQLException e){
                    System.out.println("Update Appointment: Failed to Connect to Database.");
                    System.out.println("SQLException: "+e.getMessage());
                    System.out.println("SQLState: "+e.getSQLState());
                    System.out.println("VendorError: "+e.getErrorCode());

            }
        }else{
            errorLabel.setText("Select an appointment to Update.");
        }
        
    }
    
    
    
    
    
    /**    Delete Button   ***********************************************************
     **   Deletes the selected appointment from the table view and database        ***
     *********************************************************************************/
    @FXML
    void deleteButtonAction(ActionEvent event) {

        
        boolean isNotSelected = appointmentTableView.getSelectionModel().isEmpty();
        schedule selectedApp = appointmentTableView.getSelectionModel().getSelectedItem();
        
        PreparedStatement ps;
        
        if(isNotSelected == false){
            errorLabel.setText("");
            
            String appID = appointmentTableView.getSelectionModel().getSelectedItem().getID();
        
        try{
            conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
            if(!zId.equals(ZoneId.of("Europe/Berlin"))){
            System.out.println("Delete Appointment: Connected to database : Success!");
            } else{
            
            System.out.println("Verbunden mit der Datenbank : Erfolg!");
            }
            ps = conn.prepareStatement("DELETE FROM appointment WHERE appointmentId = ? AND contact = ?");
            ps.setString(1, appID);
            ps.setString(2, user);
            //ResultSet rs = st.executeQuery("SELECT * FROM appointment");
            
            //Alert Box
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deletion Confirmation Dialog");
            alert.setHeaderText("Delete Appointment");
            alert.setContentText("Are you sure you want to delete appointment with " + selectedApp.getCustomerName() + " on " + selectedApp.getDate() + "?");

            Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                // ... user chose OK
                    int resultNum = ps.executeUpdate();
                    
                    if(resultNum == 1){
                        
                        System.out.println("Deletion Successful!");
                                           
                        masterData.remove(selectedApp);
                        appointmentTableView.getItems().remove(selectedApp);
                    } else {
                        //Error Dialog
                        Alert alert2 = new Alert(AlertType.ERROR);
                        alert2.setTitle("Error Dialog");
                        alert2.setHeaderText("ERROR");
                        alert2.setContentText("Deletion Failed.");

                        alert.showAndWait();
                    }
                    
                    
                    
                } else {
                // ... user chose CANCEL or closed the dialog

                       }
            
                
            
        } catch (SQLException e){
            System.out.println("Delete Appointment: Something went wrong.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
        }
        }else{
              errorLabel.setText("Select an appointment to delete.");

        }
        
        
        
        
    }
    
    
    
    
    
    /**    Logout Button   ***********************************************************
     **   Logs out the current user and launches the Login screen                  ***
     *********************************************************************************/
    @FXML
    void logoutButtonAction(ActionEvent event) throws IOException{
        
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("appointments.fxml"));
            Parent root = fxmlLoader.load();
            Parent root1 = loader2.load();

            
            LoginController logCon = fxmlLoader.getController();
            logCon.setZoneId(zone);
        
        /*Translates from English to German if the ZoneId is of Berlin*/

            if( !zone.equals(ZoneId.of("Europe/Berlin"))){
                logCon.runEnglish();
            } else {
                logCon.runGerman();
                }
            
             //open viewLog FXML file in new window
            Stage stage = new Stage();
            Stage stage2 = new Stage();
            stage2=(Stage)logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            Scene scene2 = new Scene(root1);
            stage.setScene(scene);
            stage2.setScene(scene2);
            stage2.hide();
            stage.show();
        
    }
    
    
    
    
    
    /**    Month Toggle Button   ************************************
     **   Displays appointments for each month in the table view. ***
     ****************************************************************/
    @FXML
    void monthToggleButtonAction(ActionEvent event) throws SQLException {
        try{
            conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
            System.out.println("Filter Appointments By Month: Connected to database : Success!");
            
            monthNum = LocalDate.now().getMonthValue();
            year = LocalDate.now().getYear();
            prevNextButtonCount = 0;
            
            currentLabel.setText(months[monthNum]);
            
            previousButton.setDisable(false);
            nextButton.setDisable(false);
            ps = conn.prepareStatement("SELECT appointmentId, title, customerName, location, description, start, end "
                + "FROM customer c RIGHT JOIN appointment a " 
                + "ON c.customerId = a.customerId WHERE MONTH(start) = ? AND YEAR(start) = ? AND contact = ? ORDER BY start");
            
            LocalDate d = LocalDate.now();
            data = FXCollections.observableArrayList();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat tf = new SimpleDateFormat("HH:mm");
           
            ps.setInt(1, d.getMonthValue());
            ps.setInt(2, d.getYear());
            ps.setString(3, user);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                String appID = rs.getString("appointmentId");
                String title = rs.getString("title");
                String customerName = rs.getString("customerName");
                String location = rs.getString("location");
                String description = rs.getString("description");
                String time = tf.format(rs.getTime("start"));
                
                Timestamp t = rs.getTimestamp("start");
                Timestamp et = rs.getTimestamp("end");

                
                String tableTime = getTime(t);
                String tableEnd = getTime(et);
                ZonedDateTime newzdtStart = t.toLocalDateTime().atZone(ZoneId.of("UTC"));

                ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId); 
                
                String newTDate = newLocalStart.toLocalDate().toString();
                
                data.add(new schedule(appID, title, customerName, location, description, newTDate, tableTime, tableEnd));
                currentLabel.setText(d.getMonth().toString());

            }
            setTableData(data);
        
        }catch (SQLException e){
            System.out.println("Filter Appointments By Month: Something went wrong.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
        }

    }

    
    
    
    
    /**    Week Toggle Button   *******************************************************************
     **   Displays appointments by the week. Only shows the previous, current and upcoming week.***
     **********************************************************************************************/
    @FXML
    void weekToggleButtonAction(ActionEvent event) {
        currentLabel.setText("Current Week");
        monthNum = LocalDate.now().getMonthValue();
        year = LocalDate.now().getYear();
        prevNextButtonCount = 0;
        
        try{
            conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
            System.out.println("Filter Appointments By Week: Connected to database : Success!");
            
            ps = conn.prepareStatement("SELECT YEARWEEK(CURDATE()) AS yearWeek");
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                currentYearWeek = rs.getInt("yearWeek");
            }
       

            ps = conn.prepareStatement("SELECT appointmentId, title, customerName, location, description, start, end "
                + "FROM customer c RIGHT JOIN appointment a " 
                + "ON c.customerId = a.customerId WHERE YEARWEEK(`start`, 0) = YEARWEEK(CURDATE(), 0) AND contact = ? ORDER BY start");
            
            LocalDate d = LocalDate.now();
            data2 = FXCollections.observableArrayList();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat tf = new SimpleDateFormat("HH:mm");
           
            ps.setString(1, user);
       
            rs = ps.executeQuery();
            
            while(rs.next()){
                String appID = rs.getString("appointmentId");
                String title = rs.getString("title");
                String customerName = rs.getString("customerName");
                String location = rs.getString("location");
                String description = rs.getString("description");
                
                Timestamp t = rs.getTimestamp("start");
                Timestamp et = rs.getTimestamp("end");

                
                String tableTime = getTime(t);
                String tableEnd = getTime(et);
                
                ZonedDateTime newzdtStart = t.toLocalDateTime().atZone(ZoneId.of("UTC"));

                ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId); 
                
                String newTDate = newLocalStart.toLocalDate().toString();
                
                data2.add(new schedule(appID, title, customerName, location, description, newTDate, tableTime, tableEnd));

            }
            setTableData(data2);
        
        }catch (SQLException e){
            System.out.println("Filter Appointments By Week: Something went wrong.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
        }


    }
    
    
    
    
    
    
    
    /**    Previous Button   ***************************************************************************************************************************************
     **   Depending on whether the month or week toggle button is selected, this button either shows the previous month of appointments or week of appointments. ***
     ***************************************************************************************************************************************************************/
    @FXML
    void previousButtonAction(ActionEvent event){
        DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        previousWeekDate = LocalDate.now().minus(1, ChronoUnit.WEEKS);
        
        //tableData.iterator().next().getDate();
       // System.out.println();
        
     //   previousMonthDate = LocalDate.parse(appointmentTableView.getItems().get(0).getDate(), DATE_FORMAT).minus(1, ChronoUnit.MONTHS);
              
        
        try{
            conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
            System.out.println("Previous Appointments: Connected to database : Success!");
            if(monthToggleButton.isSelected()){
                System.out.println("Month: " + monthNum + " Year: " + year);


                monthNum--;

                if(monthNum < 1){
                    monthNum = 12;
                    year--;
                }
                if(monthNum > 12){
                    monthNum = 1;
                    year++;
                }
            System.out.println("Month: " + monthNum + " Year: " + year);
            System.out.println(LocalDate.now().lengthOfYear());
            
            
            currentLabel.setText(months[monthNum]);
            
            
            /* ps = conn.prepareStatement("SELECT appointmentId, title, customerName, location, description, start, end "
            + "FROM customer c RIGHT JOIN appointment a "
            + "ON c.customerId = a.customerId WHERE MONTH(start) = ? AND YEAR(start) = ? AND contact = ? ORDER BY start");*/
            
            prevMonthData = FXCollections.observableArrayList();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat tf = new SimpleDateFormat("HH:mm");
            
            
            
             //filter appointments
                masterData.stream().forEach((s) -> {
                    LocalDate today = LocalDate.now();
                    
                    LocalDate appDate = LocalDate.parse(s.getDate(),dateFormat);
                    if (appDate.getMonthValue() == monthNum && appDate.getYear() == today.getYear()) {
                        prevMonthData.add(s);
                    }
                });
            
                /*      ps.setInt(1, monthNum);
                ps.setInt(2, year);
                ps.setString(3, user);
                
                ResultSet rs = ps.executeQuery();
                
                while(rs.next()){
                String appID = rs.getString("appointmentId");
                String title = rs.getString("title");
                String customerName = rs.getString("customerName");
                String location = rs.getString("location");
                String description = rs.getString("description");
                
                
                Timestamp t = rs.getTimestamp("start");
                Timestamp et = rs.getTimestamp("end");
                
                
                String tableTime = getTime(t);
                String tableEnd = getTime(et);
                
                ZonedDateTime newzdtStart = t.toLocalDateTime().atZone(ZoneId.of("UTC"));
                
                ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId);
                
                String newTDate = newLocalStart.toLocalDate().toString();
                
                prevMonthData.add(new schedule(appID, title, customerName, location, description, newTDate, tableTime, tableEnd));
                
                }*/
            setTableData(prevMonthData);
            
            
        } else if(weekToggleButton.isSelected()){
            
            prevNextButtonCount--;
            
                switch (prevNextButtonCount) {
                    case 0:
                        currentLabel.setText("Current Week");
                        previousButton.setDisable(false);
                        nextButton.setDisable(false);
                        break;
                    case -1:
                        currentLabel.setText("Previous Week");
                        previousButton.setDisable(true);
                        nextButton.setDisable(false);
                        break;
                    case 1:
                        currentLabel.setText("Next Week");
                        previousButton.setDisable(false);
                        nextButton.setDisable(true);
                        break;
                    default:
                        break;
                }
            

              
                
            
            
            
            ps = conn.prepareStatement("SELECT appointmentId, title, customerName, location, description, start, end "
                + "FROM customer c RIGHT JOIN appointment a " 
                + "ON c.customerId = a.customerId WHERE YEARWEEK(`start`, 0) = YEARWEEK(?, 0) AND contact = ? ORDER BY start");
            
            LocalDate d = LocalDate.now();
            prevWeekData = FXCollections.observableArrayList();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat tf = new SimpleDateFormat("HH:mm");
           
            if(prevNextButtonCount == 0)
                ps.setString(1, LocalDate.now().toString());
            else if(prevNextButtonCount == -1)
                ps.setString(1, previousWeekDate.toString());
            
            ps.setString(2, user);
       
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                String appID = rs.getString("appointmentId");
                String title = rs.getString("title");
                String customerName = rs.getString("customerName");
                String location = rs.getString("location");
                String description = rs.getString("description");
               
                
                Timestamp t = rs.getTimestamp("start");
                Timestamp et = rs.getTimestamp("end");

                
                String tableTime = getTime(t);
                String tableEnd = getTime(et);
                
                ZonedDateTime newzdtStart = t.toLocalDateTime().atZone(ZoneId.of("UTC"));

                ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId); 
                
                String newTDate = newLocalStart.toLocalDate().toString();
                
                prevWeekData.add(new schedule(appID, title, customerName, location, description, newTDate, tableTime, tableEnd));

            }
            setTableData(prevWeekData);
        
            
        }
        }catch (SQLException e){
                System.out.println("Previous Appointments: Something went wrong.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
        }
    }
    
    
    
    
    
    
    /**    Next Button   ***************************************************************************************************************************************
     **   Depending on whether the month or week toggle button is selected, this button either shows the next month of appointments or week of appointments. ***
     ***********************************************************************************************************************************************************/
    @FXML
    void nextButtonAction(ActionEvent event){
        DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        nextWeekDate = LocalDate.now().plus(1, ChronoUnit.WEEKS);
        
        
        //nextMonthDate = LocalDate.parse(appointmentTableView.getItems().get(0).getDate(), DATE_FORMAT).plus(1, ChronoUnit.MONTHS);
        
        try{
            conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
            System.out.println("Upcoming Appointments: Connected to database : Success!");
        if(monthToggleButton.isSelected()){
             System.out.println("Month: " + monthNum + " Year: " + year);
           
            
            monthNum++;
            
            if(monthNum < 1){
                monthNum = 12;
                year--;
            }
            if(monthNum > 12){
                monthNum = 1;
                year++;
            }
            System.out.println("Month: " + monthNum + " Year: " + year);
            
            
            currentLabel.setText(months[monthNum]);
            nextMonthData = FXCollections.observableArrayList();

            
            /* ps = conn.prepareStatement("SELECT appointmentId, title, customerName, location, description, start, end "
            + "FROM customer c RIGHT JOIN appointment a "
            + "ON c.customerId = a.customerId WHERE MONTH(start) = ? AND YEAR(start) = ? AND contact = ? ORDER BY start");*/
            
            
            
             //filter appointments
                masterData.stream().forEach((s) -> {
                    LocalDate today = LocalDate.now();
                    
                    LocalDate appDate = LocalDate.parse(s.getDate(),dateFormat);
                    if (appDate.getMonthValue() == monthNum && appDate.getYear() == today.getYear()) {
                        nextMonthData.add(s);
                    }
                });
            
                /*    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                DateFormat tf = new SimpleDateFormat("HH:mm");
                
                ps.setInt(1, monthNum);
                ps.setInt(2, year);
                ps.setString(3, user);
                
                ResultSet rs = ps.executeQuery();
                
                while(rs.next()){
                String appID = rs.getString("appointmentId");
                String title = rs.getString("title");
                String customerName = rs.getString("customerName");
                String location = rs.getString("location");
                String description = rs.getString("description");
                
                
                Timestamp t = rs.getTimestamp("start");
                Timestamp et = rs.getTimestamp("end");
                
                
                String tableTime = getTime(t);
                String tableEnd = getTime(et);
                
                ZonedDateTime newzdtStart = t.toLocalDateTime().atZone(ZoneId.of("UTC"));
                
                ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId);
                
                String newTDate = newLocalStart.toLocalDate().toString();
                
                nextMonthData.add(new schedule(appID, title, customerName, location, description, newTDate, tableTime, tableEnd));
                
                }*/
            setTableData(nextMonthData);
            
            
        } else if(weekToggleButton.isSelected()){
            
            prevNextButtonCount++;
            
                switch (prevNextButtonCount) {
                    case 0:
                        currentLabel.setText("Current Week");
                        previousButton.setDisable(false);
                        nextButton.setDisable(false);
                        break;
                    case -1:
                        currentLabel.setText("Previous Week");
                        previousButton.setDisable(true);
                        nextButton.setDisable(false);
                        break;
                    case 1:
                        currentLabel.setText("Next Week");
                        previousButton.setDisable(false);
                        nextButton.setDisable(true);
                        break;
                    default:
                        break;
                }
            
            ps = conn.prepareStatement("SELECT appointmentId, title, customerName, location, description, start, end "
                + "FROM customer c RIGHT JOIN appointment a " 
                + "ON c.customerId = a.customerId WHERE YEARWEEK(`start`, 0) = YEARWEEK(?, 0) AND contact = ? ORDER BY start");
            
            LocalDate d = LocalDate.now();
            nextWeekData = FXCollections.observableArrayList();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            DateFormat tf = new SimpleDateFormat("HH:mm");
           
           if(prevNextButtonCount == 0)
               ps.setString(1, LocalDate.now().toString());
           else if(prevNextButtonCount == 1)
                ps.setString(1, nextWeekDate.toString());
           
            ps.setString(2, user);
       
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                String appID = rs.getString("appointmentId");
                String title = rs.getString("title");
                String customerName = rs.getString("customerName");
                String location = rs.getString("location");
                String description = rs.getString("description");
                String date = df.format(rs.getDate("start"));
                String time = tf.format(rs.getTime("start"));
                
                Timestamp t = rs.getTimestamp("start");
                Timestamp et = rs.getTimestamp("end");

                
                String tableTime = getTime(t);
                String tableEnd = getTime(et);
                
                ZonedDateTime newzdtStart = t.toLocalDateTime().atZone(ZoneId.of("UTC"));

                ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId); 
                
                String newTDate = newLocalStart.toLocalDate().toString();
                
                nextWeekData.add(new schedule(appID, title, customerName, location, description, newTDate, tableTime, tableEnd));

            }
            setTableData(nextWeekData);
        
            
        }
        }catch (SQLException e){
                System.out.println("Upcoming Appointments: Something went wrong.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
        }
    }
    
    
    
    
    
    
    
    
    
    
    
     /* <Setter and Getter Methods> */
    
    
    
     /** Setter Method for variable ZoneId zId    ****
     *                                            ****
     * @param z                                   ****
     *************************************************/
    @FXML
    void setZoneId(ZoneId z){
        zId = z;
    }
    
    
    
    
    
    
    /** Setter Method for Greeting Label   ****
     *                                     ****
     * @param input                        ****
     ******************************************/
    @FXML
    void setGreetingLabel(String input){
        greetingLabel.setText("Hello, " + input);
    }
    
    
    
    
    
    
    /** Setter Method for ObservableList masterData   ****
     *                                                ****
     * @param data                                    ****
     *****************************************************/
    @FXML void setMasterData(ObservableList<schedule> data){
        masterData = data;
        Collections.sort(masterData, (m1, m2) -> m1.getDate().compareTo(m2.getDate()));
    }
    
    
    
    
    
    
    /** Getter Method for converting time from military to civilian time   ****
     *                                                                     ****
     * @return tableTime                                                   ****
     **************************************************************************/
    @FXML
    String getTime(Timestamp t){
        Timestamp  tableStart = t;
                    //ZoneId newzid = ZoneId.systemDefault();
               
                    tableStart.getTime();
                    
                    
                    
                    ZonedDateTime newzdtStart = tableStart.toLocalDateTime().atZone(ZoneId.of("UTC"));

                    ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId);
                    
                    
                    String date = newLocalStart.toLocalDate().toString();
                    String time = newLocalStart.toLocalTime().toString();
                    
                    String[] timeSplit = new String[1];
                    timeSplit = time.split(":");
                    
                    System.out.println(timeSplit[0]);
                    System.out.println(timeSplit[1]);
                    
                    String tableTime = "";

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
 
    
    
    
    
    /** Setter Method for variable user    ****
     *                                     ****
     * @param s                            ****
     ******************************************/
       @FXML void setUser(String s){
        user = s;
    }
       
       
       
    /** Getter Method for variable user  ****
     *                                   ****
     * @return user                      ****
     ****************************************/   
    @FXML String getUser(){
        return user;
    }
    
    
    
    
    /** Setter Method for Login Controller logCon    ****
     *                                               ****
     * @param l                                      ****
     ****************************************************/
    @FXML void setLoginController(LoginController l){
        logCon = l;
    }
    
    
    
    
    /** Setter Method for variable password    ****
     *                                         ****
     * @param s                                ****
     **********************************************/
    @FXML void setUserPass(String s){
        password = s;
    }
    
    
    
    
    /** Setter Method for Table View appointmentTableView    ****
     *                                                       ****
     * @param data                                           ****
     ************************************************************/
    @FXML void setTableData(ObservableList<schedule> data){
        appointmentTableView.setItems(data);
    }
    
    
    
    
    /** Setter Method for Month/Week label text    ****
     *                                             ****
     * @param input                                ****
     **************************************************/
    @FXML
    void setCurrentLabel(String input){
        currentLabel.setText(input);
    }
    
}

