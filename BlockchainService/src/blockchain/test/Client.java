package blockchain.test;

import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bftsmart.tom.ServiceProxy;

public class Client {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	ServiceProxy clientProxy = null;
	int id;

	public Client(int clientId) {

		// clientProxy = new ServiceProxy(clientId);
		id = clientId;
		clientProxy = new ServiceProxy(id);
	}

	public String insert() {

		try {
			JSONObject jObject = new JSONObject();
			jObject.put("operation", "insert");
			JSONObject subObject = new JSONObject();
			subObject.put("data", "120-jgf3[-120r8npqwrb;pqruq;LWAfbq20-[jve[jdlewqbt8['qwcjfkinvr032rfq");
			subObject.put("PKsp", DigestUtils.sha256Hex("2103jhnblso-02p3utgnm[c128ubtcl1dqw-o"));
			subObject.put("digest", DigestUtils.sha256Hex("jfpjw4eptgjp4mtimgpwaoimb032mirpo23"));
			subObject.put("type", 0);
			jObject.put("value", subObject);
			System.out.println("insert data: " + jObject.toString(4));
			byte[] reply = clientProxy.invokeOrdered(jObject.toString().getBytes());
			if (reply != null) {
				return new String(reply, "UTF-8");
			}
			return null;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		} finally {

		}
	}

	public String print() {
		JSONObject request = new JSONObject();
		request.put("operation", "print");
		try {
			byte[] reply = clientProxy.invokeUnordered(request.toString().getBytes());
			if (reply != null) {
				return new String(reply, "UTF-8");
			}
			return null;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		} finally {

		}
	}

	public void close() {
		clientProxy.close();
	}
}
