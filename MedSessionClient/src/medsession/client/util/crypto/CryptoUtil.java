package medsession.client.util.crypto;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import medsession.client.util.AppContext;
import medsession.client.util.StringUtil;

public class CryptoUtil {
	private static String location;
	private static String fileNameJKS;
	private static String fileNameJCEKS;
	private static char[] pass;
	private static char[] keypass;
	static {
		location = AppContext.getValue("medsession.crypto.keystore.location");
		fileNameJKS = AppContext.getValue("medsession.crypto.keystore.jks.name");
		fileNameJCEKS = AppContext.getValue("medsession.crypto.keystore.jceks.name");
		pass = AppContext.getValue("medsession.crypto.keystore.pass").toCharArray();
		keypass = AppContext.getValue("medsession.crypto.key.pass").toCharArray();
		Security.addProvider(new BouncyCastleProvider());
	}

	public static SecretKey getUserSecret() throws Exception {
		SecretKey key = null;
		try {
			KeyStore ks = KeyStore.getInstance("JCEKS");
			ks.load(new FileInputStream(location + fileNameJCEKS), pass);
			KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) ks.getEntry("key3",
					new KeyStore.PasswordProtection(keypass));
			key = entry.getSecretKey();
		} catch (Exception e) {
			throw e;
		}
		return key;
	}

	public static SecretKey getUserSecret(String alias) throws Exception {
		SecretKey key = null;
		try {
			KeyStore ks = KeyStore.getInstance("JCEKS");
			ks.load(new FileInputStream(location + fileNameJCEKS), pass);
			KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) ks.getEntry(alias,
					new KeyStore.PasswordProtection(keypass));
			key = entry.getSecretKey();
		} catch (Exception e) {
			throw e;
		}
		return key;
	}

	public static String hashUserID(String id, String Sdata) throws Exception {
		String userId = "";
		try {
			String content = id + Sdata;
			userId = DigestUtils.sha256Hex(content);
		} catch (Exception e) {
			throw e;
		}
		return userId;
	}

	public static String encryptWithSecret(String content, SecretKey key) throws Exception {
		/*
		 * 1) AES/CBC encryption-decryption must assign IV. 2) A random IV is not a
		 * secret. It is no more sensitive than the ciphertext itself. You can transmit
		 * it along with the ciphertext without concern. 3) IV can be used as a master
		 * key. But here, iv is not used. Thus, only AES without CBC is applied.
		 */
		Cipher cipher = Cipher.getInstance("AES", BouncyCastleProvider.PROVIDER_NAME);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrypted = cipher.doFinal(content.getBytes());
		String hex = Hex.encodeHexString(encrypted);
		return hex;
	}

	public static String encryptWithPublicKey(String content, PublicKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("ECIES", BouncyCastleProvider.PROVIDER_NAME);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrypted = cipher.doFinal(content.getBytes());
		String hex = Hex.encodeHexString(encrypted);
		return hex;
	}

	public static String decryptWithSecret(String content, SecretKey key) throws Exception {
		/*
		 * 1) AES/CBC encryption-decryption must assign IV. 2) A random IV is not a
		 * secret. It is no more sensitive than the ciphertext itself. You can transmit
		 * it along with the ciphertext without concern. 3) IV can be used as a master
		 * key. But here, iv is not used. Thus, only AES without CBC is applied.
		 */
		Cipher cipher = Cipher.getInstance("AES", BouncyCastleProvider.PROVIDER_NAME);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(Hex.decodeHex(content.toCharArray()));
		String text = new String(decrypted, "UTF-8");
		return text;
	}

	public static String decryptWithPrivateKey(String content, PrivateKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("ECIES", BouncyCastleProvider.PROVIDER_NAME);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(Hex.decodeHex(content.toCharArray()));
		String text = new String(decrypted, "UTF-8");
		return text;
	}

	public static PublicKey getPublicKey(String keyName) throws Exception {
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(location + fileNameJKS), pass);
		return ks.getCertificate(keyName).getPublicKey();
	}

	public static String getPublicKeyHex(String keyName) throws Exception {
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(location + fileNameJKS), pass);
		PublicKey pk = ks.getCertificate(keyName).getPublicKey();
		return StringUtil.getStringFromKey(pk);
	}

	public static PrivateKey getPrivateKey(String keyName) throws Exception {
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(location + fileNameJKS), pass);
		PrivateKey sk = (PrivateKey) ks.getKey(keyName, keypass);
		return sk;
	}

	public static String getPrivateKeyHex(String keyName) throws Exception {
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(location + fileNameJKS), pass);
		return StringUtil.getStringFromKey((PrivateKey) ks.getKey(keyName, keypass));
	}

	public static String generateSignature(String content, PrivateKey privateKey) throws Exception {
		// sign message
		Signature dsa = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider.PROVIDER_NAME);
		dsa.initSign(privateKey);
		byte[] data = content.getBytes();
		dsa.update(data);
		byte[] realSig = dsa.sign();
		byte[] signature = realSig;
		String hex = Hex.encodeHexString(signature);
		return hex;
	}

	public static String generateFileDigestHex(String path) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA256");
		try (InputStream is = Files.newInputStream(Paths.get(path));
				DigestInputStream dis = new DigestInputStream(is, md)) {
			/* Read decorated stream (dis) to EOF as normal... */
		}
		byte[] digest = md.digest();
		// to hex string
		String digestHex = Hex.encodeHexString(digest);
		return digestHex;
	}

	public static String generateFileDigestHex(String previousHash, String path) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA256");
		try (InputStream is = Files.newInputStream(Paths.get(path));
				DigestInputStream dis = new DigestInputStream(is, md)) {
			/* Read decorated stream (dis) to EOF as normal... */
		}
		byte[] digest = md.digest();
		// to hex string
		String digestHex = Hex.encodeHexString(digest);
		String chainedHash = digestHex;
		if (previousHash != null) {
			chainedHash = hash(previousHash + digestHex);
		}
		return chainedHash;
	}

	public static SecretKey getSecretKeyFromString(String secretKey) throws Exception {
		byte[] key = Hex.decodeHex(secretKey.toCharArray());
		SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES");
		return originalKey;
	}

	public static PrivateKey getPrivateKeyFromString(String privateKey) throws Exception {
		// May use ECIES specification?
		KeyFactory kf = KeyFactory.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
		return kf.generatePrivate(new PKCS8EncodedKeySpec(Hex.decodeHex(privateKey.toCharArray())));
	}

	public static String hash(String content) {
		return DigestUtils.sha256Hex(content);
	}
}
