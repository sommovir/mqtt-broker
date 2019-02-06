/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mqtt.lele.logic;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @author Luca Coraci <luca.coraci@istc.cnr.it> ISTC-CNR
 */
public class MqttBroker_Lele_OLD implements MqttCallback {

    private static MqttBroker_Lele_OLD _instance = null;
    private Process mqtt = null;
    private static String PROCESS = "mosquitto";
    private static final String TASKLIST = "ps -C " + PROCESS;
    private static final String KILL = "pkill ";
//    private List<MessageListener> listeners = new ArrayList<>();
    private String broker = "tcp://localhost:1883";
    private String clientId = "server";
    private boolean ignore = false;
    private MqttClient sampleClient = null;

    public static MqttBroker_Lele_OLD getInstance() {
        if (_instance == null) {
            _instance = new MqttBroker_Lele_OLD();
            return _instance;
        } else {
            return _instance;
        }
    }

    private MqttBroker_Lele_OLD() {
        super();
    }

    public void connect() {
        
        
        try {
            if (mqtt == null && !isProcessRunning(PROCESS)) {
                mqtt = Runtime.getRuntime().exec(PROCESS);
                System.out.println("MQTT connected");
                System.out.println("subscribing all");
                MemoryPersistence persistence = new MemoryPersistence();
                try {
                    sampleClient = new MqttClient(broker, clientId, persistence);
                    
                    MqttConnectOptions connOpts = new MqttConnectOptions();
                    connOpts.setCleanSession(true);
                    System.out.println("Connecting to broker: " + broker);
                    sampleClient.connect(connOpts);
                    System.out.println("Connected");

                    sampleClient.setCallback(_instance);
                    sampleClient.subscribe("chat");
                    sampleClient.subscribe("#");
//                    sampleClient.disconnect();
//                    mqtt = null;
                    System.out.println("Disconnected");
                } catch (MqttException me) {
                    System.out.println("reason " + me.getReasonCode());
                    System.out.println("msg " + me.getMessage());
                    System.out.println("loc " + me.getLocalizedMessage());
                    System.out.println("cause " + me.getCause());
                    System.out.println("excep " + me);
                    me.printStackTrace();
                }
            }
//        } catch (IOException ex) {
//            Logger.getLogger(MqttBroker_Lele_OLD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MqttBroker_Lele_OLD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect() {
        try {            
            Runtime.getRuntime().exec(KILL + PROCESS);
        } catch (IOException ex) {
            Logger.getLogger(MqttBroker_Lele_OLD.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }

    public void killProcess(String serviceName) {
        try {
            System.out.println("service name : " + serviceName);
                    
            Runtime.getRuntime().exec(KILL + serviceName);
        } catch (IOException ex) {
            Logger.getLogger(MqttBroker_Lele_OLD.class.getName()).log(Level.SEVERE, null, ex);
        }

    }    
    
    public static boolean isProcessRunning(String serviceName) throws Exception {

        Process p = Runtime.getRuntime().exec(TASKLIST);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {

            System.out.println(line);
            if (line.contains(serviceName)) {
                System.out.println("process found !");
                return true;
            }
        }

        return false;

    }



    @Override
    public void connectionLost(Throwable thrwbl) {
        System.out.println("we have a problem!");
        thrwbl.printStackTrace();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        System.out.println("message received -> " + mm);
        System.out.println("topic: " + topic);
       
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        System.out.println("delivery complete");
    }
    
    public void publish(String topic, String message) throws MqttException{
        sampleClient.publish(topic, new MqttMessage(message.getBytes()));
    }

}