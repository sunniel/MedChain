package blockchain.data;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;

import org.apache.commons.io.IOUtils;

public class EventSCDomain {

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -1665946198710500044L;

	private String blockHash;
	private String eventHash;
	private String PKpat;
	private String signature;
	private Blob session;
	private byte[] content;

	public String getBlock_Hash() {
		return blockHash;
	}

	public void setBlock_Hash(String blockHash) {
		this.blockHash = blockHash;
	}

	public String getEvent_Hash() {
		return eventHash;
	}

	public void setEvent_Hash(String eventHash) {
		this.eventHash = eventHash;
	}

	public String getPKpat() {
		return PKpat;
	}

	public void setPKpat(String pKpat) {
		PKpat = pKpat;
	}

	public Blob getSession() {
		return session;
	}

	public void setSession(Blob session) throws Exception {

		InputStream blobInputStream = session.getBinaryStream();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		IOUtils.copyLarge(blobInputStream, os);
		this.content = os.toByteArray();
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public EventSCDomain() {
		// TODO Auto-generated constructor stub
	}

}
