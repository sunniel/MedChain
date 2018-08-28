/**
 * 
 */
package blockchain.data;

/**
 * @author user
 *
 */
public class EventSC extends Event {

	public EventSC() {
		;
	}

	// Returns true if new transaction could be created.
	public boolean processTransaction() {
		if (this.verifySignature()) {
			id = calulateHash();
			return true;
		}
		return false;
	}

	@Override
	protected boolean verifySignature() {
		return this.verifySignature(content + PK + type);
	}
}
