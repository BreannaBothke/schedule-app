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
public final class Customer {
    
    private final SimpleStringProperty customerId = new SimpleStringProperty("");
    private final SimpleStringProperty customerName = new SimpleStringProperty("");
    private final SimpleStringProperty address = new SimpleStringProperty("");
    private final SimpleStringProperty phone = new SimpleStringProperty("");
   
    




    public Customer(String customerId, String customerName, String address, String phone) {
        
        setCustomerId(customerId);
        setCustomerName(customerName);
        setAddress(address);
        setPhone(phone);
        
}

public String getCustomerId(){
    
    return customerId.get();
}
    
public void setCustomerId(String i){
    customerId.set(i);
}

public String getCustomerName(){
    
    return customerName.get();
}

public void setCustomerName(String t){
    
    customerName.set(t);
}

public String getAddress(){

    return address.get();
}

public void setAddress(String n){

    address.set(n);
    
}

public String getPhone(){
    return phone.get();
}

public void setPhone(String l){
    phone.set(l);
}


}