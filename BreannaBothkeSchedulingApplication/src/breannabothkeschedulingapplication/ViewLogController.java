/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package breannabothkeschedulingapplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;





/**
 *
 * @author Admin
 */
public class ViewLogController {
    
    
    
       @FXML
    private TextArea textArea;

    @FXML
    private Label viewLogLabel;

    @FXML
    private Separator separator;

    @FXML
    private Button backButton;

    
    
    
    
    
    
    
    
      /* <Button Actions> */
    
    
     /**    Back Button   ***************************************************
     **   Closes View Log Screen and returns to the Appointments screen   ***
     ************************************************************************/ 
    @FXML
    void backButtonAction(ActionEvent event) throws IOException {

            Stage stage;
            Parent root;
            
            //gets reference to the button's stage
            stage=(Stage)backButton.getScene().getWindow();
            
            //load up Other FXML Document
            root = FXMLLoader.load(getClass().getResource("viewLog.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.hide();
        
    }
    
    
    
    
    
    
    
       /* <Setter and Getter Methods> */
    
    
    
     /**         Setter Method for Text Area      **********************************
     *  Sets the text area text to the information stored in the log txt file.  ****
     *  @param file                                                             ****
     *******************************************************************************/
    @FXML
    void setTextArea(String file) throws FileNotFoundException{
        
        Scanner s = new Scanner(new File(file)).useDelimiter("/n");
        while(s.hasNext()){
            if(s.hasNextInt()){
                textArea.appendText(s.nextInt() + " ");
            }else{
                textArea.appendText(s.next() + " ");
            }
        }
            
    }
    
    
    
    
    
    
    
    
    
}
