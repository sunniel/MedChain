package medsession.client.executable;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONObject;

import medsession.client.manager.BlockchainServiceManager;
import medsession.client.util.PropertiesLoader;
import medsession.client.util.crypto.CryptoUtil;

public class MedBlockMultiDataAccess {

	static {
		PropertiesLoader.loadPropertyFile();
	}

	public static void main(String[] args) {
		try {
//			for(int i = 0; i <100; i++)
//			{
//				 System.out.println("add data");
//				 insertData();				
//			}

			System.out.println("Retrieve data");
			long startTime = System.nanoTime();
			for (int i = 0; i < 10; i++) {
				retrieveData();
			}
			long endTime = System.nanoTime();
			double duration = (endTime - startTime) / 1000000000d;
			double average = duration / 10.0;
			NumberFormat formatter = new DecimalFormat("#0.000000");
			String time = formatter.format(average);
			System.out.println("Execution time: " + time + " second(s)");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			;
		}
	}

	private static void retrieveData() throws Exception {
		String path = "C://Workspaces//eclipse-workspace//MedSessionClient//data//records.txt";
		FileReader fileReader = new FileReader(path);
		CSVParser csvFileParser = new CSVParser(fileReader, CSVFormat.DEFAULT);
		List<JSONObject> records = new ArrayList<JSONObject>();
		int i = 0;
		SecretKey Sdata = CryptoUtil.getUserSecret("key" + 201);
		for (CSVRecord csvRecord : csvFileParser) {
			String blockHash = csvRecord.get(0);
			String eventHash = csvRecord.get(1);

			JSONObject content = BlockchainServiceManager.getContent(blockHash, eventHash, i);
			records.add(content);

			i++;
		}
		csvFileParser.close();
		fileReader.close();

		// Retrieve records and verify integrity
		boolean valid = true;
		for (JSONObject record : records) {
			String hex = record.getString("Content");
			String content = CryptoUtil.decryptWithSecret(hex, Sdata);
			JSONObject object = new JSONObject(content);
			String location = object.getString("Location");
			String digestHex = CryptoUtil.generateFileDigestHex(location);
			String digestHex2 = record.getString("Digest");
			int index = object.getInt("Index");
			if (digestHex.equals(digestHex2)) {
				System.out.println("valid data " + index);
			} else {
				valid = false;
			}
		}
		System.out.println("All data are valid? " + valid);
	}

	private static void insertData() throws Exception {
		String path = "C://Workspaces//eclipse-workspace//MedSessionClient//data//mitdb//";
		UUID uuid = new UUID(0, 201);
		String userId = uuid.toString();

		try {
			SecretKey Sdata = CryptoUtil.getUserSecret("key" + 201);
			String hashId = CryptoUtil.hashUserID(uuid.toString(), Hex.encodeHexString(Sdata.getEncoded()));
			String PKsp = CryptoUtil.getPublicKeyHex("key" + 1);
			String PKpat = CryptoUtil.getPublicKeyHex("key" + 201);
			String tempfile = "C://Workspaces//eclipse-workspace//MedSessionClient//data//records.txt";
			FileOutputStream fos = new FileOutputStream(tempfile, false);
			for (int i = 100; i <= 129; i++) {

				String file = path + i + ".dat";
				// create file digest
				String digestHex = CryptoUtil.generateFileDigestHex(file);
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
				dataObj.put("Name", name);
				dataObj.put("Summary", summary);
				dataObj.put("DataType", dataType);
				dataObj.put("RecordType", recordType);
				dataObj.put("Date", date);
				dataObj.put("Location", location);
				dataObj.put("PKpat", PKpat);
				String encrypted = CryptoUtil.encryptWithSecret(dataObj.toString(), Sdata);

				// key1 for healthcare provider
				PrivateKey SKsp = CryptoUtil.getPrivateKey("key" + 1);

				JSONObject result = BlockchainServiceManager.addEventDG(PKsp, SKsp, digestHex, encrypted, i);

				String line = result.getString("BlockHash") + "," + result.getString("EventHash")
						+ System.lineSeparator();
				fos.write(line.getBytes());
			}
			fos.flush();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
