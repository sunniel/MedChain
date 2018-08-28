/**
 * 
 */
package medsession.client.load;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.UUID;
import java.util.Vector;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.JSONObject;

import medsession.client.thread.MedBlockGenerationThread;
import medsession.client.thread.ThreadPool;
import medsession.client.util.PropertiesLoader;
import medsession.client.util.crypto.CryptoUtil;

/**
 * @author user
 * 
 *         Insert record for users with ID = 101 - 600, each one with 500
 *         records from 1 healthcare provider. Record ID = user ID * 1000 +
 *         sequence number, with seq = 1 - 500. Public key alias = 'key' + (user
 *         ID). Secret key alias = "key" + (user ID - 100).
 *
 */
public class MedBlockGenerationClient {

	private static final String path = "C://Workspaces//eclipse-workspace//MedSessionClient//upload//SampleRecord1.dat";
	private static final int EVENT_DG = 0;
	private static final int EVENT_SC = 1;

	static {
		// read property file by invoking static method on ChordImpl
		PropertiesLoader.loadPropertyFile();
	}

	/**
	 * @param args
	 *            Set args[0] = args[1] to avoid error
	 */
	public static void main(String[] args) {
//		int initId = Integer.valueOf(args[0]);
//		int upperBound = Integer.valueOf(args[1]);
		 int initId = 101;
		 int upperBound = 110;

		Vector<JSONObject> jhashes = new Vector<JSONObject>();

		// any client id
		// BFTClient client = new BFTClient(195);
		String fileName = "C://Workspaces//eclipse-workspace//MedSessionClient//data//breadcrumbs.csv";
		File breadcrumbs = new File(fileName);
		CSVPrinter csvPrinter = null;
		ThreadPool pool = new ThreadPool();
		// init thread pool
		pool.init(10);
		try {
			// create file digest
			String digestHex = CryptoUtil.generateFileDigestHex(path);

			int counter = 0;
			for (int i = initId; i < upperBound; i++) {
				// for (int i = 101; i <= 600; i++) {
				// generate crypto tools
				String PKpat = CryptoUtil.getPublicKeyHex("key" + i);
				SecretKey Sdata = CryptoUtil.getUserSecret("key" + (i - 100));
				// only one healthcare provider
				String PKsp = CryptoUtil.getPublicKeyHex("key" + 1);
				// pool.init(100);
				for (int j = 0; j < 10; j++) {
					UUID did = new UUID(0, (i * 100 + j));
					UUID uuid = new UUID(0, i);
					String userId = uuid.toString();
					String hashId = CryptoUtil.hashUserID(userId, Hex.encodeHexString(Sdata.getEncoded()));
					String name = "DataName";
					String summary = "Data summary";
					String dataType = "Record";
					String recordType = "Type1";
					String date = "2019-08-19 15:40:00";
					String location = "http://192.168.56.1:8080/MedSessionPortal/data?=File1-ID";

					// create json-type directory
					JSONObject dir = new JSONObject();
					dir.put("InvID", hashId);
					JSONObject content = new JSONObject();
					content.put("DID", did);
					content.put("DataName", name);
					content.put("Summary", summary);
					content.put("DataType", dataType);
					content.put("RecordType", recordType);
					content.put("Date", date);
					content.put("URL", location);
					System.out.println("content: " + content.toString(4));
					String inventory = CryptoUtil.encryptWithSecret(content.toString(), Sdata);

					JSONObject dataObj = new JSONObject();
					dataObj.put("DID", did);
					// optional
					dataObj.put("index", 0);
					dataObj.put("PKpat", PKpat);
					String enrypted = CryptoUtil.encryptWithSecret(dataObj.toString(), Sdata);
					PrivateKey SKsp = CryptoUtil.getPrivateKey("key" + 1);

					MedBlockGenerationThread thread = new MedBlockGenerationThread();
					int clientId = counter;
					thread.clientId = clientId;
					thread.content = enrypted;
					thread.dataIndex = j + 1;
					thread.dataType = dataType;
					thread.date = date;
					thread.digestHex = digestHex;
					thread.hashId = hashId;
					thread.location = location;
					thread.name = name;
					thread.PKpat = PKpat;
					thread.PKsp = PKsp;
					thread.recordType = recordType;
					thread.Sdata = Sdata;
					thread.summary = summary;
					thread.userId = userId;
					thread.did = did.toString();
					thread.SKsp = SKsp;
					thread.inventory = inventory;
					// thread.client = client;
					thread.jHashes = jhashes;

					pool.spawn(thread);

					// UUID did = new UUID(0, (i * 100 + j));
					// JSONObject result1 = BlockchainServiceManager.addEventDG(did.toString(), j +
					// 1, PKsp, PKpat, Sdata,
					// digestHex, client);
					// System.out.println("result1: " + result1.toString());
					//
					// UUID uuid = new UUID(0, i);
					// String userId = uuid.toString();
					// String hashId = CryptoUtil.hashUserID(userId,
					// Hex.encodeHexString(Sdata.getEncoded()));
					// String name = "DataName";
					// String summary = "Data summary";
					// String dataType = "Record";
					// String recordType = "Type1";
					// String date = "2019-08-19 15:40:00";
					// String location = "http://192.168.56.1:8080/MedSessionPortal/data?=File1-ID";
					// JSONObject result2 = DirectoryServiceManager.addInventory(hashId,
					// did.toString(), name, summary,
					// dataType, recordType, date, location, Sdata, result1.getString("BlockHash"),
					// result1.getString("EventHash"));
					//
					// Write records to a CSV file
					// csvPrinter.printRecord(result1.getString("BlockHash"),
					// result1.getString("EventHash"));

					counter++;
					Thread.sleep(200);
				}
			}
			while (!pool.canClose()) {
				Thread.sleep(1000);
			}
			pool.close();
			System.out.println("counter: " + counter);

			// create breadcrumbs
			if (!breadcrumbs.exists()) {
				breadcrumbs.createNewFile();
			}
			FileWriter writer = new FileWriter(fileName, true);
			csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
			for (JSONObject hash : jhashes) {
				csvPrinter.printRecord(hash.getString("BlockHash"), hash.getString("EventHash"));
			}
			// String HashID = result2.getString("HashID");
			// DirectoryServiceManager.getInventories(HashID, key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// if (client != null) {
			// client.close();
			// }
			if (csvPrinter != null) {
				try {
					csvPrinter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

}
