/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package breannabothkeschedulingapplication;

import breannabothkeschedulingapplication.Models.schedule;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.MINUTES;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;



/**
 *
 * @author Admin
 */
public class LoginController {
    
    
     @FXML
    private Label loginLabel, usernameLabel, passwordLabel, errorLabel;


    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;
 
    @FXML
    private Separator divider;

    @FXML
    private Button loginButton, exitButton;

    private String username, pass;
    Statement st;
    private ObservableList<schedule> data, masterData;
    
    private final DateTimeFormatter  dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd"), dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private LoginController logCon;
    private Connection conn;
    private ZoneId zId;
    
    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;
    
    private final static Logger LOGGER = Logger.getLogger(ViewCustomersController.class.getName());

    
    
    
    
     /********     Initializer                   ***********
     *                                                  ****
     *                                                  ****
     * @throws java.lang.Exception
     *******************************************************/
    
      public void initialize() throws Exception{
        
       //sets LogCon variable to LoginController
       setLogCon(this);
       
       //Login Button is made the default button
       loginButton.setDefaultButton(true);
       
       //file variable is set to schedapp-log.txt
       File file = new File("..\\BreannaBothkeSchedulingApplication", "schedapp-log.txt");
       
       LOGGER.setLevel(Level.ALL);
       
       //if file does not exist, then file is created.
       if(!file.exists()){
           FileWriter writer = new FileWriter(file);
       }
       
       fileTxt = new FileHandler("schedapp-log.txt", true);

       //schedapp-log.txt is binded with LOGGER
       LOGGER.addHandler(fileTxt);
       SimpleFormatter formatter = new SimpleFormatter();
       fileTxt.setFormatter(formatter);
               
       

    }
      
      
      
    /* <Button Actions> */
      
      
      
     /*****    Login Button Action     ***************************
     *        Checks to see if username and password match    ****
     *        username and password combo in database.        ****
     *        If match is found, appointments.fxml is opened  ****
     *                                                        ****
     *************************************************************/  
    @FXML
    void loginButtonAction(ActionEvent event) throws Exception, ParseException {
        username = usernameTextField.getText();
        pass = passwordTextField.getText();
        PreparedStatement ps;
        
        try{
            
            //connection to database
            conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
            
            if(!zId.equals(ZoneId.of("Europe/Berlin"))){
            System.out.println("Login: Connected to database : Success!");
            } else{
            
            System.out.println("Anmeldung: Verbunden mit der Datenbank : Erfolg!");
            }

            //Checks if inputed username and password match any username and password combinations in the database
            ps = conn.prepareStatement("SELECT userName, password FROM user WHERE userName = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, pass);
            ResultSet result = ps.executeQuery();
            
            //if match is found, then proceed
            if(result.next()){
                errorLabel.setText("");
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("appointments.fxml"));
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("login.fxml"));
                Parent root = fxmlLoader.load();
                AppointmentController appCon = fxmlLoader.getController();
            
                appCon.setLoginController(logCon);
                String user = result.getString("userName");
                appCon.setUser(user);
                appCon.setUserPass(result.getString("password"));
                appCon.setZoneId(zId);
                appCon.setGreetingLabel(user);
                appCon.setCurrentLabel(LocalDate.now().getMonth().toString());

                System.out.println("User: " + appCon.getUser());
                System.out.println("Login Successful!");
                
              
                
                
                //Selects all appointments connected to the current user logged in.
                ps = conn.prepareStatement("SELECT appointmentId, title, customerName, location, description, start, end "
                + "FROM customer c RIGHT JOIN appointment a " 
                + "ON c.customerId = a.customerId WHERE contact = ? ORDER BY start");
               
                ps.setString(1, user);
                
                ResultSet rs =  ps.executeQuery();

                data = FXCollections.observableArrayList();
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

                    ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId);
                    ZonedDateTime newLocalEnd = newzdtEnd.withZoneSameInstant(zId);
                    
                    
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
                
                
                        //filter appointments
                        for(int i = 0;i < masterData.size();i++){
                            LocalDate today = LocalDate.now();
                            LocalDate appDate = LocalDate.parse(masterData.get(i).getDate(),dateFormat);
                            
                           //if appointment is scheduled for current month, then it is added to ObservableList data 
                            if(appDate.getMonth() == today.getMonth() && appDate.getYear() == today.getYear())
                                data.add(masterData.get(i));
                        }
                      
                
                    appCon.setTableData(data);
                    appCon.setMasterData(masterData);
                    
                    ObservableList<schedule> alertData;
                    alertData = FXCollections.observableArrayList();

                    final List<String> fixedTime = FXCollections.observableArrayList();

                    
                    
                    //lambda that compares each appointment's time to current users time and sets off an alert window if there is an appointment within 15 minutes
                    masterData.stream().forEach((s) -> {
                        String[] splitTime = new String[1];
                        splitTime = s.getTime().split(" ");
                        String[] splitHalf = new String[1];
                        splitHalf = splitTime[0].split(":");
                        if(s.getTime().contains("pm")){
                            switch(splitHalf[0]){
                                case "12":
                                    fixedTime.add(0, "12:" + splitHalf[1]);
                                    break;
                                case "1":
                                    fixedTime.add(0,"13:" + splitHalf[1]);
                                    break;
                                case "2": 
                                    fixedTime.add(0,"14:" + splitHalf[1]);
                                    break;
                                case "3":
                                    fixedTime.add(0,"15:" + splitHalf[1]);
                                    break;
                                case "4":
                                    fixedTime.add(0,"16:" + splitHalf[1]);
                                    break;
                                case "5":
                                    fixedTime.add(0,"17:" + splitHalf[1]);
                                    break;
                                case "6":
                                    fixedTime.add(0,"18:" + splitHalf[1]);
                                    break;
                                case "7":
                                    fixedTime.add(0,"19:" + splitHalf[1]);
                                    break;
                                case "8":
                                    fixedTime.add(0,"20:" + splitHalf[1]);
                                    break;
                                case "9":
                                    fixedTime.add(0,"21:" + splitHalf[1]);
                                    break;
                                case "10":
                                    fixedTime.add(0,"22:" + splitHalf[1]);
                                    break;
                                case "11":
                                    fixedTime.add(0,"23:" + splitHalf[1]);
                                    break;
                            }
                            
                        }else{
                            switch(splitHalf[0]){
                                case "12":
                                    fixedTime.add(0,"24:" + splitHalf[1]);
                                    break;
                                case "1":
                                    fixedTime.add(0,"01:" + splitHalf[1]);
                                    break;
                                case "2": 
                                    fixedTime.add(0,"02:" + splitHalf[1]);
                                    break;
                                case "3":
                                    fixedTime.add(0,"03:" + splitHalf[1]);
                                    break;
                                case "4":
                                    fixedTime.add(0,"04:" + splitHalf[1]);
                                    break;
                                case "5":
                                    fixedTime.add(0,"05:" + splitHalf[1]);
                                    break;
                                case "6":
                                    fixedTime.add(0,"06:" + splitHalf[1]);
                                    break;
                                case "7":
                                    fixedTime.add(0,"07:" + splitHalf[1]);
                                    break;
                                case "8":
                                    fixedTime.add(0,"08:" + splitHalf[1]);
                                    break;
                                case "9":
                                    fixedTime.add(0,"09:" + splitHalf[1]);
                                    break;
                                case "10":
                                    fixedTime.add(0,"10:" + splitHalf[1]);
                                    break;
                                case "11":
                                    fixedTime.add(0,"11:" + splitHalf[1]);
                                    break;
                            
                            }
                        }
                        
                        LocalTime appTime = LocalTime.parse(fixedTime.get(0));
                        
                        
                        
                        double comparedTime = MINUTES.between(appTime, LocalTime.now());
                        
                        //System.out.println("appTime compared to now time is: " + comparedTime);
                        if(comparedTime >= -15 && comparedTime < (0) && s.getDate().equals(LocalDate.now(zId).toString()))
                            alertData.add(s);
                            
                    });
                    
                    
                    //Login screen is closed             
                    Parent root1 = loader2.load();
                    Stage stage = new Stage();
                    Stage stage2 = new Stage();
                    stage2=(Stage)loginButton.getScene().getWindow();
                    Scene scene = new Scene(root);
                    Scene scene2 = new Scene(root1);
                    stage.setScene(scene);
                    stage2.setScene(scene2);
                    stage2.hide();
                    
                    //if alertData is empty appointments.fxml is opened
                    if(alertData.isEmpty()){
                        stage.show();
                        
                    //else if alertData is not empty, an alert dialog is opened
                    } else{
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Reminder");
                        alert.setHeaderText("Appointment Reminder");
                        alert.setContentText("You have an appointment with " + alertData.get(0).getCustomerName() + " in 15 minutes or less in " + alertData.get(0).getLocation() + ".");

                        //Lambda Alert
                        alert.showAndWait().ifPresent((response -> {
                            if (response == ButtonType.OK) {
                                    System.out.println("Alerting!");
                                    stage.show();
                            }
                        }));
                    }
                         
                    
                    //Appends information about when and who logged in to Log text file
                    LOGGER.log(Level.INFO, "User: {0} successfully Logged in at {1}. /n", new Object[]{user, LocalDateTime.now()});



                if( !zId.equals(ZoneId.of("Europe/Berlin"))){
                System.out.println("Username and Password Correct!");
                } else{
                    System.out.println("Benutzername und Passwort korrekt!");
                }
                
               
                
            } else{
                if(!zId.equals(ZoneId.of("Europe/Berlin"))){
                    
                errorLabel.setText("Invalid Username or Password");
                System.out.println("Login unsuccessful! Username or Password Incorrect.");
                } else{
                    errorLabel.setText("Ungültiger Benutzername oder Passwort");
                    System.out.println("Anmeldung fehlgeschlagen! Benutzername oder Passwort falsch.");
                }
            }

                
            
        // if no match is found for username and password, then an error is caught and printed to the console.
        } catch (SQLException e){
            System.out.println("Failed to Connect to Database.");
            System.out.println("SQLException: "+e.getMessage());
            System.out.println("SQLState: "+e.getSQLState());
            System.out.println("VendorError: "+e.getErrorCode());
        }
        
       

    }
    
    
    
     /********     Exit Button Action     ******************
     *             Closes Program.                      ****
     *                                                  ****
     *******************************************************/
    @FXML
    void exitButtonAction(ActionEvent event) throws Exception{
        
            Stage stage;
            Parent root;
            
            //gets reference to the button's stage
            stage=(Stage)exitButton.getScene().getWindow();
            
            //load up Other FXML Document
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            root = fxmlLoader.load();
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.hide();
        
    }
    
    
    

    
    
    
    
    /* <Setter and Getter Methods> */
    
    
    
     /** Setter Method for Connection conn  ****************
     *                                                  ****
     * @param c                                         ****
     *******************************************************/
    @FXML void setConn(Connection c){
        conn = c;
    }
    
    
    
    
     /** Getter Method for username variable  **************
     *                                                  ****
     * @return username                                 ****
     *******************************************************/
    @FXML String getUsername(){
        return username;
    }
    
    
     /** Setter Method for LoginController logCon **********
     *                                                  ****
     * @param l                                         ****
     *******************************************************/
    public void setLogCon(LoginController l){
    logCon = l;
}
   
    
     /** Setter Method for ZoneId zId  *********************
     *                                                  ****
     * @param z                                         ****
     *******************************************************/
    public void setZoneId(ZoneId z){
        zId = z;
    }
   
    
   
    
    
    /** Getter Method that converts End Time to civilian time in current time zone.     ****
     *                                                                                  ****
     * @return tableEnd                                                                 ****
     ***************************************************************************************/
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
    
    
    
    
    
    /** Getter Method that converts Start Time to civilian time in current time zone.    ****
     *                                                                                   ****
     * @return tableTime                                                                 ****
     ****************************************************************************************/
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
    
    
    
    
    
    
    /** Setter Method that translates label text to English ****
     *                                                      ****
     *                                                      ****
     ***********************************************************/
    //translates label and button texts to English
    public void runEnglish(){
      setText(loginLabel, "Login");
      setText(usernameLabel, "Username:");
      setText(passwordLabel, "Password:");
      setButtonText(loginButton, "Login");
    }
    
    
    
    
    /** Setter Method that translates label text to German  ****
     *                                                      ****
     *                                                      ****
     ***********************************************************/
    //translates label and button texts to German 
    public void runGerman(){
      setText(loginLabel, "Anmeldung");
      setText(usernameLabel, "Nutzername:");
      setText(passwordLabel, "Passwort:");
      setButtonText(loginButton, "Anmeldung");
    }
    
    
 
  


    
    /** Setter Methods that are used by multiple nodes to accomplish the same action ****
     *                                                                               ****
     *                                                                               ****
     * @param b
     * @param s
     ************************************************************************************/
      
    public void setText(Label b, String s){
    b.setText(s);
    }
    
    public void setButtonText(Button b, String s){
        b.setText(s);
    }
    
    
    
    
}
    
