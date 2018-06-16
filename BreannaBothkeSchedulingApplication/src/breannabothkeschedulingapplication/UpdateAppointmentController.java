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
import static java.time.temporal.ChronoUnit.MINUTES;
import java.util.Collections;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Admin
 */
public class UpdateAppointmentController {
    
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
    private UpdateAppointmentController updateApp;
    private LoginController logCon;
    private LocalTime scheduleTime;
    private LocalTime endTime;
    private String[] selectedTimeItem = new String[3];
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm"), ldtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"), dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateFormat df = new SimpleDateFormat("MM/dd/yyyy"), tf = new SimpleDateFormat("HH:mm");
    private ObservableList<schedule> data;
    private schedule selectedApp;
    private int index;
    
    
    
    
    
             /* <Button Actions> */
    
    
     /**    Cancel Button   **********************************************************
     **   Cancels Update Appointment Screen and returns to the Appointments screen ***
     *********************************************************************************/   
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
                    root1 = FXMLLoader.load(getClass().getResource("updateAppointment.fxml"));


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
                    
                    
                //filter appointments
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
    
    
    
    
    
    
    
    /**    Update Button    **************************************************
     **   Updates/modifies existing appointment with the new data.         ***
     *************************************************************************/ 
    @FXML
    void updateButtonAction(ActionEvent event) throws IOException{
        
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
            System.out.println("Update Appointment: Connected to database : Success!");
           

            if(datePicker.getValue() == null)
                errorLabel.setText("Please enter in a date.");
          
            else if(customerName != null && datePicker.getValue().isAfter(LocalDate.now(zId).minusDays(1)) && title != null &&
                location != null && tmPart != null && description != null && appointmentLength.getSelectedToggle() != null 
                && !datePicker.getValue().getDayOfWeek().equals(SATURDAY) && !datePicker.getValue().getDayOfWeek().equals(SUNDAY) && datePicker.getValue() != null){
                
                
                
                
            errorLabel.setText("");
            
            
            start = LocalDateTime.of(datePicker.getValue(), tmPart);
            
            startEnd = LocalDateTime.of(datePicker.getValue(), endTime);            
            if(location.equalsIgnoreCase("Phoenix, Arizona"))
                locationZone = "America/Phoenix";
            else if(location.equalsIgnoreCase("New York, New York"))
                locationZone = "America/New_York";
            else if(location.equalsIgnoreCase("London, England"))
                locationZone = "Europe/London";
            
           
             //Grab the list of appointments from observablelist appointmentData and see if there are overlaping appointments.
            for(int i = 0; i < masterData.size();i++){
                String tableID = masterData.get(i).getID();
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
                
            
                if(!tableID.equals(selectedApp.getID()) && tableTime.equals(newAppTime) && tableDate.equals(newAppDate) && locationComboBox.getSelectionModel().getSelectedItem().equals(tableLocation)){
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
                    ps2 = conn.prepareStatement("UPDATE appointment SET customerId = ?, title = ?, description = ?,"
                            + " location = ?, contact = ?, start = ?, end = ?, url = ?, createdBy = ?, lastUpdateBy = ?"
                            + " WHERE appointmentId = ?");

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
                    ps2.setInt(11, Integer.parseInt(selectedApp.getID()));


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
                        
                        masterData.remove(selectedApp);
                        
                        masterData.add(new schedule(appID, tableTitle, tableCustName, tableLocation, tableDescription, newTDate, tableTime, tableEnd));
                        
                        
                        //Lambda that filters through appointments in masterData and adds appointments with Today's current month and year to appointmentData.
                        masterData.stream().forEach((s) -> {
                            LocalDate today = LocalDate.now();

                            LocalDate appDate = LocalDate.parse(s.getDate(),dateFormat);
                            if (appDate.getMonth() == today.getMonth() && appDate.getYear() == today.getYear()) 
                                    appointmentData.add(s);
                            
                                    //Lambda that sorts elements by date
                                Collections.sort(appointmentData, (a1, a2) -> a1.getDate().compareTo(a2.getDate()));
                            
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
            
        }else if(datePicker.getValue().getDayOfWeek().equals(SATURDAY)) {
            
            errorLabel.setText("You cannot schedule an appointment on a Saturday.");
        }else if(datePicker.getValue().getDayOfWeek().equals(SUNDAY)){
            errorLabel.setText("You cannot schedule an appointment on a Sunday.");
        }else if(datePicker.getValue() == null){
            
            errorLabel.setText("Please enter in a date.");
        }
        else if(appointmentLength.getSelectedToggle() == null)
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
            newCust.setUpdateAppointmentController(updateApp);
                     
            //open newCustomer FXML file in new window
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (SQLException e){
            System.out.println("Failed to Connect to Database.");
                System.out.println("SQLException: "+e.getMessage());
                System.out.println("SQLState: "+e.getSQLState());
		System.out.println("VendorError: "+e.getErrorCode());
        }
        
        
    }

    
    
    
    
    /**    15 Minute Toggle Button   ***************************************************
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

    
    
    
    
    /**    30 Minute Toggle Button   ***************************************************
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

    
    
    
    
    /**    45 Minute Toggle Button   ***************************************************
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
        customerNameChoiceBox.getSelectionModel().select(selectedApp.getCustomerName());

    }    
 
    
    
    
     /** Getter Method for Customer Name Choice Box values    ****
     *                                                        ****
     *  @return customerNameChoiceBox.getItems()              ****
     *************************************************************/

    @FXML ObservableList getChoiceBoxData(){
        return customerNameChoiceBox.getItems();
    }
    
   
    
    
    
    /**         Setter Method for DatePicker Text Area                          ****
     *  Sets the text fields text to the selected appointments date.            ****
     *                                                                          ****
     *******************************************************************************/
    @FXML void setDatePicker(){
        LocalDate date = LocalDate.parse(selectedApp.getDate(), dateFormat);
        datePicker.setValue(date);
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
            new time("2:00 pm", "14:00"),
            new time("2:30 pm", "14:30"),
            new time("3:00 pm", "15:00"),
            new time("3:30 pm", "15:30"),
            new time("4:00 pm", "16:00"),
            new time("4:30 pm", "16:30"),
            new time("5:00 pm", "17:00"));
        
        
        LocalTime newTime = LocalTime.parse(reverseTime(selectedApp.getTime()), dtf);
        LocalDate newDate = LocalDate.parse(selectedApp.getDate(), dateFormat);
        
        String monthSt = "";
        String daySt = "";
        String hourSt = "";
        String minuteSt = "";
        
        if(newDate.getMonthValue() < 10)
            monthSt = "0" + newDate.getMonthValue();
        else
            monthSt = Integer.toString(newDate.getMonthValue());
        if(newDate.getDayOfMonth() < 10)
            daySt = "0" + newDate.getDayOfMonth();
        else
            daySt = Integer.toString(newDate.getDayOfMonth());
        if(newTime.getHour() < 10)
            hourSt = "0" + newTime.getHour();
        else
            hourSt = Integer.toString(newTime.getHour());
        if(newTime.getMinute() == 0)
            minuteSt = "0" + newTime.getMinute();
        else
            minuteSt = Integer.toString(newTime.getMinute());
        
        String st = newDate.getYear() + "-" + monthSt + "-" + daySt + " " + hourSt + ":" + minuteSt;
        
        
        LocalDateTime start = LocalDateTime.parse(st, ldtf);
        ZonedDateTime newzdtStart = start.atZone(zId);
        String locationZone = "";
        ZonedDateTime newLocalStart;

        String appLocation = selectedApp.getLocation();
         if(appLocation.equalsIgnoreCase("Phoenix, Arizona"))
                locationZone = "America/Phoenix";
            else if(appLocation.equalsIgnoreCase("New York, New York"))
                locationZone = "America/New_York";
            else if(appLocation.equalsIgnoreCase("London, England"))
                locationZone = "Europe/London";
         
         if(appLocation.equalsIgnoreCase("Online")){
            newLocalStart = newzdtStart.withZoneSameInstant(zId);
         }else{
            newLocalStart = newzdtStart.withZoneSameInstant(ZoneId.of(locationZone));
         }
         
         String timeStr = "";
         String minSt = "";
         
         if(newLocalStart.getMinute() == 0)
             minSt = "0" + newLocalStart.getMinute();
         else
             minSt = Integer.toString(newLocalStart.getMinute());
         
         
         
         if(newLocalStart.getHour() < 10)
            timeStr = "0" + newLocalStart.getHour() + ":" + minSt;        
         else
             timeStr = newLocalStart.getHour() + ":" + minSt;
         
         
        String choiceValue = "";
        for(int i = 0; i < time.size(); i++){
            timeComboBox.getItems().add(time.get(i).getNormal());
            
            if(time.get(i).getMilitary().equals(timeStr))
                choiceValue = time.get(i).getNormal();
       
        } 
        timeComboBox.getSelectionModel().select(choiceValue);
        System.out.println(timeStr);
        
        
        
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
        locationComboBox.getSelectionModel().select(selectedApp.getLocation());
    }
    
    
    
    
    
    /**         Setter Method for Title Text Area                         ****
     *  Sets the text fields text to the selected appointments title.     ****
     *                                                                    ****
     *************************************************************************/
    @FXML void setTitleTextField(){
        titleTextField.setText(selectedApp.getTitle());
    }
    
    
    
    
    
    
    /**         Setter Method for Description Text Area                         ****
     *  Sets the text fields text to the selected appointments description.     ****
     *                                                                          ****
     *******************************************************************************/
    @FXML void setDescriptionTextArea(){
        descriptionTextArea.setText(selectedApp.getDescription());
    }
    
    
    
    
    
    
    
    /** Setter Method where it selects one of the appointment duration buttons based on how many minutes are between start time and end time.    ****
     *                                                                                                                                           ****
     *                                                                                                                                           ****
     ************************************************************************************************************************************************/
    @FXML void setMinuteButton(){
        String startSt = selectedApp.getTime();
        String endSt = selectedApp.getEndTime();
         
        String[] splitStart = new String[1];
        String[] splitEnd = new String[1];
        
        String[] finalStart = new String[1];
        String[] finalEnd = new String[1];
        
        //startStringToTime = splitStart[1].split(" ");
        //endStringToTime = splitEnd[1].split(" ");
        
        splitStart = startSt.split(":");
        splitEnd = endSt.split(":");
        
        String startHr = getHour(splitStart[0]);
        String endHr = getHour(splitEnd[0]);

        String startString = startHr + ":" + splitStart[1];
        String endString = endHr + ":" + splitEnd[1];
        
        finalStart = startString.split(" ");
        finalEnd = endString.split(" ");
        
        LocalTime start = LocalTime.parse(finalStart[0], dtf);
        LocalTime end = LocalTime.parse(finalEnd[0], dtf);
        
        double diff = 0;
        diff = MINUTES.between(start, end);
        
        if(diff == 15){
            //button15.setSelected(true);
            button15.fire();
        }
        else if (diff == 30){
            //button30.setSelected(true);
            button30.fire();
        }
        else if (diff == 45){
            //button45.setSelected(true);
            button45.fire();
        }
        
        
    }
    
    
    
    
    
    /** Setter Method for String variable user    ****
     *                                            ****
     * @param input                               ****
     *************************************************/
    @FXML void setUser(String input){
        user = input;
    }
 
    
    
    
    
    /** Setter Method for schedule variable selectedApp     ****
     *                                                      ****
     * @param app                                           ****
     ***********************************************************/
    @FXML void setSelectedApp(schedule app){
        selectedApp = app;
    }
    
    
    
    
    
    /** Setter Method for integer variable index    ****
     *                                          ****
     * @param i                                 ****
     ***********************************************/
    @FXML void setTableIndex(int i){
        index = i;
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
    
    
    
    
     /** Setter Method for AddAppointment Controller addApp     ****
     *                                                          ****
     * @param a                                                 ****
     ***************************************************************/
    @FXML void setAddAppointmentController(AddAppointmentController a){
        addApp = a;
    }
    
    
    
    
    
    
     /** Setter Method for UpdateAppointment Controller updateApp  ****
     *                                                             ****
     * @param u                                                    ****
     ******************************************************************/

    @FXML void setUpdateAppointmentController(UpdateAppointmentController u){
        updateApp = u;
    }
    
    
    
    
     /** Setter Method for Login Controller logCon      ****
     *                                                  ****
     * @param l                                         ****
     *******************************************************/
    @FXML void setLoginController(LoginController l){
        logCon = l;
    }
   
    
    
    
     /** Setter Method that sets the appointment duration buttons to unselected.   ****
     *                                                                             ****
     *                                                                             ****
     **********************************************************************************/

    @FXML void unselectButtons(){
        button15.setSelected(false);
        button30.setSelected(false);
        button45.setSelected(false);
    }
    
    
    
    
    
    
    /** Getter Method that accepts a String variable and adds a 0 at the beginning if it contains a number under 10.           ****
     *   This allows the time string to be parsed as a Local time so I can either compare or add/subtract two time variables.  ****
     * @param t                                                                                                                ****
     * @return result                                                                                                          ****
     ******************************************************************************************************************************/

    @FXML
    String getHour(String data){
        String result = "";
        int num = Integer.parseInt(data);
        
        if(num < 10)
            result = "0" + num;
        else
            result = Integer.toString(num);
        
        return result;
        
        
    }
    
    
    
    
    
    
     /** Getter Method that accepts a String variable and converts it to military time in the time zone of the location the appointment is at.  ****
     *                                                                                                                                          ****
     * @param t                                                                                                                                 ****
     * @return switchString                                                                                                                     ****
     ***********************************************************************************************************************************************/
    @FXML
    String reverseTime(String t){
        
        
        String[] appTime = new String[1];
        appTime = t.split(" ");
        String[] splitTime = new String[1];
        splitTime = appTime[0].split(":");
        
        
        String switchString = "";
        
        if(appTime[1].equals("am")){
            
            switch (splitTime[0]){
                case "12":
                    switchString = "24:" + splitTime[1];
                    break;
                case "1":
                    switchString = "01:" + splitTime[1];
                    break;
                case "2":
                    switchString = "02:" + splitTime[1];
                    break;
                case "3":
                    switchString = "03:" + splitTime[1];
                    break;
                case "4":
                    switchString = "04:" + splitTime[1];
                    break;
                case "5":
                    switchString = "05:" + splitTime[1];
                    break;
                case "6":
                    switchString = "06:" + splitTime[1];
                    break;
                case "7":
                    switchString = "07:" + splitTime[1];
                    break;
                case "8":
                    switchString = "08:" + splitTime[1];
                    break;
                case "9":
                    switchString = "09:" + splitTime[1];
                    break;
                case "10":
                    switchString = "10:" + splitTime[1];
                    break;
                case "11":
                    switchString = "11:" + splitTime[1];
                    break;
                default:
                    
                    
            }
            
        }else if (appTime[1].equals("pm")){
            
            switch (splitTime[0]){
                case "12":
                    switchString = "12:" + splitTime[1];
                    break;
                case "1":
                    switchString = "13:" + splitTime[1];
                    break;
                case "2":
                    switchString = "14:" + splitTime[1];
                    break;
                case "3":
                    switchString = "15:" + splitTime[1];
                    break;
                case "4":
                    switchString = "16:" + splitTime[1];
                    break;
                case "5":
                    switchString = "17:" + splitTime[1];
                    break;
                case "6":
                    switchString = "18:" + splitTime[1];
                    break;
                case "7":
                    switchString = "19:" + splitTime[1];
                    break;
                case "8":
                    switchString = "20:" + splitTime[1];
                    break;
                case "9":
                    switchString = "21:" + splitTime[1];
                    break;
                case "10":
                    switchString = "22:" + splitTime[1];
                    break;
                case "11":
                    switchString = "23:" + splitTime[1];
                    break;
                default:
            }
        }
        
        return switchString;
        
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
    
}
