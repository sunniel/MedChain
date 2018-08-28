package medsession.client.blockchain;

import org.json.JSONObject;

import bftsmart.tom.ServiceProxy;

public class BFTClient {

	private ServiceProxy clientProxy = null;
	private int id;

	public BFTClient(int clientId) {

		// clientProxy = new ServiceProxy(clientId);
		id = clientId;
		clientProxy = new ServiceProxy(id);
	}

	// public void warmup(JSONObject event) throws Exception {
	//
	// JSONObject request = new JSONObject();
	// request.put("operation", "warmup");
	// request.put("event", event);
	// System.out.println("event: " + event.toString(4));
	// System.out.println("request: " + request.toString());
	// byte[] data = clientProxy.invokeOrdered(request.toString().getBytes());
	// if (data == null) {
	// System.out.println("warmup error, empty reply");
	// }
	// }

	public JSONObject addEvent(JSONObject event) throws Exception {

		try {
			JSONObject request = new JSONObject();
			request.put("operation", "addEvent");
			request.put("event", event);
			System.out.println("event: " + event.toString(4));
			System.out.println("request: " + request.toString());
			byte[] data = clientProxy.invokeOrdered(request.toString().getBytes());
			if (data != null) {
				String content = new String(data, "UTF-8");
				System.out.println("content: " + content);
				JSONObject reply = new JSONObject(content);
				return reply;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			String msg = e.getMessage();
			if (msg.trim().equals("Received n-f replies without f+1 of them matching.")) {

			}
		}
		return null;
	}

	public JSONObject get(String blockHash, String eventHash) throws Exception {

		JSONObject request = new JSONObject();
		request.put("operation", "getEvent");
		JSONObject value = new JSONObject();
		value.put("BlockHash", blockHash);
		value.put("EventHash", eventHash);
		request.put("value", value);
		System.out.println("request: " + request);
		byte[] data = clientProxy.invokeUnordered(request.toString().getBytes());
		if (data != null) {
			String content = new String(data, "UTF-8");
			System.out.println("replied content from BFT: " + content);
			JSONObject reply = new JSONObject(content);
			return reply;
		}
		return null;
	}

	public void close() {
		clientProxy.close();
	}
}
