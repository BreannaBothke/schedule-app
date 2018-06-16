/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package breannabothkeschedulingapplication;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Admin
 */
public class BreannaBothkeSchedulingApplication extends Application {
        private final ZonedDateTime ztd = ZonedDateTime.now();
        private final ZoneId zone = ztd.getZone();



    @Override
    public void start(Stage stage) throws Exception {
        
        
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = fxmlLoader.load();        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
        LoginController logCon = fxmlLoader.getController();
        logCon.setZoneId(zone);
        
    /*Translates from English to German if the ZoneId is of Berlin*/

        if( !zone.equals(ZoneId.of("Europe/Berlin"))){
            logCon.runEnglish();
           
        } else {
            logCon.runGerman();
           
            }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        
        
    }
    
}
