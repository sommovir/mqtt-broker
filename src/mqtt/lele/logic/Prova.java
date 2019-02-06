/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mqtt.lele.logic;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lele
 */
public class Prova {
    private static Prova instance = null;
    
    public static Prova getInstance(){
        if (instance == null){
            instance = new Prova();
        }
        return instance;
    }
    
    public void start(){
        try {
            Runtime.getRuntime().exec("mosquitto");
        } catch (IOException ex) {
            Logger.getLogger(Prova.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void end(){
        try {
            Runtime.getRuntime().exec("pkill mosquitto");
        } catch (IOException ex) {
            Logger.getLogger(Prova.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
