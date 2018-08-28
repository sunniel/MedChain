/**
 * 
 */
package blockchain.data;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author user
 *
 */
public class EventSRAD extends EventDG {

	// encrypted data summary and location
	public String encrypt;

	public EventSRAD() {
		super();
	}

	// Returns true if new transaction could be created.
	public boolean processTransaction() {
		if (this.verifySignature()) {
			id = calulateHash();
			return true;
		}
		return false;
	}

	protected boolean verifySignature() {
		// return this.verifySignature(content + digest + PK + type + encrypt);
		return this.verifySignature(content + PK + type);
	}

	// This Calculates the transaction hash (which will be used as its Id)
	public String calulateHash() {
		sequence++;
		// return DigestUtils.sha256Hex(content + PK + digest + type + signature +
		// encrypt + sequence);
		return DigestUtils.sha256Hex(content + PK + type + signature + sequence);
	}
}
