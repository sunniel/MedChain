package medsession.client.executable;

import java.io.FileReader;
import java.security.PrivateKey;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import medsession.client.manager.BlockchainServiceManager;
import medsession.client.util.PropertiesLoader;
import medsession.client.util.crypto.CryptoUtil;

public class UpdateThroughBlockchain {

	private static final String path = "C://Workspaces//eclipse-workspace//MedSessionClient//upload//SampleRecord1.dat";
	private static final int EVENT_DG = 0;
	private static final int EVENT_SC = 1;

	static {
		// read property file by invoking static method on ChordImpl
		PropertiesLoader.loadPropertyFile();
	}

	public static void main(String[] args) {
		String oldPath = args[0];
		String newPath = args[1];
		int userId = Integer.valueOf(args[2]);
		try {
			System.out.println("add old inventory from: " + oldPath);
			insertInventory(userId, oldPath);

			System.out.println("add new inventory from: " + newPath);
			long startTime = System.nanoTime();
			insertInventory(userId, newPath);
			long endTime = System.nanoTime();
			double duration = (endTime - startTime) / 1000000000d;
			NumberFormat formatter = new DecimalFormat("#0.00000");
			String time = formatter.format(duration);
			System.out.println("Execution time: " + time + " second(s)");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private static void insertInventory(int userId, String path) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject inventory = (JSONObject) parser.parse(new FileReader(path));
			System.out.println("inventory: " + inventory.toString());

			UUID uuid = new UUID(0, userId);
			SecretKey Sdata = CryptoUtil.getUserSecret("key" + (userId - 100));
			String hashId = CryptoUtil.hashUserID(uuid.toString(), Hex.encodeHexString(Sdata.getEncoded()));
			String PKsp = CryptoUtil.getPublicKeyHex("key" + 1);
			String PKpat = CryptoUtil.getPublicKeyHex("key" + (userId - 100));
			// create file digest
			String digestHex = CryptoUtil.generateFileDigestHex(path);
			// encrypt content
			JSONObject dataObj = new JSONObject();
			String did = (String) inventory.get("DID");
			dataObj.put("DID", did);
			// optional
			dataObj.put("index", 0);
			dataObj.put("PKpat", PKpat);
			String encrypted = CryptoUtil.encryptWithSecret(dataObj.toString(), Sdata);
			// key1 for healthcare provider
			PrivateKey SKsp = CryptoUtil.getPrivateKey("key" + 1);

			BlockchainServiceManager.addEventDG(PKsp, SKsp, digestHex, encrypted, 195);

			System.out.println("HashID: " + hashId);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
