/**
 * 
 */
package medsession.client.executable;

import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import medsession.client.thread.ThreadPool;
import medsession.client.thread.TransactionGenerationThread;
import medsession.client.util.PropertiesLoader;
import medsession.client.util.crypto.CryptoUtil;

/**
 * @author user
 *
 */
public class TransactionRateScalability {

	private static final String path = "C://Workspaces//eclipse-workspace//MedSessionClient//upload//SampleRecord1.dat";

	static {
		PropertiesLoader.loadPropertyFile();
	}

	/**
	 * 
	 */
	public TransactionRateScalability() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// minute * second * millisecond * microsecond * nanosecond
		long duration = 5L * 60L * 1000L * 1000L * 1000L; // in nano-seconds
//		long duration = 1L * 10L * 1000L * 1000L * 1000L; // in nano-seconds

		// concurrency = upperBound + 1 - initId
		int initId = 101;
		int upperBound = 300;
		NumberFormat formatter = new DecimalFormat("#0.000000");
		ThreadPool pool = new ThreadPool();
		try {
			// create file digest
			String digestHex = CryptoUtil.generateFileDigestHex(path);

			long startTime = System.nanoTime();
			long endTime = startTime + duration;
			long current = startTime;
			int index = 0;
			do {
				// init thread pool
				int concurrency = upperBound + 1 - initId;
				pool.init(concurrency);
				for (int i = initId; i <= upperBound; i++) {
					// generate crypto tools
					String PKpat = CryptoUtil.getPublicKeyHex("key" + initId);
					SecretKey Sdata = CryptoUtil.getUserSecret("key" + (initId - 100));
					// only one healthcare provider
					String PKsp = CryptoUtil.getPublicKeyHex("key" + 1);
					UUID did = new UUID(0, index++);
					UUID uuid = new UUID(0, i);
					// for BFT session identification
//					int clientId = (i <= 500 ? i : (i - 500));
//					int clientId = (index <= 500 ? index : (index - 500));
					int clientId = index;
//					int clientId = i - initId;

					TransactionGenerationThread thread = new TransactionGenerationThread();
					initializeThread(thread, uuid.toString(), clientId, did.toString(), digestHex, PKpat, Sdata, PKsp);

					pool.spawn(thread);

					Thread.sleep(100);
				}
				
//				Thread.sleep(500);

				// wait threads termination
//				while (!pool.canClose()) {
//					// wait for 1 second
//					Thread.sleep(100);
//				}
				// terminate the pool
				pool.close();

				current = System.nanoTime();
			} while (current < endTime);

			// calculate the throughput, i.e., number of transactions per second
			System.out.println("Start time: " + startTime);
			System.out.println("End time: " + endTime);
			System.out.println("Current time: " + current);
			System.out.println("Duration: " + formatter.format((current - startTime) / 1000000000d) + " second(s)");

			System.out.println("Count: " + index);
			int active = pool.active();
			System.out.println("Active threads: " + active);
			double throughput = (index - active) / ((current - startTime) / (double) 1000000000d);
//			double throughput = index / ((current - startTime) / (double) 1000000000d);
			System.out.println("Throughput: " + throughput);
			System.out.println("total request: " + index);
			throughput = index / ((current - startTime) / (double) 1000000000d);
			System.out.println("Throughput: " + throughput);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private static void initializeThread(TransactionGenerationThread thread, String uid, int cid, String did,
			String digestHex, String PKpat, SecretKey Sdata, String PKsp) {
		try {
			String userId = uid;
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

			thread.clientId = cid;
			thread.content = enrypted;
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
			thread.SKsp = SKsp;
			thread.inventory = inventory;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
