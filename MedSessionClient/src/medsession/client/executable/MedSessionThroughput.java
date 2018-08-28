/**
 * 
 */
package medsession.client.executable;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;
import org.json.JSONObject;

import medsession.client.manager.BlockchainServiceManager;
import medsession.client.manager.DirectoryServiceManager;
import medsession.client.thread.DataQueryThread;
import medsession.client.thread.ThreadPool;
import medsession.client.util.PropertiesLoader;
import medsession.client.util.StringUtil;
import medsession.client.util.crypto.CryptoUtil;

/**
 * @author user
 *
 */
public class MedSessionThroughput {

	private static String tempfile = "C://Workspaces//eclipse-workspace//MedSessionClient//data//session4.txt";
	// private static String sessionId;
	private static final int THREAD_NUM = 1;
	// duration for calculating throughput, in mills
	private static final long duration = 200;
	// interval of two thread start, in mills
	private static final long DELAY_TO_START = 100L;
	//
	private static final long LOOP_SIZE = 10L;

	static {
		PropertiesLoader.loadPropertyFile();
	}

	/**
	 * 
	 */
	public MedSessionThroughput() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			createSession();

			RandomAccessFile file = new RandomAccessFile(tempfile, "r");
			String sid = file.readLine();
			file.close();

			System.out.println("Start load test ..............");
			long startTime = System.nanoTime();
			// ThreadPool pool = new ThreadPool();
			int j = 1;
			for (; j <= LOOP_SIZE; j++) {

				// pool.setDelayToStart(DELAY_TO_START);
				// init thread pool
				// pool.init(THREAD_NUM);

				// for (int i = 0; i < THREAD_NUM; i++) {
				// DataQueryThread thread = new DataQueryThread();
				// thread.sid = sid;
				// thread.clientId = i;
				// pool.spawn(thread);
				// }
				queryData(sid, j);
				// while(!pool.canClose()) {
				// Thread.sleep(1000);
				// }
				Thread.sleep(duration);
				// int count = pool.closeAndCountActive();
				System.out.println("Loop " + j);
			}
			long endTime = System.nanoTime();
			double duration = (endTime - startTime) / 1000000000d;
			double average = duration / LOOP_SIZE;
			NumberFormat formatter = new DecimalFormat("#0.00000");
			String time = formatter.format(average);
			System.out.println("Execution time: " + time + " sec");

			// System.out.println("Total number of run: " + (j * THREAD_NUM));
			// double throughput = (double) (THREAD_NUM - count);
			// System.out.println("Throughput: " + throughput);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void queryData(String sid, int clientId) throws Exception {

		JSONObject records = DirectoryServiceManager.getInventories(sid);
		// TODO valid reply by checking SHash
		JSONArray sections = records.getJSONArray("Sections");
		int size = sections.length();
		// for integrity verification
		boolean valid = true;
		for (int i = 0; i < size; i++) {
			JSONObject section = sections.getJSONObject(i);
			// decrypt EKreq
			String EKreq = section.getString("EKreq");
			PrivateKey SKeyreq = CryptoUtil.getPrivateKey("key" + 700);
			String EK = CryptoUtil.decryptWithPrivateKey(EKreq, SKeyreq);
			SecretKey EKey = CryptoUtil.getSecretKeyFromString(EK);
			String encrypted = section.getString("SectionContent");
			JSONObject content = new JSONObject(CryptoUtil.decryptWithSecret(encrypted, EKey));
			JSONArray entries = content.getJSONArray("Entry");
			int num = entries.length();
			// String digestHex = null;
			String blockHash = null;
			String eventHash = null;
			for (int j = 0; j < num; j++) {
				JSONObject entry = entries.getJSONObject(j);
				String location = entry.getString("Location");
				// digestHex = CryptoUtil.generateFileDigestHex(digestHex, location);
				blockHash = entry.getString("BlockHash");
				eventHash = entry.getString("EventHash");
			}
			JSONObject bcRecord = BlockchainServiceManager.getContent(blockHash, eventHash, clientId);
			// String digestHex2 = bcRecord.getString("Digest");
			// if (!digestHex.equals(digestHex2)) {
			// valid = false;
			// }
		}

		// System.out.println("All data are valid? " + valid);

	}

	private static void createSession() throws Exception {
		String path = "C://Workspaces//eclipse-workspace//MedSessionClient//data//mitdb//";
		UUID uuid = new UUID(0, 201);
		// for data inventory creation / update
		// String userId = uuid.toString();

		try {
			SecretKey Sdata = CryptoUtil.getUserSecret("key" + 201);
			String hashId = CryptoUtil.hashUserID(uuid.toString(), Hex.encodeHexString(Sdata.getEncoded()));
			String PKsp = CryptoUtil.getPublicKeyHex("key" + 1);
			String PKpat = CryptoUtil.getPublicKeyHex("key" + 201);
//
			JSONObject records = new JSONObject();
			JSONObject section = new JSONObject();
			JSONObject content = new JSONObject();
			JSONObject content2 = new JSONObject();
			String digestHex = null;
			for (int i = 100; i <= 109; i++) {

				String file = path + i + ".dat";
				// create file digest
				digestHex = CryptoUtil.generateFileDigestHex(digestHex, file);
				// UUID did = UUID.randomUUID();
				String name = "Marry";
				String summary = "This is a data summary";
				String dataType = "Health Care Record";
				String recordType = "Type1";
				String date = "2019-08-19 15:40:00";
				String location = path + i + ".dat";
				int index = i - 100;
				// encrypt content
				JSONObject dataObj = new JSONObject();
				dataObj.put("DID", String.valueOf(i));
				dataObj.put("Index", index);
				dataObj.put("PKpat", PKpat);
				String encrypted = CryptoUtil.encryptWithSecret(dataObj.toString(), Sdata);

				// key1 for healthcare provider
				PrivateKey SKsp = CryptoUtil.getPrivateKey("key" + 1);

				JSONObject result = BlockchainServiceManager.addEventDG(PKsp, SKsp, digestHex, encrypted, i);
				// String tempfile =
				// "C://Workspaces//eclipse-workspace//MedSessionClient//data//records.txt";
				// FileOutputStream fos = new FileOutputStream(tempfile, true);
				// String line = result.getString("BlockHash") + "," +
				// result.getString("EventHash")
				// + System.lineSeparator();
				// fos.write(line.getBytes());
				// fos.flush();
				// fos.close();

				// for session creation at directory service
				JSONObject record = new JSONObject();
				record.put("DID", String.valueOf(i));
				record.put("Index", String.valueOf(i));
				record.put("Name", name);
				record.put("Summary", summary);
				record.put("DataType", dataType);
				record.put("RecordType", recordType);
				record.put("Date", date);
				record.put("Location", location);
				record.put("BlockHash", result.getString("BlockHash"));
				record.put("EventHash", result.getString("EventHash"));
				content.append("Entry", record);

				// for session creation at blockchain
				JSONObject record2 = new JSONObject();
				record2.put("DID", String.valueOf(i));
				record2.put("Index", String.valueOf(i));
				content2.append("Entry", record2);
			}
			String encrypted = CryptoUtil.encryptWithSecret(content.toString(), Sdata);
			section.put("SectionContent", encrypted);
			section.put("SectionID", hashId);
			String SdataHex = StringUtil.getStringFromKey(Sdata);
			PublicKey PKeysp = CryptoUtil.getPublicKey("key" + 1);
			String EKsp = CryptoUtil.encryptWithPublicKey(SdataHex, PKeysp);
			PublicKey PKeyreq = CryptoUtil.getPublicKey("key" + 700);
			String EKreq = CryptoUtil.encryptWithPublicKey(SdataHex, PKeyreq);
			PublicKey PKeypat = CryptoUtil.getPublicKey("key" + 201);
			String EKpat = CryptoUtil.encryptWithPublicKey(SdataHex, PKeypat);
			section.put("EKsp", EKsp);
			section.put("EKreq", EKreq);
			section.put("EKpat", EKpat);

			// store session into Blockchain
			String PKreq = CryptoUtil.getPublicKeyHex("key" + 700);
			content2.put("PKreq", PKreq);
			String encrypted2 = CryptoUtil.encryptWithPublicKey(content2.toString(), PKeypat);
			PrivateKey SKpat = CryptoUtil.getPrivateKey("key" + 201);
			JSONObject result = BlockchainServiceManager.addEventSC(PKpat, SKpat, encrypted2, 11);

			records.append("Sections", section);
			String sid = result.getString("EventHash");
			records.put("SID", sid);
			String SHash = CryptoUtil.hash(records.toString());
			records.put("SHash", SHash);
			DirectoryServiceManager.addInventory(sid, records.toString(), result.getString("BlockHash"),
					result.getString("EventHash"));

			FileOutputStream fos = new FileOutputStream(tempfile, false);
			StringBuilder sb = new StringBuilder();
			// String SKreq = CryptoUtil.getPrivateKeyHex("key" + 700);
			// sb.append(sid).append(System.lineSeparator()).append(SKreq).append(System.lineSeparator());
			// fos.write(sb.toString().getBytes());
			sb.append(sid).append(System.lineSeparator());
			fos.write(sb.toString().getBytes());
			fos.flush();
			fos.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
