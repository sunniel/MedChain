/**
 * 
 */
package ds.web;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import de.uniba.wiai.lspi.chord.service.Key;

/**
 * @author user
 *
 */
public class CryptoHashKey implements Key {

	private byte[] hashKey;

	public CryptoHashKey(byte[] key) {
		hashKey = key.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uniba.wiai.lspi.chord.service.Key#getBytes()
	 */
	@Override
	public byte[] getBytes() {
		return hashKey;
	}

	@Override
	public int hashCode() {
		// TODO correct impl?
		return hashKey.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CryptoHashKey) {
			return java.util.Arrays.equals(((CryptoHashKey) obj).hashKey, this.hashKey);
		}
		return false;
	}

	@Override
	public String toString() {
		return Hex.encodeHexString(hashKey);
	}

	public static CryptoHashKey encode(byte[] content) {
		return new CryptoHashKey(new DigestUtils(MessageDigestAlgorithms.SHA_256).digest(content));
	}
}
