/**
 * 
 */
package medsession.client.test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import medsession.client.blockchain.BFTClient;
import medsession.client.util.HttpCallUtil;
import medsession.client.util.PropertiesLoader;
import medsession.client.util.crypto.CryptoUtil;

/**
 * @author user
 *
 */
public class MedSessionMain {
	private static String path = "C://Workspaces//eclipse-workspace//MedSessionClient//upload//SampleRecord1.dat";
	private static String DIRECTORY_SERVICE_URL = "http://localhost:8080/DirectoryService/directory";

	private static final int EVENT_DG = 0;
	private static final int EVENT_SC = 1;

	static {
		// read property file by invoking static method on ChordImpl
		PropertiesLoader.loadPropertyFile();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// any client id
		BFTClient client = new BFTClient(195);
		try {
			// generate crypto tools
			SecretKey key = CryptoUtil.getUserSecret();
			JSONObject result1 = testEventDG(key, client);
			System.out.println("result1: " + result1.toString());
			JSONObject result2 = testAddData(key, result1.getString("BlockHash"), result1.getString("EventHash"));
			System.out.println("result2: " + result2.toString());
			String HashID = result2.getString("HashID");
			testGetInventories(HashID, key);
			// testDisplayInventories();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			client.close();
		}
	}

	private static JSONObject testEventDG(SecretKey Sdata, BFTClient client) throws Exception {
		JSONObject event = new JSONObject();
		JSONObject dataObj = new JSONObject();
		UUID did = UUID.randomUUID();
		dataObj.put("DID", did.toString());
		// optional
		dataObj.put("index", 0);
		// key2 for patient
		String PKpat = CryptoUtil.getPublicKeyHex("key2");
		dataObj.put("PKpat", PKpat);
		String enrypted = CryptoUtil.encryptWithSecret(dataObj.toString(), Sdata);
		event.put("Content", enrypted);
		// create file digest
		MessageDigest md = MessageDigest.getInstance("SHA256");
		InputStream is = Files.newInputStream(Paths.get(path));
		/* Read decorated stream (dis) to EOF as normal... */
		DigestInputStream dis = new DigestInputStream(is, md);
		byte[] digest = md.digest();
		// to hex string
		String digestHex = Hex.encodeHexString(digest);
		event.put("Digest", digestHex);
		// key1 for healthcare provider
		String PKsp = CryptoUtil.getPublicKeyHex("key1");
		event.put("PKsp", PKsp);
		// assign event type
		event.put("Type", EVENT_DG);
		// sign the message
		String content = enrypted + digestHex + PKsp + EVENT_DG;
		// key1 for healthcare provider
		PrivateKey SKsp = CryptoUtil.getPrivateKey("key1");
		String signature = CryptoUtil.generateSignature(content, SKsp);
		event.put("Signature", signature);

		JSONObject reply = client.addEvent(event);
		System.out.println("reply: ");
		System.out.println(reply);
		JSONObject result = new JSONObject();
		// TODO blockhash and eventhash could be null, if event is failed to be added
		// into blockchain
		result.put("BlockHash", reply.getString("BlockHash"));
		result.put("EventHash", reply.getString("BlockHash"));
		return result;
	}

	private static void testDisplayInventories() {
		try {
			// send to the directory service
			JSONObject request = new JSONObject();
			request.put("operation", "check");
			HttpCallUtil http = new HttpCallUtil();
			JSONObject resp = http.httpCall(DIRECTORY_SERVICE_URL, request);
			System.out.println("Response: ");
			System.out.println(resp.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testGetInventories(String hashId, SecretKey key) {
		try {
			JSONObject dir = new JSONObject();
			dir.put("InvID", hashId);

			// send to the directory service
			JSONObject request = new JSONObject();
			request.put("operation", "get");
			request.put("content", dir);
			HttpCallUtil http = new HttpCallUtil();
			JSONObject resp = http.httpCall(DIRECTORY_SERVICE_URL, request);
			System.out.println("Response: ");
			System.out.println(resp.toString());
			String encrypted = resp.getString("value");
			String text = CryptoUtil.decryptWithSecret(encrypted, key);
			JSONObject content = new JSONObject(text);
			System.out.println("Original content: " + content.toString(4));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static JSONObject testAddData(SecretKey key, String blockHash, String eventHash) throws Exception {
		JSONObject result = new JSONObject();
		try {
			// create json-type directory
			JSONObject dir = new JSONObject();
			String userId = UUID.randomUUID().toString();
			String Sdata = Hex.encodeHexString(key.getEncoded());
			String hashId = CryptoUtil.hashUserID(userId, Sdata);
			dir.put("InvID", hashId);
			JSONObject content = new JSONObject();
			content.put("DID", UUID.randomUUID().toString());
			content.put("DataType", "Record");
			content.put("RecordType", "Type1");
			content.put("Date", "2018-08-20");
			content.put("URL", "http://192.168.56.1:8080/MedSessionPortal/data?=File1-ID");
			content.put("BlockHash", blockHash);
			content.put("EventHash", eventHash);
			System.out.println("content: " + content.toString(4));
			String encrypted = CryptoUtil.encryptWithSecret(content.toString(), key);
			dir.put("InvContent", encrypted);

			// send to the directory service
			JSONObject request = new JSONObject();
			request.put("operation", "insert");
			request.put("content", dir);
			HttpCallUtil http = new HttpCallUtil();
			result = http.httpCall(DIRECTORY_SERVICE_URL, request);
			System.out.println("Response: ");
			System.out.println(result.toString());
			result.put("HashID", hashId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
