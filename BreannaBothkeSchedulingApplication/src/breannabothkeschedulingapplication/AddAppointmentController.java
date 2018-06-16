/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package breannabothkeschedulingapplication;

import breannabothkeschedulingapplication.Models.schedule;
import breannabothkeschedulingapplication.Models.time;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddAppointmentController {

    @FXML
    private Label createAppointmentLabel, errorLabel,  titleLabel, timeLabel, descriptionLabel, dateLabel, lengthLabel, locationLabel, customerNameLabel;

    @FXML
    private ToggleButton button15, button30, button45;

    @FXML
    private ToggleGroup appointmentLength;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextField locationTextField, titleTextField;

    @FXML
    private ChoiceBox<String> customerNameChoiceBox, timeChoiceBox;

    @FXML
    private Button addCustomerButton, createButton, cancelButton;

    @FXML
    private DatePicker datePicker;
    
    @FXML
    private ComboBox<String> timeComboBox, locationComboBox;


    private ZoneId zId;
    private String user;
    private Connection conn;
    private ObservableList custNameData, timeList;
    private ObservableList<time> time;
    private ObservableList<schedule> masterData;
    private final ObservableList<schedule> appointmentData = FXCollections.observableArrayList();
    private AddAppointmentController addApp;
    private LoginController logCon;
    private LocalTime scheduleTime, endTime;
    private String[] selectedTimeItem = new String[3];
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm"), ldtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"), dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateFormat df = new SimpleDateFormat("MM/dd/yyyy"), tf = new SimpleDateFormat("HH:mm");
    private ObservableList<schedule> data;




     /******     Initializer for Add Appointment Controller                           ************************************
     *    Sets the Add Appointment Controller addApp and populates the Location Combo box when the page launches.     ****
     *                                                                                                                ****
     *********************************************************************************************************************/
    @FXML void initialize(){
        
        setAddAppointmentController(this);
        setLocationComboBox();
        
    }
    
    
            /* <Button Actions> */
    
    
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
                    
                
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("appointments.fxml"));
                    Stage stage, stage2;
                    Parent root1, root = loader.load();
                    root1 = FXMLLoader.load(getClass().getResource("newAppointment.fxml"));


                    //gets reference to the button's stage
                    stage=(Stage)cancelButton.getScene().getWindow();

                    //load up Other FXML Document
                    stage2 = new Stage();
                    
                    
                    
                    
                    AppointmentController appCon = loader.getController();
            
                    appCon.setLoginController(logCon);
                    appCon.setUser(user);
                    appCon.setZoneId(zId);
                    appCon.setGreetingLabel(user);
                    appCon.setCurrentLabel(LocalDate.now().getMonth().toString());
                    appCon.setMasterData(masterData);
                    
                    
                //Lambda that filters through appointments in masterData and adds appointments with Today's current month and year to appointmentData.
                masterData.stream().forEach((s) -> {
                    LocalDate today = LocalDate.now();
                    LocalDate appDate = LocalDate.parse(s.getDate(),dateFormat);
                    if (appDate.getMonth() == today.getMonth() && appDate.getYear() == today.getYear()) {
                        appointmentData.add(s);
                    }
                });
             
                    appCon.setTableData(appointmentData);
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    Scene scene = new Scene(root1);
                    Scene scene2 = new Scene(root);
                    stage.setScene(scene);
                    stage2.setScene(scene2);
                    stage.hide();
                    stage2.show();
                    
                } else {
                // ... user chose CANCEL or closed the dialog

                       }
            
    }
    
    
    
    
    
     /**    Create Button   **************************************************
     **   Creates a new appointment with the current user.                 ***
     *************************************************************************/ 
    @FXML
    void createButtonAction(ActionEvent event) throws Exception{
        
        String customerId = "";
        String customerName = customerNameChoiceBox.getSelectionModel().getSelectedItem();
        LocalDate date = datePicker.getValue();
        String title = titleTextField.getText();
        String location = locationComboBox.getSelectionModel().getSelectedItem();
        String locationZone = "";
        LocalTime tmPart;
        tmPart = scheduleTime;
        String description = descriptionTextArea.getText();
        LocalDateTime start;
        LocalDateTime startEnd;
        String url = " ";
        PreparedStatement ps, ps2;
        boolean found = false;
        
        try{
            conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
            System.out.println("New Appointment: Connected to database : Success!");
         
            if(datePicker.getValue() == null)
                errorLabel.setText("Please enter in a date.");
          
            else if(customerName != null && datePicker.getValue().isAfter(LocalDate.now(zId).minusDays(1)) && title != null &&
                location != null && tmPart != null && description != null && appointmentLength.getSelectedToggle() != null
                && !datePicker.getValue().getDayOfWeek().equals(SATURDAY) && !datePicker.getValue().getDayOfWeek().equals(SUNDAY) && datePicker.getValue() != null){
                
                start = LocalDateTime.of(date, tmPart);
                startEnd = LocalDateTime.of(date, endTime);
                errorLabel.setText("");

                 if(location.equalsIgnoreCase("Phoenix, Arizona"))
                    locationZone = "America/Phoenix";
                else if(location.equalsIgnoreCase("New York, New York"))
                    locationZone = "America/New_York";
                else if(location.equalsIgnoreCase("London, England"))
                    locationZone = "Europe/London";


                 //Grab the list of appointments from observablelist appointmentData and see if there are overlaping appointments.
                for(int i = 0; i < masterData.size();i++){
                    String tableTime = masterData.get(i).getTime();
                    String tableTimeEnd = masterData.get(i).getEndTime();
                    String tableLocation = masterData.get(i).getLocation();
                    String tableDate = masterData.get(i).getDate();

                    LocalDateTime stTime;
                    LocalDateTime enTime;

                    String[] splitPT = new String[1];
                    String newAppTime;
                    String newAppEndTime;
                    String newAppDate;



                    if(location.equalsIgnoreCase("online")){
                            stTime = ZonedDateTime.of(start, zId).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
                            enTime = ZonedDateTime.of(startEnd, zId).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

                        }else{
                            stTime = ZonedDateTime.of(start, ZoneId.of(locationZone)).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
                            enTime = ZonedDateTime.of(startEnd, ZoneId.of(locationZone)).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
                        }

                    newAppTime = getLocalTime(stTime);
                    newAppEndTime = getLocalTime(enTime);

                    ZonedDateTime newzdtStart = stTime.atZone(ZoneId.of("UTC"));

                    ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId); 

                    newAppDate = newLocalStart.toLocalDate().toString();


                    if(tableTime.equals(newAppTime) && tableDate.equals(newAppDate) && locationComboBox.getSelectionModel().getSelectedItem().equals(tableLocation)){
                        found = true;
                        break;
                    }else{
                        found = false;
                    }
                }
                if(found){
                    System.out.println("Cannot overlap appointments.");
                    errorLabel.setText("Cannot overlap appointments.");
                }else{
                    //Grab Customer ID
                        ps = conn.prepareStatement("SELECT customerId FROM customer WHERE customerName = ?");
                        ps.setString(1, customerName);

                        ResultSet rs = ps.executeQuery();

                        if(rs.next())
                            customerId = Integer.toString(rs.getInt("customerId"));


                        //Insert New Appointment
                        ps2 = conn.prepareStatement("INSERT INTO appointment (customerId, title, description,"
                                + " location, contact, start, end, url, createDate, createdBy, lastUpdateBy)"
                                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURDATE(), ?, ?)");

                        ps2.setString(1, customerId);
                        ps2.setString(2, title);
                        ps2.setString(3, description);
                        ps2.setString(4, location);
                        ps2.setString(5, user);



                        if(location.equalsIgnoreCase("online")){
                            ps2.setString(6, ZonedDateTime.of(start, zId).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().toString());
                            ps2.setString(7, ZonedDateTime.of(startEnd, zId).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().toString());
                        }else{
                            ps2.setString(6, ZonedDateTime.of(start, ZoneId.of(locationZone)).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().toString());
                            ps2.setString(7, ZonedDateTime.of(startEnd, ZoneId.of(locationZone)).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().toString());
                        }
                        ps2.setString(8, url);
                        ps2.setString(9, user);
                        ps2.setString(10, user);


                        int result = ps2.executeUpdate();

                        if(result == 1){
                            System.out.println("Appointment Created.");
                            ps = conn.prepareStatement("SELECT appointmentId, (SELECT customerName FROM customer WHERE customerId = ?) AS customerName, title, location, description,"
                                    + " start, end FROM appointment WHERE customerId = ? AND contact = ? AND start = ? AND end = ?");
                            ps.setString(1, customerId);
                            ps.setString(2, customerId);
                            ps.setString(3, user);
                            if(location.equalsIgnoreCase("online")){
                                ps.setString(4, ZonedDateTime.of(start, zId).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().toString());
                                ps.setString(5, ZonedDateTime.of(startEnd, zId).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().toString());

                            }else{
                                ps.setString(4, ZonedDateTime.of(start, ZoneId.of(locationZone)).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().toString());
                                ps.setString(5, ZonedDateTime.of(startEnd, ZoneId.of(locationZone)).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().toString());
                            }
                            ResultSet appResult = ps.executeQuery();

                            if(appResult.next()){

                            String appID = appResult.getString("appointmentId");
                            String tableTitle = appResult.getString("title");
                            String tableCustName = appResult.getString("customerName");
                            String tableLocation = appResult.getString("location");
                            String tableDescription = appResult.getString("description");

                            Timestamp t = appResult.getTimestamp("start");
                            Timestamp et = appResult.getTimestamp("end");


                            String tableTime = getTime(t);
                            String tableEnd = getTime(et);

                            ZonedDateTime newzdtStart = t.toLocalDateTime().atZone(ZoneId.of("UTC"));

                            ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId); 

                            String newTDate = newLocalStart.toLocalDate().toString();
                            masterData.add(new schedule(appID, tableTitle, tableCustName, tableLocation, tableDescription, newTDate, tableTime, tableEnd));


                            //Lambda that filters through appointments in masterData and adds appointments with Today's current month and year to appointmentData.
                            masterData.stream().forEach((s) -> {
                                LocalDate today = LocalDate.now();

                                LocalDate appDate = LocalDate.parse(s.getDate(),dateFormat);
                                if (appDate.getMonth() == today.getMonth() && appDate.getYear() == today.getYear()) {
                                    appointmentData.add(s);

                                    //Lambda that sorts elements by date
                                    Collections.sort(appointmentData, (a1, a2) -> a1.getDate().compareTo(a2.getDate()));
                                }
                            });

                            Stage stage, stage2;
                            Parent root1, root;


                                //gets reference to the button's stage
                            stage=(Stage)cancelButton.getScene().getWindow();
                            stage2 = new Stage();



                                //load up Other FXML Document
                            root1 = FXMLLoader.load(getClass().getResource("newAppointment.fxml"));
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("appointments.fxml"));
                            root = loader.load();

                            AppointmentController appCon = loader.getController();
                            appCon.setLoginController(logCon);
                            appCon.setUser(user);
                            appCon.setZoneId(zId);
                            appCon.setGreetingLabel(user);
                            appCon.setCurrentLabel(LocalDate.now().getMonth().toString());




                            appCon.setTableData(appointmentData);
                            appCon.setMasterData(masterData);


                            Scene scene = new Scene(root1);
                            Scene scene2 = new Scene(root);
                            stage.setScene(scene);
                            stage2.setScene(scene2);
                            stage.hide();
                            stage2.show();
                            }
                        }
                    else{
                        System.out.println("Something went wrong: appointment was not created.");
                    }
            }
        }else if(datePicker.getValue().isBefore(LocalDate.now(zId))){
            errorLabel.setText("You cannot schedule an appointment with a past date.");
        }else if(datePicker.getValue().getDayOfWeek().equals(SATURDAY)){
            
            errorLabel.setText("You cannot schedule an appointment on a Saturday.");
            
        }else if(datePicker.getValue().getDayOfWeek().equals(SUNDAY)){
            errorLabel.setText("You cannot schedule an appointment on a Sunday.");
            
        }else if(appointmentLength.getSelectedToggle() == null)
            
            errorLabel.setText("Please set the duration of the appointment.");
        
        
        else{
            System.out.println("Fill in all the fields.");
            errorLabel.setText("Fill in all the fields.");
        }
        
    }catch (SQLException e){
            System.out.println("New Appointment: Error. Something went wrong.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
                }
        
}

    
    
    
     /**    Add Button   ***********************************************************
     **   Launches the New Customer window, where you can create a new customer. ***
     *******************************************************************************/ 
    @FXML
    void addCustomerButtonAction(ActionEvent event) throws IOException, SQLException {
        PreparedStatement ps;
        ObservableList countries= FXCollections.observableArrayList();
        
        try{
            conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
            if(!zId.equals(ZoneId.of("Europe/Berlin"))){
            System.out.println("Adding Customer: Connected to database : Success!");
            } else{
            
            System.out.println("Verbunden mit der Datenbank : Erfolg!");
            }
         //grab customerName from database
            ps = conn.prepareStatement("SELECT country from country");
            ResultSet rs = ps.executeQuery();
            
          
                //add customer names to observableList custName
                while(rs.next()){
                    String country = rs.getString("country");
                    countries.add(country);
                }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("newCustomer.fxml"));
            Parent root = fxmlLoader.load();
            
            NewCustomerController newCust = fxmlLoader.getController();
            newCust.setUser(user);
            newCust.setZoneId(zId);
            newCust.setCountryChoiceBox(countries);
            newCust.setAddAppointmentController(addApp);
                     
            //open newCustomer FXML file in new window
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (SQLException e){
            System.out.println("Something went wrong.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
        }
        
        
    }

    
    
     /**    15 Minute Toggle Button   **************************************************
     **   Sets the duration of the appointment being scheduled to 15 minutes.        ***
     ***********************************************************************************/ 
    @FXML
    void button15Action(ActionEvent event) throws ParseException {
        boolean timeIsEmpty = timeComboBox.getSelectionModel().isEmpty();
        if(timeIsEmpty == true){
            errorLabel.setText("Choose a Time.");
            System.out.println("You must choose a time.");
            button15.setSelected(false);
        } else{
          for(int i = 0; i < time.size();i++){
            if (i == timeComboBox.getSelectionModel().getSelectedIndex())
                scheduleTime = LocalTime.parse(time.get(i).getMilitary(), dtf);
            }
             
        endTime = scheduleTime.plus(15, ChronoUnit.MINUTES);
        System.out.println("Appointment starts at " + scheduleTime);
        System.out.println("Appointment ends at " + endTime);
        }
    }
    
    
    
    
    
     /**    30 Minute Toggle Button   **************************************************
     **   Sets the duration of the appointment being scheduled to 30 minutes.        ***
     ***********************************************************************************/
    @FXML
    void button30Action(ActionEvent event) {
        selectedTimeItem = timeComboBox.getSelectionModel().getSelectedItem().split("\\s+");
        boolean timeIsEmpty = timeComboBox.getSelectionModel().isEmpty();
        if(timeIsEmpty == true){
            errorLabel.setText("Choose a Time.");
            System.out.println("You must choose a time.");
            button30.setSelected(false);
        } else{
             for(int i = 0; i < time.size();i++){
                if (i == timeComboBox.getSelectionModel().getSelectedIndex())
                    scheduleTime = LocalTime.parse(time.get(i).getMilitary(), dtf);
            }
             
            endTime = scheduleTime.plus(30, ChronoUnit.MINUTES);
            System.out.println("Appointment starts at " + scheduleTime);
            System.out.println("Appointment ends at " + endTime);
        }
    }
    
    
    
    
     /**    45 Minute Toggle Button   **************************************************
     **   Sets the duration of the appointment being scheduled to 45 minutes.        ***
     ***********************************************************************************/
    @FXML
    void button45Action(ActionEvent event) {
        selectedTimeItem = timeComboBox.getSelectionModel().getSelectedItem().split("\\s+");
         boolean timeIsEmpty = timeComboBox.getSelectionModel().isEmpty();
        if(timeIsEmpty == true){
            errorLabel.setText("Choose a Time.");
            System.out.println("You must choose a time.");
            button45.setSelected(false);
        } else{
        //System.out.println("Appointment starts at " + selectedTimeItem[0] + " " + selectedTimeItem[1]);
        
            for(int i = 0; i < time.size();i++){
                if (i == timeComboBox.getSelectionModel().getSelectedIndex())
                    scheduleTime = LocalTime.parse(time.get(i).getMilitary(), dtf);
            }

            endTime = scheduleTime.plus(45, ChronoUnit.MINUTES);
            System.out.println("Appointment starts at " + scheduleTime);
            System.out.println("Appointment ends at " + endTime);
            
        }
    }

    
    
    
    
    
     /* <Setter and Getter Methods> */
    
    
    
     /** Setter Method for Customer Name Choice Box     ****
     *                                                  ****
     * @param data                                      ****
     *******************************************************/
    
    @FXML void setCustNameData(ObservableList data){
        custNameData = FXCollections.observableArrayList(data);
        
        //Lambda that sorts elements by name alphabetically
        Collections.sort(custNameData, (c1, c2) -> c1.toString().compareTo(c2.toString()));
        customerNameChoiceBox.setItems(custNameData);   
    }    
    
    
    
    
    
    
    
     /** Getter Method for Customer Name Choice Box values    ****
     *                                                        ****
     *  @return customerNameChoiceBox.getItems()              ****
     *************************************************************/
    @FXML ObservableList getChoiceBoxData(){
        return customerNameChoiceBox.getItems();
    }
    
    
   
     /**     Setter Method for Time Combo Box           ****
     *                                                  ****
     *                                                  ****
     *******************************************************/   
    @FXML void setTimeComboBox(){
        time = FXCollections.observableArrayList();
        
        time.addAll(
            new time("8:00 am", "08:00"),
            new time("8:30 am", "08:30"),
            new time("9:00 am", "09:00"),
            new time("9:30 am", "09:30"),
            new time("10:00 am", "10:00"),
            new time("10:30 am", "10:30"),
            new time("11:00 am", "11:00"),
            new time("11:30 am", "11:30"),
            new time("12:00 pm", "12:00"),
            new time("12:30 pm", "12:30"),
            new time("1:00 pm", "13:00"),
            new time("1:30 pm", "13:30"),
            new time("2:oo pm", "14:00"),
            new time("2:30 pm", "14:30"),
            new time("3:00 pm", "15:00"),
            new time("3:30 pm", "15:30"),
            new time("4:00 pm", "16:00"),
            new time("4:30 pm", "16:30"),
            new time("5:00 pm", "17:00"));
        
        for(int i = 0; i < time.size(); i++){
            timeComboBox.getItems().add(time.get(i).getNormal());
        }
        
         timeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                System.out.println(ov);
                System.out.println(t);
                System.out.println(t1);
                
                unselectButtons();
                
            }    
    });
    } 
  
    
    
    
     /** Setter Method for Location Combo Box           ****
     *                                                  ****
     *                                                  ****
     *******************************************************/
    @FXML void setLocationComboBox(){
        ObservableList locations = FXCollections.observableArrayList();
        
        locations.add("Phoenix, Arizona");
        locations.add("New York, New York");
        locations.add("London, England");
        locations.add("Online");
        locationComboBox.setItems(locations);
    }
    
    
    
    
    
     /** Setter Method for variable user                ****
     *                                                  ****
     * @param input                                     ****
     *******************************************************/
    @FXML void setUser(String input){
        user = input;
    }
    
    
    
    
     /** Setter Method for ObservableList masterData    ****
     *                                                  ****
     * @param data                                      ****
     *******************************************************/
    @FXML void setMasterData(ObservableList<schedule> data){
        masterData = data;
    }
    
   
    
    
     /** Setter Method for ZoneId variable zId          ****
     *                                                  ****
     * @param z                                         ****
     *******************************************************/
     @FXML
    void setZoneId(ZoneId z){
        zId = z;
    }
    
    
    
     /** Setter Method for pre-selecting appointment duration button    ****
     *                                                                  ****
     *                                                                  ****
     ***********************************************************************/
     @FXML void unselectButtons(){
        button15.setSelected(false);
        button30.setSelected(false);
        button45.setSelected(false);
    }
    
     
     
     
     /** Setter Method for AddAppointment Controller addApp     ****
     *                                                          ****
     * @param a                                                 ****
     ***************************************************************/
    @FXML void setAddAppointmentController(AddAppointmentController a){
        addApp = a;
    }
        
    
    
    
    
     /** Setter Method for Login Controller logCon      ****
     *                                                  ****
     * @param l                                         ****
     *******************************************************/
    @FXML void setLoginController(LoginController l){
        logCon = l;
    }
    
    
    
    
    
    
      
    
    
    
     /** Getter Method that accepts a timestamp and converts it to current time zone   ****
     *                                                                                 ****
     * @param t                                                                        ****
     **************************************************************************************/
     @FXML
    String getTime(Timestamp t){
        Timestamp  tableStart = t;
                    //ZoneId newzid = ZoneId.systemDefault();
               
                    tableStart.getTime();
                    
                    
                    
                    ZonedDateTime newzdtStart = tableStart.toLocalDateTime().atZone(ZoneId.of("UTC"));

                    ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId);
                    
                    
                    String date = newLocalStart.toLocalDate().toString();
                    String tm = newLocalStart.toLocalTime().toString();
                    
                    String[] timeSplit = new String[1];
                    timeSplit = tm.split(":");
                    
                    //System.out.println(timeSplit[0]);
                    //System.out.println(timeSplit[1]);
                    
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
    
    
    
    
    
    
    
    
    
     /** Getter Method that accepts a LocalDateTime variable and converts it to current time zone   ****
     *                                                                                              ****
     * @param t                                                                                     ****
     ***************************************************************************************************/
     @FXML
    String getLocalTime(LocalDateTime t){
        LocalDateTime  tableStart;
                    //ZoneId newzid = ZoneId.systemDefault();
                    int month = t.getMonthValue();
                    int day = t.getDayOfMonth();
                    int hour = t.getHour();
                    int minute = t.getMinute();
                    String mStr = "";
                    String dStr = "";
                    String hr = "";
                    String min = "";
                    
                    if(month < 10)
                        mStr = "0" + Integer.toString(month);
                    else
                        mStr = Integer.toString(month);
                    
                    if(day < 10)
                        dStr = "0" + Integer.toString(day);
                    else
                        dStr = Integer.toString(day);
                    
                    if(hour < 10)
                        hr = "0" + Integer.toString(hour);
                    else
                        hr = Integer.toString(hour);
                    
                    if(minute == 0)
                        min = "0" + Integer.toString(minute);
                    else
                        min = Integer.toString(minute);
                    
                    
                    
               
                    String st = t.getYear() + "-" + mStr + "-" + dStr + " " + hr + ":" + min;
                                        
                    
                    
                    tableStart = LocalDateTime.parse(st, ldtf);
                    
                    
                    
                    ZonedDateTime newzdtStart = tableStart.atZone(ZoneId.of("UTC"));

                    ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId);
                    
                    
                    String date = newLocalStart.toLocalDate().toString();
                    String time = newLocalStart.toLocalTime().toString();
                    
                    String[] timeSplit = new String[1];
                    timeSplit = time.split(":");
                    
                  //  System.out.println(timeSplit[0]);
                  //  System.out.println(timeSplit[1]);
                    
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
    
}
