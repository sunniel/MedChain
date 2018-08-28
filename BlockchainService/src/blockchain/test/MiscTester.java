/**
 * 
 */
package blockchain.test;

import org.apache.commons.codec.binary.Hex;

import blockchain.dao.EventDAO;
import blockchain.data.EventSCDomain;
import blockchain.util.PropertiesLoader;

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
			// // TODO Auto-generated method stub
			// JSONObject jo = new JSONObject();
			// jo.put("key1", "value1");
			// jo.put("key2", "value2");
			// jo.append("key3", "value3.1");
			// jo.append("key3", "value3.2");
			// jo.append("key3", "value3.3");
			// jo.put("key4", "value4");
			// System.out.println(jo.toString(4));

			EventDAO dao = new EventDAO();
			EventSCDomain domain = dao.getEventSC(0,
					"33416377bbdb24cd1d7fe46d6e7b90f4cbe4d3e201aaa1aee18e1fe19ec72b57");
			System.out.println("block hash: " + domain.getBlock_Hash());
			System.out.println("event hash: " + domain.getEvent_Hash());
			System.out.println("PKpat hash: " + domain.getPKpat());
			System.out.println("signature hash: " + domain.getSignature());
			System.out.println("session hash: " + Hex.encodeHexString(domain.getContent()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}