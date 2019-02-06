/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mqtt.luca;

import mqtt.lele.gui.JFrameGui;

/**
 *
 * @author Luca Coraci <luca.coraci@istc.cnr.it> ISTC-CNR
 */
public class Main_Luca {
    
    public static void main(String[] args) {
        if(args.length !=0 && args[0].equals("-gui")){
            System.out.println("[MQTT-BROKED] using gui..");
        }else{
            System.out.println("[MQTT-BROKED] dsfault start..");
            MqttBroker_Luca.getInstance().connect();
        }
    }
    
}
