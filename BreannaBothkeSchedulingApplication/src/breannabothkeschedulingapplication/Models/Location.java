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
public final class Location {
    
  
    private final SimpleStringProperty location = new SimpleStringProperty("");
    private final SimpleStringProperty count = new SimpleStringProperty("");
    




    public Location(String location, String count) {
        
        setLocation(location);
        setCount(count);
       
    }

   
    public String getLocation(){

        return location.get();
    }

    public void setLocation(String n){
        location.set(n);
    }

    public String getCount(){

        return count.get();
    }

    public void setCount(String m){

        count.set(m);
    }

}
