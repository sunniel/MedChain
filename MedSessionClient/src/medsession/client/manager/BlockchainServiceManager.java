/**
 * 
 */
package medsession.client.manager;

import java.security.PrivateKey;

import org.json.JSONObject;

import medsession.client.blockchain.BFTClient;
import medsession.client.util.crypto.CryptoUtil;

/**
 * @author user
 *
 */
public class BlockchainServiceManager {

	private static final int EVENT_DG = 0;
	private static final int EVENT_SC = 1;
	private static final int EVENT_SR = 2;

	public static JSONObject addEventDG(String PKsp, PrivateKey SKsp, String digestHex, String content, int clientId)
			throws Exception {
		BFTClient client = new BFTClient(clientId);
		JSONObject event = new JSONObject();
		String enrypted = content;
		event.put("Content", enrypted);
		event.put("Digest", digestHex);
		event.put("PKsp", PKsp);
		// assign event type
		event.put("Type", EVENT_DG);
		// sign the message
		String text = enrypted + digestHex + PKsp + EVENT_DG;
		// key1 for healthcare provider
		String signature = CryptoUtil.generateSignature(text, SKsp);

		event.put("Signature", signature);

		JSONObject reply = client.addEvent(event);
		System.out.println("reply: ");
		System.out.println(reply);
		JSONObject result = new JSONObject();
		// TODO blockhash and eventhash could be null, if event is failed to be added
		// into blockchain
		result.put("BlockHash", reply.getString("BlockHash"));
		result.put("EventHash", reply.getString("EventHash"));
		client.close();
		return result;
	}

	public static JSONObject addEventSC(String PKsp, PrivateKey SKsp, String content, int clientId) throws Exception {
		BFTClient client = new BFTClient(clientId);
		JSONObject event = new JSONObject();
		String enrypted = content;
		event.put("Content", enrypted);
		event.put("PKsp", PKsp);
		// assign event type
		event.put("Type", EVENT_SC);
		// sign the message
		String text = enrypted + PKsp + EVENT_SC;
		// key1 for healthcare provider
		String signature = CryptoUtil.generateSignature(text, SKsp);
		event.put("Signature", signature);

		JSONObject reply = client.addEvent(event);
		System.out.println("reply: ");
		System.out.println(reply);
		JSONObject result = new JSONObject();
		// TODO blockhash and eventhash could be null, if event is failed to be added
		// into blockchain
		result.put("BlockHash", reply.getString("BlockHash"));
		result.put("EventHash", reply.getString("EventHash"));
		client.close();
		return result;
	}

	public static JSONObject addEventSR(String PKsp, PrivateKey SKsp, String content, int clientId) throws Exception {
		BFTClient client = new BFTClient(clientId);
		JSONObject event = new JSONObject();
		String enrypted = content;
		event.put("Content", enrypted);
		event.put("PKsp", PKsp);
		// assign event type
		event.put("Type", EVENT_SR);
		// sign the message
		String text = enrypted + PKsp + EVENT_SR;
		// key1 for healthcare provider
		String signature = CryptoUtil.generateSignature(text, SKsp);

		event.put("Signature", signature);

		JSONObject reply = client.addEvent(event);
		System.out.println("reply: ");
		System.out.println(reply);
		JSONObject result = new JSONObject();
		// TODO blockhash and eventhash could be null, if event is failed to be added
		// into blockchain
		result.put("BlockHash", reply.getString("BlockHash"));
		result.put("EventHash", reply.getString("EventHash"));
		client.close();
		return result;
	}

	public static JSONObject getContent(String blockHash, String eventHash, int clientId) throws Exception {
		JSONObject replyContent = null;
		BFTClient client = new BFTClient(clientId);
		JSONObject reply = client.get(blockHash, eventHash);
		System.out.println("Response: ");
		System.out.println(reply.toString());
		if (reply.getString("Result").equals("OK")) {
			replyContent = reply.getJSONObject("Content");
		}
		client.close();
		return replyContent;
	}

	/**
	 * @deprecated
	 */
	// public static void warmup(String did, int index, String PKsp, String PKpat,
	// SecretKey Sdata, String digestHex,
	// int clientId) throws Exception {
	// BFTClient client = new BFTClient(clientId);
	// JSONObject event = new JSONObject();
	// JSONObject dataObj = new JSONObject();
	// dataObj.put("DID", did);
	// // optional
	// dataObj.put("index", 0);
	// dataObj.put("PKpat", PKpat);
	// String enrypted = CryptoUtil.encryptWithSecret(dataObj.toString(), Sdata);
	// event.put("Content", enrypted);
	// event.put("Digest", digestHex);
	// event.put("PKsp", PKsp);
	// // assign event type
	// event.put("Type", EVENT_DG);
	// // sign the message
	// String content = enrypted + digestHex + PKsp + EVENT_DG;
	// // key1 for healthcare provider
	// PrivateKey SKsp = CryptoUtil.getPrivateKey("key1");
	// String signature = CryptoUtil.generateSignature(content, SKsp);
	// event.put("Signature", signature);
	//
	// JSONObject reply = client.addData(event);
	// System.out.println("reply: ");
	// System.out.println(reply);
	// JSONObject result = new JSONObject();
	// // TODO blockhash and eventhash could be null, if event is failed to be added
	// // into blockchain
	// result.put("BlockHash", reply.getString("BlockHash"));
	// result.put("EventHash", reply.getString("EventHash"));
	// client.close();
	// }
}
