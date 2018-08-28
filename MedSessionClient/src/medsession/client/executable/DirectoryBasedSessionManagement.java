package medsession.client.executable;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import medsession.client.manager.BlockchainServiceManager;
import medsession.client.manager.DirectoryServiceManager;
import medsession.client.util.PropertiesLoader;
import medsession.client.util.StringUtil;
import medsession.client.util.crypto.CryptoUtil;

public class DirectoryBasedSessionManagement {

	static {
		PropertiesLoader.loadPropertyFile();
	}

	public DirectoryBasedSessionManagement() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		try {
			for (int i = 0; i < 10; i++) {
				System.out.println("Create session");
				createSession();

				System.out.println("Remove session");
				removeSession();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			;
		}
	}

	private static void removeSession() throws Exception {
		String path = "C://Workspaces//eclipse-workspace//MedSessionClient//data//session3.txt";
		try {
			RandomAccessFile file = new RandomAccessFile(path, "r");
			String sid = file.readLine();
			file.close();

			DirectoryServiceManager.removeSession(sid);

			JSONObject query = DirectoryServiceManager.getInventories(sid);
			System.out.println("Session query result: " + query.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

				// JSONObject result = BlockchainServiceManager.addEventDG(PKsp, SKsp,
				// digestHex, encrypted, i);

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
				// record.put("BlockHash", result.getString("BlockHash"));
				// record.put("EventHash", result.getString("EventHash"));
				// for creating session only
				record.put("BlockHash", "ffe45ff3ec82f0e9c61bc1f4ce046a5395eb864f3f9098534ef8d56c29e6ac3f");
				record.put("EventHash", "194b8d13776cd1e11af3f2a075cd0da3d054e3bdb81f326d66ab42afa8c6598e");

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

			String tempfile = "C://Workspaces//eclipse-workspace//MedSessionClient//data//session3.txt";
			FileOutputStream fos = new FileOutputStream(tempfile, false);
			StringBuilder sb = new StringBuilder();
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
