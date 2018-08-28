package medsession.client.executable;

import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import medsession.client.manager.DirectoryServiceManager;
import medsession.client.util.PropertiesLoader;
import medsession.client.util.crypto.CryptoUtil;

public class UpdateThroughDirectory {

	static {
		// read property file by invoking static method on ChordImpl
		PropertiesLoader.loadPropertyFile();
	}

	/**
	 * @param args
	 *            Set args[0] = args[1] to avoid error
	 */
	public static void main(String[] args) {
		String oldPath = args[0];
		String newPath = args[1];
		int userId = Integer.valueOf(args[2]);
		try {

			System.out.println("Insert old inventory from: " + oldPath);
			insertInventory(userId, oldPath);

			System.out.println("Inser new inventory from: " + newPath);
			long startTime = System.nanoTime();
			insertInventory(userId, newPath);
			long endTime = System.nanoTime();
			double duration = (endTime - startTime) / 1000000000d;
			NumberFormat formatter = new DecimalFormat("#0.00000");
			String time = formatter.format(duration);
			System.out.println("Execution time: " + time);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private static void insertInventory(int userId, String path) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject inventory = (JSONObject) parser.parse(new FileReader(path));

			UUID uuid = new UUID(0, userId);
			SecretKey Sdata = CryptoUtil.getUserSecret("key" + (userId - 100));
			String hashId = CryptoUtil.hashUserID(uuid.toString(), Hex.encodeHexString(Sdata.getEncoded()));

			// create json-type directory
			JSONObject content = new JSONObject();
			content.put("DID", (String) inventory.get("DID"));
			content.put("DataName", (String) inventory.get("name"));
			content.put("Summary", (String) inventory.get("Summary"));
			content.put("DataType", (String) inventory.get("DataType"));
			content.put("RecordType", (String) inventory.get("RecordType"));
			content.put("Date", (String) inventory.get("Date"));
			content.put("URL", (String) inventory.get("Location"));
			String directory = CryptoUtil.encryptWithSecret(content.toString(), Sdata);

			DirectoryServiceManager.addInventory(hashId, directory, (String) inventory.get("BlockHash"),
					(String) inventory.get("EventHash"));

			System.out.println("HashID: " + hashId);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
