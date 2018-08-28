/**
 * 
 */
package blockchain.data;

/**
 * @author user
 *
 */
public class EventDG extends Event {

	public EventDG() {
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
		return this.verifySignature(content + digest + PK + type);
	}
}
