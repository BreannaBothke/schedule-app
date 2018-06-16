/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package breannabothkeschedulingapplication;

import breannabothkeschedulingapplication.Models.AppointmentType;
import breannabothkeschedulingapplication.Models.Report;
import breannabothkeschedulingapplication.Models.Location;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Admin
 */
public class ViewReportsController {
    
    @FXML
    private Label viewReportsLabel, reportLabel;

    @FXML
    private Separator divider;
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane scrollPaneContent,contentPane, locationPane;

    @FXML
    private Button monthReportButton, consultantReportButton, locationReportButton, backButton;
    
    private Connection conn;
    private PreparedStatement ps;
    private ZoneId zId;

    
    
    
    
    
    
    
    
    
     /* <Button Actions> */
    
    
     /**    Back Button   *******************************************************
     **   Closes View Reports Screen and returns to the Appointments screen   ***
     ****************************************************************************/ 
    @FXML 
    void backButtonAction (ActionEvent event) throws IOException {
        
            Stage stage;
            Parent root;
            
            //gets reference to the button's stage
            stage=(Stage)backButton.getScene().getWindow();
            
            //load up Other FXML Document
            root = FXMLLoader.load(getClass().getResource("viewReports.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.hide();
        
    }
    
    
    
    
    
     /**    Consultant Report Button   ************************************************************************************************
     **   Accesses the database and creates a tab for each consultant and a table view that lists each consultant's appointments.   ***
     **********************************************************************************************************************************/ 
    @FXML
    void consultantReportButtonAction(ActionEvent event) throws SQLException {
        
        viewReportsLabel.setText("Consultant Schedule Report");
    
        if(contentPane.getChildren() != null && contentPane.getChildren().contains(scrollPane)){
            contentPane.getChildren().remove(scrollPane);
            tabPane = new TabPane();
            tabPane.setPrefSize(1037, 460);
            contentPane.getChildren().add(tabPane);

        }else{
            tabPane = new TabPane();
            tabPane.setPrefSize(1037, 460);
            contentPane.getChildren().add(tabPane);
        }
        
        conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
        
        ObservableList<String> consultants = FXCollections.observableArrayList();
        ObservableList<Report> appointments = FXCollections.observableArrayList();
        
        ps = conn.prepareStatement("SELECT username FROM user");
        ResultSet rs = ps.executeQuery();
        
        while(rs.next()){
            consultants.add(rs.getString("username"));
            
        }
        
        
        
        //grab appointments by consultant
        ps = conn.prepareStatement("SELECT contact as Consultant, appointmentId, title, customerName, location, description, start, end " +
                                        "FROM customer c RIGHT JOIN appointment a " +
                                        "ON c.customerId = a.customerId ORDER BY start");
        
        rs = ps.executeQuery();
        
        while(rs.next()){
            
            Timestamp start = rs.getTimestamp("start");
            Timestamp end = rs.getTimestamp("end");
            String dateSt = "";
            String timeSt = "";
            String endTimeSt = "";
            
            start.getTime();
            end.getTime();
            
            ZonedDateTime newzdtStart = start.toLocalDateTime().atZone(ZoneId.of("UTC"));
            ZonedDateTime newzdtEnd = end.toLocalDateTime().atZone(ZoneId.of("UTC"));

            ZonedDateTime newLocalStart = newzdtStart.withZoneSameInstant(zId);
            ZonedDateTime newLocalEnd = newzdtEnd.withZoneSameInstant(zId);
            
            dateSt = newLocalStart.toLocalDate().toString();
            timeSt = newLocalStart.toLocalTime().toString();
            endTimeSt = newLocalEnd.toLocalTime().toString();
            
            String[] timeSplit = new String[1];
            String[] endSplit = new String[1];
                    
            endSplit = endTimeSt.split(":");
            timeSplit = timeSt.split(":");
                    
            System.out.println(timeSplit[0]);
            System.out.println(timeSplit[1]);
                    
            String tableTime = "";
            String tableEnd = "";

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
            
            appointments.add(new Report(rs.getString("Consultant"), rs.getString("appointmentId"), rs.getString("title"), rs.getString("customerName"), rs.getString("location"),
                    rs.getString("description"), dateSt, tableTime, tableEnd));
        }
        
        
        consultants.stream().forEach((c) -> {
            TableView<Report> table = new TableView();
            TableColumn<Report, String> dateColumn = new TableColumn<>("Date");
            dateColumn.setCellValueFactory(new PropertyValueFactory("date"));
            dateColumn.setPrefWidth(86);
            TableColumn<Report, String> titleColumn = new TableColumn<>("Title");
            titleColumn.setCellValueFactory(new PropertyValueFactory("title"));
            titleColumn.setPrefWidth(92);
            TableColumn<Report, String> nameColumn = new TableColumn<>("Customer Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory("customerName"));
            nameColumn.setPrefWidth(146);
            TableColumn<Report, String> locationColumn = new TableColumn<>("Location");
            locationColumn.setCellValueFactory(new PropertyValueFactory("location"));
            locationColumn.setPrefWidth(141);
            TableColumn<Report, String> descriptionColumn = new TableColumn<>("Description");
            descriptionColumn.setCellValueFactory(new PropertyValueFactory("description"));
            descriptionColumn.setPrefWidth(198);
            TableColumn<Report, String> timeColumn = new TableColumn<>("Time");
            timeColumn.setCellValueFactory(new PropertyValueFactory("time"));
            timeColumn.setPrefWidth(89);
            
            table.getColumns().add(dateColumn);
            table.getColumns().add(titleColumn);
            table.getColumns().add(nameColumn);
            table.getColumns().add(locationColumn);
            table.getColumns().add(descriptionColumn);
            table.getColumns().add(timeColumn);
            table.setPrefWidth(753);
            table.setLayoutX(150);
      
            Tab tab = new Tab();
            tab.setText(c);
            tab.setContent(table);
            tabPane.getTabs().add(tab);            
            
            appointments.stream().filter((r) -> (r.getConsultant().equals(c))).forEach((r) -> {
                table.getItems().add(r);
                
            });
          });

        

    }

    
    
    
    
    
    
    
    
     /**    Location Report Button   **************************************************************************************************
     **   Accesses the database and creates an anchor pane that prints out how many appointments were scheduled for each location.  ***
     **********************************************************************************************************************************/ 
    @FXML
    void locationReportButtonAction(ActionEvent event) throws SQLException {

        
         if(contentPane.getChildren().contains(tabPane)){
                    
            tabPane.getTabs().clear();
            contentPane.getChildren().remove(tabPane);
            scrollPane = new ScrollPane();
            scrollPaneContent = new AnchorPane();
            scrollPane.setContent(scrollPaneContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(contentPane.getPrefWidth(), contentPane.getPrefHeight());
            scrollPane.setPrefViewportWidth(contentPane.getPrefWidth());
            scrollPaneContent.setPrefSize(scrollPane.getPrefWidth(), scrollPane.getPrefHeight());            
            contentPane.getChildren().add(scrollPane);
            
         
                        

            
        }else if(contentPane.getChildren().contains(scrollPane)){
            
            contentPane.getChildren().remove(scrollPane);
             scrollPane = new ScrollPane();
            scrollPaneContent = new AnchorPane();
            scrollPane.setContent(scrollPaneContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(contentPane.getPrefWidth(), contentPane.getPrefHeight());
            scrollPane.setPrefViewportWidth(contentPane.getPrefWidth());
            scrollPaneContent.setPrefSize(scrollPane.getPrefWidth(), scrollPane.getPrefHeight());            
            contentPane.getChildren().add(scrollPane);
            
        }
         else if(contentPane.getChildren().isEmpty()){
            scrollPane = new ScrollPane();
            scrollPaneContent = new AnchorPane();
            scrollPane.setContent(scrollPaneContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(contentPane.getPrefWidth(), contentPane.getPrefHeight());
            scrollPane.setPrefViewportWidth(contentPane.getPrefWidth());
            scrollPaneContent.setPrefSize(scrollPane.getPrefWidth(), scrollPane.getPrefHeight());            
            contentPane.getChildren().add(scrollPane);
        }
        viewReportsLabel.setText(LocalDate.now(zId).getYear() + " Location Report");
        conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
        ps = conn.prepareStatement("SELECT location as Location, COUNT(location) as Count FROM appointment WHERE year(start) = year(curdate()) GROUP BY location");

        ResultSet rs = ps.executeQuery();
        int yLayout = 0;
        ObservableList<Location> location = FXCollections.observableArrayList();
     
        while(rs.next()){
            
            location.add(new Location(rs.getString("Location"), rs.getString("Count")));
            
        }
        
        for(Location l:location){
            Label locationLabel = new Label();
            locationLabel.setText(l.getLocation() + ":");
            locationLabel.setLayoutX(20);
            locationLabel.setLayoutY(yLayout);
            
            Label countLabel = new Label();
            countLabel.setText(l.getCount());
            countLabel.setLayoutX(150);
            countLabel.setLayoutY(yLayout);
            scrollPaneContent.getChildren().addAll(locationLabel, countLabel);
            yLayout += 50;

            
            
        }
        
        
        
            

            
        }
        
        
        
        
        
    

    
    
    
    
    
     /**    Month Report Button   ****************************************************************************************************************
     **   Accesses the database and creates an anchor pane that prints out how many appointments of each type were scheduled for each month.   ***
     *********************************************************************************************************************************************/ 
    @FXML
    void monthReportButtonAction(ActionEvent event) throws SQLException {
    
        
        if(contentPane.getChildren().contains(tabPane)){
                    
            tabPane.getTabs().clear();
            contentPane.getChildren().remove(tabPane);
            scrollPane = new ScrollPane();
            scrollPaneContent = new AnchorPane();
            scrollPane.setContent(scrollPaneContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(contentPane.getPrefWidth(), contentPane.getPrefHeight());
            scrollPane.setPrefViewportWidth(contentPane.getPrefWidth());
            scrollPaneContent.setPrefSize(scrollPane.getPrefWidth(), scrollPane.getPrefHeight());            
            contentPane.getChildren().add(scrollPane);
            
         
                        

            
        }else if(contentPane.getChildren().contains(scrollPane)){
            contentPane.getChildren().remove(scrollPane);
            scrollPane = new ScrollPane();
            scrollPaneContent = new AnchorPane();
            scrollPane.setContent(scrollPaneContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(contentPane.getPrefWidth(), contentPane.getPrefHeight());
            scrollPane.setPrefViewportWidth(contentPane.getPrefWidth());
            scrollPaneContent.setPrefSize(scrollPane.getPrefWidth(), scrollPane.getPrefHeight());            
            contentPane.getChildren().add(scrollPane);
            
        }else if(contentPane.getChildren().isEmpty()){
            scrollPane = new ScrollPane();
            scrollPaneContent = new AnchorPane();
            scrollPane.setContent(scrollPaneContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefSize(contentPane.getPrefWidth(), contentPane.getPrefHeight());
            scrollPane.setPrefViewportWidth(contentPane.getPrefWidth());
            scrollPaneContent.setPrefSize(scrollPane.getPrefWidth(), scrollPane.getPrefHeight());            
            contentPane.getChildren().add(scrollPane);
           
        }
        viewReportsLabel.setText(LocalDate.now(zId).getYear() + " Monthly Report");
        conn = DriverManager.getConnection("jdbc:mysql://52.206.157.109/U04bnM", "U04bnM", "53688195426");
        ps = conn.prepareStatement("SELECT title as Type, COUNT(title) as Count FROM appointment WHERE month(start) = ? AND year(start) = year(curdate()) GROUP BY title");

        
        String monthName;
        String infoSt = "";
        int xPosition = 0;
        ObservableList<AppointmentType> jan = FXCollections.observableArrayList();
        ObservableList<AppointmentType> feb = FXCollections.observableArrayList();
        ObservableList<AppointmentType> mar = FXCollections.observableArrayList();
        ObservableList<AppointmentType> apr = FXCollections.observableArrayList();
        ObservableList<AppointmentType> may = FXCollections.observableArrayList();
        ObservableList<AppointmentType> jun = FXCollections.observableArrayList();
        ObservableList<AppointmentType> jul = FXCollections.observableArrayList();
        ObservableList<AppointmentType> aug = FXCollections.observableArrayList();
        ObservableList<AppointmentType> sep = FXCollections.observableArrayList();
        ObservableList<AppointmentType> oct = FXCollections.observableArrayList();
        ObservableList<AppointmentType> nov = FXCollections.observableArrayList();
        ObservableList<AppointmentType> dec = FXCollections.observableArrayList();
        
        for(int i = 0;i<=12;i++){
            monthName = getMonthName(i);
            ps.setInt(1, i);
            Label monthLabel = new Label();
            monthLabel.setLayoutX(xPosition);
            monthLabel.setText(monthName);
            monthLabel.setLayoutY(2); 
            
            
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                switch (i) {
                    case 1:
                        jan.add( new AppointmentType(rs.getString("Type"), rs.getString("Count")));
                        break;
                    case 2:
                        feb.add(new AppointmentType(rs.getString("Type"), rs.getString("Count")));
                        break;
                    case 3:
                        mar.add(new AppointmentType(rs.getString("Type"), rs.getString("Count")));
                        break;
                    case 4:
                        apr.add(new AppointmentType(rs.getString("Type"), rs.getString("Count")));
                        break;
                    case 5:
                        may.add(new AppointmentType(rs.getString("Type"), rs.getString("Count")));
                        break;
                    case 6:
                        jun.add(new AppointmentType(rs.getString("Type"), rs.getString("Count"))); 
                        break;
                    case 7:
                        jul.add(new AppointmentType(rs.getString("Type"), rs.getString("Count")));
                        break;
                    case 8:
                        aug.add(new AppointmentType(rs.getString("Type"), rs.getString("Count")));
                        break;
                    case 9:
                        sep.add(new AppointmentType(rs.getString("Type"), rs.getString("Count")));
                        break;
                    case 10:
                        oct.add(new AppointmentType(rs.getString("Type"), rs.getString("Count")));
                        break;
                    case 11:
                        nov.add(new AppointmentType(rs.getString("Type"), rs.getString("Count")));
                        break;
                    case 12:
                        dec.add(new AppointmentType(rs.getString("Type"), rs.getString("Count")));
                        break;
                }
               
            }
            switch (i) {
                case 1:
                    infoSt = "";
                    if(jan.size()> 0){
                    for(AppointmentType a:jan){
                        infoSt += a.getTitle() + ": " + a.getCount() + " \n";
                    } 
                    }else
                        infoSt += "None";
                    break;
                case 2:
                    infoSt = "";
                     if(feb.size()> 0){
                    for(AppointmentType a:feb){
                        infoSt += a.getTitle() + ": " + a.getCount() + " \n";
                    }  
                    }else
                        infoSt += "None";
                    break;
                case 3:
                    infoSt = "";
                     if(mar.size()> 0){
                    for(AppointmentType a:mar){
                        infoSt += a.getTitle() + ": " + a.getCount() + " \n";
                    }  
                    }else
                        infoSt += "None";
                    break;
                    
                case 4:
                    infoSt = "";
                     if(apr.size()> 0){
                    for(AppointmentType a:apr){
                        infoSt += a.getTitle() + ": " + a.getCount() + " \n";
                    }   
                    }else
                        infoSt += "None";
                    break;
                case 5:
                    infoSt = "";
                     if(may.size()> 0){
                    for(AppointmentType a:may){
                        infoSt += a.getTitle() + ": " + a.getCount() + " \n";
                    }  
                    }else
                        infoSt += "None";
                    break;
                case 6:
                    infoSt = "";
                     if(jun.size()> 0){
                    for(AppointmentType a:jun){
                        infoSt += a.getTitle() + ": " + a.getCount() + " \n";
                    }      
                    }else
                        infoSt += "None";
                    break;
                case 7:
                    infoSt = "";
                     if(jul.size()> 0){
                    for(AppointmentType a:jul){
                        infoSt += a.getTitle() + ": " + a.getCount() + " \n";
                    }   
                    }else
                        infoSt += "None";
                    break;
                case 8:
                    infoSt = "";
                     if(aug.size()> 0){
                    for(AppointmentType a:aug){
                        infoSt += a.getTitle() + ": " + a.getCount() + " \n";
                    }   
                    }else
                        infoSt += "None";
                    break;
                case 9:
                    infoSt = "";
                     if(sep.size()> 0){
                    for(AppointmentType a:sep){
                        infoSt += a.getTitle() + ": " + a.getCount() + " \n";
                    }   
                    }else
                        infoSt += "None";
                    break;
                case 10:
                    infoSt = "";
                     if(oct.size()> 0){
                    for(AppointmentType a:oct){
                        infoSt += a.getTitle() + ": " + a.getCount() + " \n";
                    }   
                    }else
                        infoSt += "None";
                    break;
                case 11:
                    infoSt = "";
                     if(nov.size()> 0){
                    for(AppointmentType a:nov){
                        infoSt += a.getTitle() + ": " + a.getCount() + " \n";
                    }   
                    }else
                        infoSt += "None";
                    break;
                case 12:
                    infoSt = "";
                     if(dec.size()> 0){
                    for(AppointmentType a:dec){
                        infoSt += a.getTitle() + ": " + a.getCount() + " \n";
                    }   
                    }else
                        infoSt += "None";
                    break;
                default:
                    infoSt = "";
                    break;
            }
            Label info  = new Label();
            info.setText(infoSt);
            info.setLayoutX(xPosition);
            info.setLayoutY(50);
               
            
            scrollPaneContent.getChildren().addAll(monthLabel, info);
            xPosition+= 90;


            
        }
        
        
        


    }
    
    
    
    
    
       /* <Setter and Getter Methods> */

    
    
    
    
     /** Getter Method that returns the month name according to the number variable entered.    ****
     * @param i                                                                                 ****
     * @return monthName                                                                        ****
     ***********************************************************************************************/
    String getMonthName(int i){
        String monthName = "";
        switch (i){
                case 1:
                    monthName = "January";
                    break;
                case 2:
                    monthName = "February";
                    break;
                case 3:
                    monthName = "March";
                    break;
                case 4:
                    monthName = "April";
                    break;
                case 5:
                    monthName = "May";
                    break;
                case 6:
                    monthName = "June";
                    break;
                case 7:
                    monthName = "July";
                    break;
                case 8:
                    monthName = "August";
                    break;
                case 9:
                    monthName = "September";
                    break;
                case 10:
                    monthName = "October";
                    break;
                case 11:
                    monthName = "November";
                    break;
                case 12:
                    monthName = "December";
                    break;
                default:
                    
                            
            }
        return monthName;
    }
    
    
    
    
    
    
    
    
     /** Setter Method for ZoneId variable zId          ****
     *                                                  ****
     * @param z                                         ****
     *******************************************************/

    @FXML
    void setZoneId(ZoneId z){
        zId = z;
    }
    
    
    
    
}
