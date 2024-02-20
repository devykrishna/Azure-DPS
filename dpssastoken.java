package azure;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class dpssastoken {

	public String GetderivedKey(String device_id,String scope_id,String	dpskey) {
		String derivedKey = "";
		 String stringToSign = "";
		try {
			stringToSign = URLEncoder.encode(device_id, "UTF-8");
			derivedKey = getHMAC256(dpskey, stringToSign);
	         System.out.println("Derived Key: " + derivedKey);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
		 return derivedKey;
	}
	public String GenerateSasToken(String resourceUri, String keyName, String key) {
		long epoch = System.currentTimeMillis()/1000L;
	      int week = 60*60*24*364; //year
	      String expiry = Long.toString(epoch + week);

	      String sasToken = null;
	      try {
	          String stringToSign = URLEncoder.encode(resourceUri, "UTF-8") + "\n" + expiry;
	          String signature = getHMAC256(key, stringToSign);
	          
	          sasToken = "SharedAccessSignature sr=" + URLEncoder.encode(resourceUri, "UTF-8") +"&sig=" +
	                  URLEncoder.encode(signature, "UTF-8") + "&se=" + expiry+ "&skn=" + keyName;
	        System.out.println("sasToken: " + sasToken);
	      } catch (UnsupportedEncodingException e) {

	          e.printStackTrace();
	      }

	      return sasToken;
	}
	public static String getHMAC256(String key, String input) {
	    Mac sha256_HMAC = null;
	    String hash = null;
	    try {
	        sha256_HMAC = Mac.getInstance("HmacSHA256");
	       // SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
	        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), "HmacSHA256");
	        sha256_HMAC.init(secretKey);
	        Encoder encoder = Base64.getEncoder();

	        hash = new String(encoder.encode(sha256_HMAC.doFinal(input.getBytes("UTF-8"))));

	    } catch (InvalidKeyException e) {
	        e.printStackTrace();
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (IllegalStateException e) {
	        e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    }

	    return hash;
	}
}
