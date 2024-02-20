package azure;



public class SampleAzure {
	public static void main( String[] args ) {
			dps dps = new dps();
			dpssastoken dpssas = new dpssastoken();
			azure_connect connection = new azure_connect();
			
			try {
				String derivedKey = "";
				String SasToken = "";
				String uri = "";
				String hub = "";
				String regId = "DevysLaptop_2";
				String idscope = "test";
				String dpskey = "testkey";
				derivedKey = dpssas.GetderivedKey(regId,idscope,dpskey);
				uri = idscope+"/registrations/"+regId;
				SasToken = dpssas.GenerateSasToken(uri,"registration",derivedKey);
				dps.ConnectDPS(regId,idscope,SasToken);
				hub = "DMFTSP3.azure-devices.net";
				String Symmetrickey = "";
				connection.connect(regId,derivedKey,hub);//replace derivedkey with Symmetrickey to connect without DPS
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	
}
