package azure;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class dps implements MqttCallback {
	Boolean connectlost = true;
	Boolean registersucess = true;
	String operationId = "";
	Boolean messagereceived = false;
	MqttClient client = null;
	
public void ConnectDPS(String regId,String idscope,String sasToken) throws InterruptedException {

	  String username = idscope+"/registrations/"+regId+"/api-version=2019-03-31";
	  String uri = idscope+"/registrations/"+regId;	
	  System.out.println("-----------uri: " + uri);
	  System.out.println("-----------username: " + username);
	  System.out.println("-----------Sastoken: " + sasToken);
	  String clientId = regId;
	  String brokerUri = "ssl://global.azure-devices-provisioning.net:8883";
	  System.out.println( "Connecting to " + brokerUri +" as "+clientId); 
	  
	  while(registersucess) {
		 
	  try {
		  if(connectlost) {
		    client = new MqttClient( brokerUri, clientId );
		    //$dps/registrations/PUT/iotdps-register/?$rid={request_id}	   
			if (client != null) {
				MqttConnectOptions options = new MqttConnectOptions();
				client.setCallback(this );
				options.setUserName(username);
				options.setPassword(sasToken.toCharArray());
				options.setCleanSession(true);			
				options.setConnectionTimeout(30);
				options.setKeepAliveInterval(0);
				client.connect(options);
				String Pubtopic = "$dps/registrations/PUT/iotdps-register/?$rid=1000";			
				String Subtopic = "$dps/registrations/res/#";
				String content  = "{\"registrationId\":\""+clientId+"\"}";
			    MqttMessage message = new MqttMessage(content.getBytes());
				if (client.isConnected()) {
					connectlost = false;
					client.subscribe(Subtopic, 0);
					System.out.println("Connection Success!");
					client.publish(Pubtopic, message);
					System.out.println("Message published");
					System.out.println("topic: " + Pubtopic);
					System.out.println("message content: " + content);					

				} else {
					System.out.println("Could not connect to Azure IoT hub, timed-out");
				}
		    }
		  }
		  if(messagereceived) {
			  Thread.sleep(5000);
			  pollDPS();
		  }
			Thread.sleep(1000);  
		  } catch ( MqttException e ) {
		    client.getDebug().dumpBaseDebug();
		    e.printStackTrace();
		  }
		
	  }
	// disconnect
		try {
			client.disconnect();
			// close client
			client.close();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
public void pollDPS() {
	if (client.isConnected()) {
		try {			
			System.out.println("DPS operationId: " + operationId);
			String Polltopic = "$dps/registrations/GET/iotdps-get-operationstatus/?$rid=1001&operationId=";
			Polltopic = Polltopic+(operationId);
			 String content2   = "";
			 MqttMessage message = new MqttMessage(content2.getBytes());
			client.publish(Polltopic, message);
			System.out.println("Poll topic: " + Polltopic);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
@Override
public void connectionLost(Throwable arg0) {
	// TODO Auto-generated method stub
	System.out.println("callback connectionLost ");
	connectlost = true;
}

@Override
public void deliveryComplete(IMqttDeliveryToken arg0) {
	System.out.println("callback message delivery complete ");
	
}

@Override
public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
	String content = "";

 	System.out.println("message arrived" );
	System.out.println("MQTT topic: " + arg0);
	System.out.println("MQTT Qos: " + arg1.getQos());
	System.out.println("MQTT message Received content: "
			+ new String(arg1.getPayload()));
	content = new String(arg1.getPayload());
	if(arg0.contains("202")) {
		System.out.println("message has 202" );
		String[] result = new String[4];
		result = content.split(":");
		result = result[1].split(",");
		operationId = result[0].replaceAll("\"", "");;
		messagereceived = true;
	} else if(arg0.contains("200")) {
		System.out.println("message has 200" );
		messagereceived = false;
		registersucess = false;
	}
	
}

}
