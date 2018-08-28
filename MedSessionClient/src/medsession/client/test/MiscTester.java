/**
 * 
 */
package medsession.client.test;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import medsession.client.manager.BlockchainServiceManager;
import medsession.client.manager.DirectoryServiceManager;
import medsession.client.util.PropertiesLoader;
import medsession.client.util.StringUtil;
import medsession.client.util.crypto.CryptoUtil;

/**
 * @author user
 *
 */
public class MiscTester {

	static {
		PropertiesLoader.loadPropertyFile();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			// TODO Auto-generated method stub
			// UUID id = new UUID(0, 1000L);
			// System.out.println("uuid: " + id.toString());
			//
			// String fileName =
			// "C://Workspaces//eclipse-workspace//MedSessionClient//data//breadcrumbs.csv";
			// File breadcrumbs = new File(fileName);
			// if (breadcrumbs.exists()) {
			// breadcrumbs.delete();
			// }
			// breadcrumbs.createNewFile();
			// BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName));
			// CSVPrinter csvPrinter = new CSVPrinter(writer,
			// CSVFormat.EXCEL.withHeader("BlockHash", "EventHash"));
			//
			// // Write records to a CSV file
			// csvPrinter.printRecord("ur9p2gq202jrn9q29otfm432qi",
			// "0jrunv032q8nty92w3jtrnp2q9804");
			// csvPrinter.close();

			// Security.addProvider(new BouncyCastleProvider());
			// KeyStore ks2 = KeyStore.getInstance("JCEKS");
			// ks2.load(new
			// FileInputStream("C://Workspaces//eclipse-workspace//MedSessionClient//keys//keystore.jceks"),
			// "1234".toCharArray());
			// KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry)
			// ks2.getEntry("key3",
			// new KeyStore.PasswordProtection("1234".toCharArray()));
			// SecretKey secret2 = entry.getSecretKey();
			//
			// String data =
			// "{\"MerkleRoot\":\"b5c9849d02af7c07c30cba663c5bc6f64f6a717c5d9fd6c0b03f724822e7b52e\","
			// +
			// "\"Events\":[{\"Type\":0,\"EventHash\":\"b5c9849d02af7c07c30cba663c5bc6f64f6a717c5d9fd6c0b03f724822e7b52e\","
			// +
			// "\"Digest\":\"b8928a2d72101a9778f5564ba2d7785964045684d26ab76ff37eec041ba0356a\","
			// +
			// "\"Data\":\"120-jgf3[-120r8npqwrb;pqruq;LWAfbq20-[jve[jdlewqbt8['qwcjfkinvr032rfq\","
			// +
			// "\"PKsp\":\"172787cb004fd3560fc4da2b356c4f1fdf3a1e840463f427436d89be5cdd6823\"}],"
			// +
			// "\"BlockHash\":\"47e47c59812a060c5b0b2f2da8670608461d6b38c16d8bcd94e70170edfd8322\",\"Size\":1,"
			// +
			// "\"PreviousHash\":\"63a877a69c3f3c71f8dd4041e848759094ad731e2b9a3efc5db5d0888107e8d7\","
			// + "\"Timestamp\":1534502524649}";
			//
			// String hexString = encrypt(secret2, data);
			// System.out.println("cipherText: " + hexString);
			// String text = decrypt(secret2, hexString);
			// System.out.println("text: " + text);

			// Comparator<JSONObject> comparator = new Comparator<JSONObject>() {
			// public int compare(JSONObject o1, JSONObject o2) {
			// int c = o1.getString("BlockHash").compareTo(o2.getString("BlockHash"));
			// if (c == 0) {
			// return o1.getString("EventHash").compareTo(o2.getString("EventHash"));
			// } else {
			// return c;
			// }
			// }
			// };
			//
			// TreeSet<JSONObject> hashes = new TreeSet<JSONObject>(comparator);
			//
			// JSONObject obj1 = new JSONObject();
			// obj1.put("BlockHash", "a1234556").put("EventHash", "1oi5r-32ujgewagtfcio");
			// JSONObject obj2 = new JSONObject();
			// obj2.put("BlockHash", "1234556").put("EventHash", "21tfgw3t4agtwg");
			// hashes.add(obj1);
			// hashes.add(obj2);
			//
			// for (JSONObject hash : hashes) {
			// System.out.println("hahs: " + hash.toString());
			// }

			// JSONParser parser = new JSONParser();
			// JSONObject inventory = (JSONObject) parser
			// .parse(new
			// FileReader("C://Workspaces//eclipse-workspace//MedSessionClient//data//record1.json"));
			// String EventHash = (String) inventory.get("EventHash");
			// System.out.println(EventHash);
			//
			// String RecordType = (String) inventory.get("RecordType");
			// System.out.println(RecordType);
			//
			// String BlockHash = (String) inventory.get("BlockHash");
			// System.out.println(BlockHash);
			//
			// String DataType = (String) inventory.get("DataType");
			// System.out.println(DataType);
			// System.out.println("inventory: " + inventory.toString());

			// String rPath =
			// "C://Workspaces//eclipse-workspace//MedSessionClient//data//SampleECG//"
			// + "SampleECG_Session1_Shimmer_B64E_Calibrated_SD.csv";
			// // String rPath =
			// // "C://Workspaces//eclipse-workspace//MedSessionClient//data//SampleECG//" +
			// // "test.csv";
			// RandomAccessFile file = new RandomAccessFile(rPath, "r");
			// int i = 0, j = 0;
			// String wPath =
			// "C://Workspaces//eclipse-workspace//MedSessionClient//data//SampleECG//"
			// + "SampleECG_Session1_Shimmer_B64E_Calibrated_SD";
			// PrintWriter writer = null;
			// String line = file.readLine();
			// String header = line;
			// while (line != null) {
			// if (j % 12000 == 0) {
			// if (writer != null) {
			// writer.flush();
			// writer.close();
			// }
			// writer = new PrintWriter(wPath + i + ".csv");
			// writer.println(header);
			// i++;
			// }
			// line = file.readLine();
			// writer.println(line);
			// j++;
			// }
			// writer.flush();
			// writer.close();
			// file.close();

			String path1 = "C://Workspaces//eclipse-workspace//MedSessionClient//data//mitdb//";
			String path2 = "C://Workspaces//eclipse-workspace//MedSessionClient//data//VA//";
			UUID uuid = new UUID(0, 201);

			SecretKey Sdata1 = CryptoUtil.getUserSecret("key" + 1);
			SecretKey Sdata2 = CryptoUtil.getUserSecret("key" + 2);
			String hashId1 = CryptoUtil.hashUserID(uuid.toString(), Hex.encodeHexString(Sdata1.getEncoded()));
			String hashId2 = CryptoUtil.hashUserID(uuid.toString(), Hex.encodeHexString(Sdata2.getEncoded()));
			String PKsp1 = CryptoUtil.getPublicKeyHex("key" + 1);
			String PKsp2 = CryptoUtil.getPublicKeyHex("key" + 2);
			String PKpat = CryptoUtil.getPublicKeyHex("key" + 201);

			JSONObject records = new JSONObject();
			JSONObject section = new JSONObject();
			JSONObject content = new JSONObject();
			JSONObject content2 = new JSONObject();
			String digestHex = null;
			// section 1
			for (int i = 100; i <= 109; i++) {

				String file = path1 + i + ".dat";
				// create file digest
				digestHex = CryptoUtil.generateFileDigestHex(digestHex, file);
				// UUID did = UUID.randomUUID();
				String name = "Marry";
				String summary = "This is a data summary";
				String dataType = "Health Care Record";
				String recordType = "Type1";
				String date = "2019-08-19 15:40:00";
				String location = path1 + i + ".dat";
				int index = i - 100;
				// encrypt content
				JSONObject dataObj = new JSONObject();
				dataObj.put("DID", String.valueOf(i));
				dataObj.put("Index", index);
				dataObj.put("PKpat", PKpat);
				String encrypted = CryptoUtil.encryptWithSecret(dataObj.toString(), Sdata1);

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
				record.put("BlockHash", "888888888888888888888888888");
				record.put("EventHash", "999999999999999999999999999");
				content.append("Entry", record);

				// for session creation at blockchain
				JSONObject record2 = new JSONObject();
				record2.put("DID", String.valueOf(i));
				record2.put("Index", String.valueOf(i));
				content2.append("Entry", record2);
			}
			String encrypted = CryptoUtil.encryptWithSecret(content.toString(), Sdata1);
			section.put("SectionContent", encrypted);
			section.put("SectionID", hashId1);
			String SdataHex = StringUtil.getStringFromKey(Sdata1);
			PublicKey PKeysp1 = CryptoUtil.getPublicKey("key" + 1);
			String EKsp = CryptoUtil.encryptWithPublicKey(SdataHex, PKeysp1);
			PublicKey PKeyreq = CryptoUtil.getPublicKey("key" + 700);
			String EKreq = CryptoUtil.encryptWithPublicKey(SdataHex, PKeyreq);
			PublicKey PKeypat = CryptoUtil.getPublicKey("key" + 201);
			String EKpat = CryptoUtil.encryptWithPublicKey(SdataHex, PKeypat);
			section.put("EKsp", EKsp);
			section.put("EKreq", EKreq);
			section.put("EKpat", EKpat);
			records.append("Sections", section);

			// section 2
			section = new JSONObject();
			content = new JSONObject();
			for (int i = 0; i < 10; i++) {
				String file = path2 + "VA_" + i + ".txt";
				// create file digest
				digestHex = CryptoUtil.generateFileDigestHex(file);
				// UUID did = UUID.randomUUID();
				String summary = "This is a data summary";
				String dataType = "Health Care Record";
				String recordType = "Type1";
				String date = "2019-08-19 15:40:00";
				String location = path2 + "VA_" + i + ".txt";
				int index = i - 100;
				// encrypt content
				JSONObject dataObj = new JSONObject();
				dataObj.put("DID", String.valueOf(i));
				dataObj.put("PKpat", PKpat);
				String encrypted2 = CryptoUtil.encryptWithSecret(dataObj.toString(), Sdata2);
				// key1 for healthcare provider
				PrivateKey SKsp = CryptoUtil.getPrivateKey("key" + 1);
				// JSONObject result = BlockchainServiceManager.addEventDG(PKsp2, SKsp,
				// digestHex, encrypted2, i);

				// for session creation at directory service
				JSONObject record = new JSONObject();
				record.put("DID", String.valueOf(i));
				record.put("Summary", summary);
				record.put("DataType", dataType);
				record.put("RecordType", recordType);
				record.put("Date", date);
				record.put("Location", location);
				record.put("BlockHash", "6666666666666666666666666");
				record.put("EventHash", "7777777777777777777777777");
				content.append("Entry", record);

				// for session creation at blockchain
				JSONObject record2 = new JSONObject();
				record2.put("DID", String.valueOf(i));
				record2.put("Index", String.valueOf(i));
				content2.append("Entry", record2);
			}
			encrypted = CryptoUtil.encryptWithSecret(content.toString(), Sdata2);
			section.put("SectionContent", encrypted);
			section.put("SectionID", hashId2);
			SdataHex = StringUtil.getStringFromKey(Sdata2);
			PublicKey PKeysp2 = CryptoUtil.getPublicKey("key" + 2);
			EKsp = CryptoUtil.encryptWithPublicKey(SdataHex, PKeysp2);
			PKeyreq = CryptoUtil.getPublicKey("key" + 700);
			EKreq = CryptoUtil.encryptWithPublicKey(SdataHex, PKeyreq);
			PKeypat = CryptoUtil.getPublicKey("key" + 201);
			EKpat = CryptoUtil.encryptWithPublicKey(SdataHex, PKeypat);
			section.put("EKsp", EKsp);
			section.put("EKreq", EKreq);
			section.put("EKpat", EKpat);
			records.append("Sections", section);

			// store session into Blockchain
			String PKreq = CryptoUtil.getPublicKeyHex("key" + 700);
			content2.put("PKreq", PKreq);
			String encrypted2 = CryptoUtil.encryptWithPublicKey(content2.toString(), PKeypat);
			PrivateKey SKpat = CryptoUtil.getPrivateKey("key" + 201);
			// JSONObject result = BlockchainServiceManager.addEventSC(PKpat, SKpat,
			// encrypted2, 11);

			String sid = "111111111111111111111111";
			records.put("SID", sid);
			String SHash = CryptoUtil.hash(records.toString());
			records.put("SHash", SHash);
			// DirectoryServiceManager.addInventory(sid, records.toString(),
			// result.getString("BlockHash"),
			// result.getString("EventHash"));
			// String tempfile =
			// "C://Workspaces//eclipse-workspace//MedSessionClient//data//session.txt";
			// FileOutputStream fos = new FileOutputStream(tempfile, true);
			// StringBuilder sb = new StringBuilder();
			// String SKreq = CryptoUtil.getPrivateKeyHex("key" + 700);
			// sb.append(sid).append(System.lineSeparator()).append(SKreq).append(System.lineSeparator());
			// fos.write(sb.toString().getBytes());
			// fos.flush();
			// fos.close();
			System.out.println("Session directory: " + records.toString(4));

			// decrypt and retrieve content
			JSONArray sections = records.getJSONArray("Sections");
			int size = sections.length();
			// for integrity verification
			boolean valid = true;
			// for the first section
			JSONObject section2 = sections.getJSONObject(0);
			// decrypt EKreq
			String EKreq2 = section2.getString("EKreq");
			PrivateKey SKeyreq = CryptoUtil.getPrivateKey("key" + 700);
			String EK = CryptoUtil.decryptWithPrivateKey(EKreq2, SKeyreq);
			SecretKey EKey = CryptoUtil.getSecretKeyFromString(EK);
			String encrypted3 = section2.getString("SectionContent");
			JSONObject content3 = new JSONObject(CryptoUtil.decryptWithSecret(encrypted3, EKey));
			JSONArray entries = content3.getJSONArray("Entry");
			int num = entries.length();
			String digestHex2 = null;
			String blockHash = null;
			String eventHash = null;
			for (int j = 0; j < num; j++) {
				JSONObject entry = entries.getJSONObject(j);
				System.out.println("Entry: " + entry.toString());
				// String location = entry.getString("Location");
				// digestHex = CryptoUtil.generateFileDigestHex(digestHex, location);
				// blockHash = entry.getString("BlockHash");
				// eventHash = entry.getString("EventHash");
			}
			// JSONObject bcRecord = BlockchainServiceManager.getContent(blockHash,
			// eventHash, i);
			// String digestHex2 = bcRecord.getString("Digest");
			// if (!digestHex.equals(digestHex2)) {
			// valid = false;
			// }

			// for the second section
			section2 = sections.getJSONObject(1);
			// decrypt EKreq
			EKreq = section2.getString("EKreq");
			// SKeyreq = CryptoUtil.getPrivateKey("key" + 700);
			EK = CryptoUtil.decryptWithPrivateKey(EKreq, SKeyreq);
			EKey = CryptoUtil.getSecretKeyFromString(EK);
			encrypted = section2.getString("SectionContent");
			content = new JSONObject(CryptoUtil.decryptWithSecret(encrypted, EKey));
			entries = content.getJSONArray("Entry");
			num = entries.length();
			for (int j = 0; j < num; j++) {
				JSONObject entry = entries.getJSONObject(j);
				String location = entry.getString("Location");
				digestHex = CryptoUtil.generateFileDigestHex(location);
				blockHash = entry.getString("BlockHash");
				eventHash = entry.getString("EventHash");
				System.out.println("Entry: " + entry.toString());
				// bcRecord = BlockchainServiceManager.getContent(blockHash, eventHash, j);
				// digestHex2 = bcRecord.getString("Digest");
				// if (!digestHex.equals(digestHex2)) {
				// valid = false;
				// }
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String encrypt(SecretKey key, String data) {
		String hexString = null;
		try {

			Cipher cipher = Cipher.getInstance("AES", BouncyCastleProvider.PROVIDER_NAME);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] cipherText = cipher.doFinal(data.getBytes());
			hexString = Hex.encodeHexString(cipherText);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hexString;
	}

	private static String decrypt(SecretKey key, String cipherData) {
		String text = null;
		try {
			Cipher cipher = Cipher.getInstance("AES", BouncyCastleProvider.PROVIDER_NAME);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] clearText = cipher.doFinal(Hex.decodeHex(cipherData.toCharArray()));
			text = new String(clearText, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}

}
