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
import medsession.client.util.PropertiesLoader;
import medsession.client.util.StringUtil;
import medsession.client.util.crypto.CryptoUtil;

public class MedSessionMultiDataAccess {

	private static final int EVENT_DG = 0;
	private static final int EVENT_SC = 1;

	static {
		PropertiesLoader.loadPropertyFile();
	}

	public static void main(String[] args) {
		try {
			// System.out.println("add data");
			// for (int i = 0; i < 100; i++) {
//			 createSession();
			// }

			System.out.println("Retrieve data");
			long startTime = System.nanoTime();
			for (int i = 0; i < 10; i++) {
				retrieveSessionData();
			}
			long endTime = System.nanoTime();
			double duration = (endTime - startTime) / 1000000000d;
			double average = duration / 10.0;
			NumberFormat formatter = new DecimalFormat("#0.000000");
			// String time = formatter.format(duration);
			String time = formatter.format(average);
			System.out.println("Execution time: " + time + " second(s)");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			;
		}
	}

	private static void retrieveSessionData() throws Exception {
		String path = "C://Workspaces//eclipse-workspace//MedSessionClient//data//session.txt";
		RandomAccessFile file = new RandomAccessFile(path, "r");
		String sid = file.readLine();
		// String SKreq = file.readLine();
		file.close();

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
			String digestHex = null;
			String blockHash = null;
			String eventHash = null;
			for (int j = 0; j < num; j++) {
				JSONObject entry = entries.getJSONObject(j);
				String location = entry.getString("Location");
				digestHex = CryptoUtil.generateFileDigestHex(digestHex, location);
				blockHash = entry.getString("BlockHash");
				eventHash = entry.getString("EventHash");
			}
			JSONObject bcRecord = BlockchainServiceManager.getContent(blockHash, eventHash, i);
			String digestHex2 = bcRecord.getString("Digest");
			if (!digestHex.equals(digestHex2)) {
				valid = false;
			}
		}

		System.out.println("All data are valid? " + valid);
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

			JSONObject records = new JSONObject();
			JSONObject section = new JSONObject();
			JSONObject content = new JSONObject();
			JSONObject content2 = new JSONObject();
			String digestHex = null;
			for (int i = 100; i <= 129; i++) {

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

			String tempfile = "C://Workspaces//eclipse-workspace//MedSessionClient//data//session.txt";
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
