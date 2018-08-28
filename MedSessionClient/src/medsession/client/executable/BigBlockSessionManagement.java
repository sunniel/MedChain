package medsession.client.executable;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONObject;

import medsession.client.manager.BlockchainServiceManager;
import medsession.client.util.PropertiesLoader;
import medsession.client.util.StringUtil;
import medsession.client.util.crypto.CryptoUtil;

public class BigBlockSessionManagement {

	static {
		PropertiesLoader.loadPropertyFile();
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
		String path = "C://Workspaces//eclipse-workspace//MedSessionClient//data//session2.txt";
		try {
			FileReader fileReader = new FileReader(path);
			CSVParser csvFileParser = new CSVParser(fileReader, CSVFormat.DEFAULT);
			CSVRecord csvRecord = csvFileParser.getRecords().get(0);
			// eventHash is SID
			String eventHash = csvRecord.get(1);
			csvFileParser.close();
//			JSONObject content = new JSONObject();
//			content.put("SID", eventHash);
			String PKpat = CryptoUtil.getPublicKeyHex("key" + 201);
			PrivateKey SKpat = CryptoUtil.getPrivateKey("key" + 201);
			JSONObject result = BlockchainServiceManager.addEventSR(PKpat, SKpat, eventHash, 12);
			System.out.println("Session removal BlockHash: " + result.getString("BlockHash"));
			System.out.println("Session removal EventHash: " + result.getString("EventHash"));

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

//				JSONObject result = BlockchainServiceManager.addEventDG(PKsp, SKsp, digestHex, encrypted, i);

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
//				record.put("BlockHash", result.getString("BlockHash"));
//				record.put("EventHash", result.getString("EventHash"));
				// for session creation only
				record.put("BlockHash", "cfcd16ac601d9223492f6b0435cde444ffe0ddccb82b5537c6302e61ad74568a");
				record.put("EventHash", "dfed4ef7455df3547c4bd96082b5ad22ab7eabcf0621fa74c4b40d427ebfeb91");
				
				content.append("Entry", record);
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
			PrivateKey SKpat = CryptoUtil.getPrivateKey("key" + 201);
			records.append("Sections", section);
			records.append("PKreq", PKreq);
			JSONObject result = BlockchainServiceManager.addEventSC(PKpat, SKpat, records.toString(), 11);

			String tempfile = "C://Workspaces//eclipse-workspace//MedSessionClient//data//session2.txt";
			FileOutputStream fos = new FileOutputStream(tempfile, false);
			StringBuilder sb = new StringBuilder();
			sb.append(result.getString("BlockHash")).append(",").append(result.getString("EventHash")).append(System.lineSeparator());
			fos.write(sb.toString().getBytes());
			fos.flush();
			fos.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
