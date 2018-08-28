/**
 * 
 */
package medsession.client.thread;

import java.security.PrivateKey;
import java.util.Vector;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import medsession.client.load.DataGenerationClient;
import medsession.client.manager.BlockchainServiceManager;
import medsession.client.manager.DirectoryServiceManager;
import medsession.client.util.crypto.CryptoUtil;

/**
 * @author user
 *
 */
public class DataGenerationThread implements Runnable {
	public String userId;
	public String did;
	public int dataIndex;
	public String hashId;
	public String name;
	public String summary;
	public String dataType;
	public String recordType;
	public String date;
	public String location;
	public String PKsp;
	public PrivateKey SKsp;
	public String PKpat;
	public SecretKey Sdata;
	public String digestHex;
	public String content;
	public String signature;
	public String inventory;
	public int clientId;
	public Vector<JSONObject> jHashes;

	/**
	 * 
	 */
	public DataGenerationThread() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			JSONObject result1 = BlockchainServiceManager.addEventDG(PKsp, SKsp, digestHex, content, clientId);
			System.out.println("result1: " + result1.toString());
			System.out.println("Stage 1 done on " + this.clientId + "......................");

			hashId = CryptoUtil.hashUserID(userId, Hex.encodeHexString(Sdata.getEncoded()));
			name = "DataName";
			summary = "Data summary";
			dataType = "Record";
			recordType = "Type1";
			date = "2019-08-19 15:40:00";
			location = "http://192.168.56.1:8080/MedSessionPortal/data?=File1-ID";
			if (result1.getString("BlockHash") != null && result1.getString("EventHash") != null) {
				JSONObject result2 = DirectoryServiceManager.addInventory(hashId, inventory,
						result1.getString("BlockHash"), result1.getString("EventHash"));
				System.out.println("Stage 2 done on " + this.clientId + "......................");

				// write result back
				// JSONObject hashpair = new JSONObject();
				// hashpair.put("BlockHash", result1.getString("BlockHash")).put("EventHash",
				// result1.getString("EventHash"));
				// jHashes.addElement(hashpair);

				// report complete
				DataGenerationClient.report();
			} else {
				throw new Exception("Either BlockHash or EventHash is null");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
