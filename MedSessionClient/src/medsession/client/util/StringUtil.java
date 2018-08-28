package medsession.client.util;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class StringUtil {

	public static String getStringFromKey(Key key) {
		// return Base64.getEncoder().encodeToString(key.getEncoded());
		return Hex.encodeHexString(key.getEncoded());
	}

	// Tacks in array of transactions and returns a merkle root.
	public static String getMerkleRoot(List<String> hashes) {
		int count = hashes.size();
		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		for (String hash : hashes) {
			previousTreeLayer.add(hash);
		}
		ArrayList<String> treeLayer = previousTreeLayer;
		while (count > 1) {
			treeLayer = new ArrayList<String>();
			for (int i = 1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(DigestUtils.sha256Hex(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}
}
