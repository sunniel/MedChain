/**
 * 
 */
package blockchain.data;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author user
 *
 */
public abstract class Event {
	public static int sequence = 0; // a rough count of how many events have been generated.

	public String id;
	public String digest;
	public String PK;
	// hex string
	public String content;
	public int type;
	public String signature;

	// Returns true if new transaction could be created.
	public abstract boolean processTransaction();

	protected abstract boolean verifySignature();

	protected boolean verifySignature(String data) {
		boolean flag = false;
		try {
			KeyFactory kf = KeyFactory.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
			PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(Hex.decodeHex(PK.toCharArray())));
			Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			flag = ecdsaVerify.verify(Hex.decodeHex(signature.toCharArray()));
			System.out.println("Valide signature? " + flag);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	// This Calculates the transaction hash (which will be used as its Id)
	public String calulateHash() {
		sequence++;
		return DigestUtils.sha256Hex(content + PK + digest + type + signature + sequence);
	}
}
