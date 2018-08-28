package medsession.client.thread;

import java.security.PrivateKey;

import javax.crypto.SecretKey;

import org.json.JSONArray;
import org.json.JSONObject;

import medsession.client.manager.BlockchainServiceManager;
import medsession.client.manager.DirectoryServiceManager;
import medsession.client.util.crypto.CryptoUtil;

public class DataQueryThread implements Runnable {

	public String sid;
	public int clientId;

	public DataQueryThread() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		try {
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
