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
public class MedBlockMain {
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
			String blockHash = result1.getString("BlockHash");
			String eventHash = result1.getString("EventHash");
			testGetEvent(blockHash, eventHash, client, key);
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
		dataObj.put("DID", UUID.randomUUID().toString());
		dataObj.put("DataType", "Record");
		dataObj.put("RecordType", "Type1");
		dataObj.put("Date", "2018-08-20");
		dataObj.put("URL", "http://192.168.56.1:8080/MedSessionPortal/data?=File1-ID");
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
		result.put("EventHash", reply.getString("EventHash"));
		return result;
	}

	private static void testGetEvent(String blockHash, String eventHash, BFTClient client, SecretKey Sdata) {
		try {
			JSONObject reply = client.get(blockHash, eventHash);
			System.out.println("Response: ");
			System.out.println(reply.toString());
			if (reply.getString("Result").equals("OK")) {
				JSONObject replyContent = reply.getJSONObject("Content");
				String hex = replyContent.getString("Content");
				String content = CryptoUtil.decryptWithSecret(hex, Sdata);
				JSONObject object = new JSONObject(content);
				System.out.println("Content: " + object);
			}
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
}
