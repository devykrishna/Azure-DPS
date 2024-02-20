package azure;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class azure_connect {
	public void connect(String deviceId,String key,String hub) {

	  String uri = hub+"/devices/"+deviceId;
	  String sasToken = sastoken.GetSASToken(uri," ",key);
	  System.out.println("-----------Sastoken: " + sasToken);
	  String brokerUri = "ssl://"+hub+":8883";
	  String clientId = deviceId;
	  String Username = hub+"/"+deviceId+"/?api-version=2021-04-12";
	  System.out.println("-----------Username: " + Username);
	  System.out.println( "Connecting to " + brokerUri +" as "+clientId);
	  
	  MqttAsyncClient client = null;
	  try {
	    client = new MqttAsyncClient( brokerUri, clientId );
		String topic        = "devices/"+deviceId+"/messages/events/";
	    String content      = "{device:123}";
	    MqttMessage message = new MqttMessage(content.getBytes());
		if (client != null) {
			MqttConnectOptions options = new MqttConnectOptions();
			options.setUserName(Username);
			options.setPassword(sasToken.toCharArray());
			options.setCleanSession(true);			
			options.setConnectionTimeout(30);
			options.setKeepAliveInterval(0);
			IMqttToken token = client.connect(options);
			//System.out.println("Connecting to " + brokerUri + " as " + clientId);
			token.waitForCompletion(6000);
			//System.out.println("Connecting to " + brokerUri + " as " + clientId);
			if (client.isConnected()) {
				System.out.println("Success!");
				client.publish(topic, message);
				System.out.println("Message published");
				System.out.println("topic: " + topic);
				System.out.println("message content: " + content);
				// disconnect
				client.disconnect();
				// close client
				client.close();
			} else {
				System.out.println("Could not connect to Azure IoT hub, timed-out");
			}
	    }
	  } catch ( MqttException e ) {
	    client.getDebug().dumpBaseDebug();
	    e.printStackTrace();
	  } finally {
	    if ( client != null ) {
	      try {
	        client.disconnect();
	      } catch ( MqttException ignore ) {}
	    }
	  }
}
}
