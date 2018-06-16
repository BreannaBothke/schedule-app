/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package breannabothkeschedulingapplication.Models;

import javafx.beans.property.SimpleStringProperty;



/**
 *
 * @author Admin
 */
public final class Report {
    
    private final SimpleStringProperty consultant = new SimpleStringProperty("");
    private final SimpleStringProperty appID = new SimpleStringProperty("");
    private final SimpleStringProperty title = new SimpleStringProperty("");
    private final SimpleStringProperty customerName = new SimpleStringProperty("");
    private final SimpleStringProperty location = new SimpleStringProperty("");
    private final SimpleStringProperty description = new SimpleStringProperty("");
    private final SimpleStringProperty date = new SimpleStringProperty("");
    private final SimpleStringProperty time = new SimpleStringProperty("");
    private final SimpleStringProperty endTime = new SimpleStringProperty("");
    




    public Report(String consult, String appID, String title, String customerName, String location, String description, String dt, String tm, String et) {
        
        setConsultant(consult);
        setID(appID);
        setTitle(title);
        setCustomerName(customerName);
        setLocation(location);
        setDescription(description);
        setDate(dt);
        setTime(tm);
        setEndTime(et);
}

public String getConsultant(){
    
    return consultant.get();
}
    
public void setConsultant(String i){
    consultant.set(i);
}
    
public String getID(){
    
    return appID.get();
}
    
public void setID(String i){
    appID.set(i);
}

public String getTitle(){
    
    return title.get();
}

public void setTitle(String t){
    
    title.set(t);
}

public String getCustomerName(){

    return customerName.get();
}

public void setCustomerName(String n){

    customerName.set(n);
    
}

public String getLocation(){
    return location.get();
}

public void setLocation(String l){
    location.set(l);
}

public String getDescription(){

    return description.get();
}

public void setDescription(String d){

    description.set(d);
}

public String getDate(){
    return date.get();
}

public void setDate(String d){
    date.set(d);
}

public String getTime(){
    return time.get();
}

public void setTime(String t){
    time.set(t);
}

public String getEndTime(){
    return endTime.get();
}

public void setEndTime(String t){
    endTime.set(t);
}
}