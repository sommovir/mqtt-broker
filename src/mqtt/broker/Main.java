/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mqtt.broker;

/**
 *
 * @author Luca Coraci <luca.coraci@istc.cnr.it> ISTC-CNR
 */
public class Main {
    
    public static void main(String[] args) {
        if(args.length !=0 && args[0].equals("-gui")){
            System.out.println("[MQTT-BROKED] using gui..");
        }else{
            System.out.println("[MQTT-BROKED] dafault start..");
            MqttBroker.getInstance().connect();
        }
    }
    
}
