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
public final class AppointmentType {
    
    private final SimpleStringProperty title = new SimpleStringProperty("");
    private final SimpleStringProperty count = new SimpleStringProperty("");
    
    




    public AppointmentType(String title, String count) {
        
        setTitle(title);
        setCount(count);
        
}

    


public String getTitle(){
    
    return title.get();
}

public void setTitle(String t){
    
    title.set(t);
}

public String getCount(){

    return count.get();
}

public void setCount(String n){

    count.set(n);
    
}


}