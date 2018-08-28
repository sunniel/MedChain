/**
 * 
 */
package medsession.client.manager;

import org.json.JSONObject;

import medsession.client.util.HttpCallUtil;

/**
 * @author user
 *
 */
public class DirectoryServiceManager {
	private static String DIRECTORY_SERVICE_URL = "http://192.168.56.1:8080/DirectoryService/directory";
	private static String OPERATION_INSERT = "insert";
	private static String OPERATION_REMOVE = "remove";

	public static void displayInventories() {
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

	public static JSONObject getInventories(String hashId) {
		JSONObject object = new JSONObject();
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
			String content = resp.getString("value");
			if (content != null && !content.trim().equals("")) {
				object = new JSONObject(content);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}

	public static JSONObject addInventory(String hashId, String inventory, String blockHash, String eventHash)
			throws Exception {
		JSONObject result = new JSONObject();
		try {
			// create json-type directory
			JSONObject dir = new JSONObject();
			dir.put("InvID", hashId);
			dir.put("InvContent", inventory);
			dir.put("BlockHash", blockHash);
			dir.put("EventHash", eventHash);

			// send to the directory service
			JSONObject request = new JSONObject();
			request.put("operation", OPERATION_INSERT);
			request.put("content", dir);
			System.out.println("Directory service request: " + request.toString(4));
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

	public static void removeSession(String sid) throws Exception {
		JSONObject result = new JSONObject();
		try {
			// create json-type directory
			JSONObject dir = new JSONObject();
			dir.put("InvID", sid);

			// send to the directory service
			JSONObject request = new JSONObject();
			request.put("operation", OPERATION_REMOVE);
			request.put("content", dir);
			System.out.println("Directory service request: " + request.toString(4));
			HttpCallUtil http = new HttpCallUtil();
			result = http.httpCall(DIRECTORY_SERVICE_URL, request);
			System.out.println("Response: ");
			System.out.println(result.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
