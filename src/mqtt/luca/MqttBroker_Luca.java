/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mqtt.luca;

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
public class MqttBroker_Luca implements MqttCallback {

    private static MqttBroker_Luca _instance = null;
    private Process mqtt = null;
    private static final String TASKLIST = "tasklist";
    private static final String KILL = "taskkill /F /IM ";
//    private List<MessageListener> listeners = new ArrayList<>();
    private String broker = "tcp://localhost:1883";
    private String clientId = "server";
    private boolean ignore = false;
    private MqttClient sampleClient = null;
    private final String IM_ALIVE_TOPIC = "imalive";

    public static MqttBroker_Luca getInstance() {
        if (_instance == null) {
            _instance = new MqttBroker_Luca();
            return _instance;
        } else {
            return _instance;
        }
    }

    private MqttBroker_Luca() {
        super();
    }

    public void connect() {
        try {
            if (mqtt == null && !isProcessRunning("mosquitto.exe *32")) {
                mqtt = Runtime.getRuntime().exec("./mosquitto/mosquitto.exe");
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
        } catch (IOException ex) {
            Logger.getLogger(MqttBroker_Luca.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MqttBroker_Luca.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect() {
        try {
            if (mqtt != null && isProcessRunning("mosquitto.exe")) {
                killProcess("mosquitto.exe");
                System.out.println("MQTT disconnected");
            }
        } catch (Exception ex) {
            Logger.getLogger(MqttBroker_Luca.class.getName()).log(Level.SEVERE, null, ex);
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

    public static void killProcess(String serviceName) throws Exception {
        System.out.println("service name : " + serviceName);

        Runtime.getRuntime().exec(KILL + serviceName);

    }

    @Override
    public void connectionLost(Throwable thrwbl) {
        System.out.println("we have a problem!");
        thrwbl.printStackTrace();
    }

    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        String message = new String(mm.getPayload());
        System.out.println("[Server][Message arrived]Topic: " + topic);
        System.out.println("[Server][Message arrived]Message: " + message);

        if (topic.equals(IM_ALIVE_TOPIC)) {
            String client_id = message;
            this.publish(client_id, "Hello dear "+client_id);
            System.out.println("message sent on topic: "+client_id);
        }
       
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        System.out.println("delivery complete");
    }

    public synchronized void publish(String topic, String message) throws MqttException {
//        int t = 1;
        sampleClient.publish(topic, new MqttMessage(message.getBytes()));
    }

}