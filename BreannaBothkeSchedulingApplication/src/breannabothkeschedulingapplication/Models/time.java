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
public final class time {
    
  
    private final SimpleStringProperty normal = new SimpleStringProperty("");
    private final SimpleStringProperty military = new SimpleStringProperty("");
    




    public time(String normal, String military) {
        
        setNormal(normal);
        setMilitary(military);
       
    }

   
    public String getNormal(){

        return normal.get();
    }

    public void setNormal(String n){
        normal.set(n);
    }

    public String getMilitary(){

        return military.get();
    }

    public void setMilitary(String m){

        military.set(m);
    }

}
