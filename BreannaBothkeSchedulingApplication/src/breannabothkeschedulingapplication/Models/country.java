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
public final class country {
    
    private final SimpleStringProperty countryId = new SimpleStringProperty("");
    private final SimpleStringProperty name = new SimpleStringProperty("");
    
    
    
    
    
    
     public country(String countryId, String name) {
        
        setCountryId(countryId);
        setName(name);
    
    }
 

    public String getCountryId(){
        return countryId.get();
    }

    public void setCountryId(String i){
        countryId.set(i);
    }

    public String getName(){
        return name.get();
    }

    public void setName(String n){
        name.set(n);
    }


}
     
