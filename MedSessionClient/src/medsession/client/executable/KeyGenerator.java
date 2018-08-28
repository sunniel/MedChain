/**
 * 
 */
package medsession.client.executable;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V1CertificateGenerator;

import medsession.client.util.AppContext;
import medsession.client.util.PropertiesLoader;

/**
 * @author user <br/>
 * 
 *         Generate 1000 keys: <br/>
 *         1) 1 - 100 for healthcare providers, <br/>
 *         2) 101 - 600 for patients, <br/>
 *         3) 601 - 1000 for requsters. <br/>
 * 
 */
public class KeyGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PropertiesLoader.loadPropertyFile();

		System.out.println("==================== Start =========================");

		try {
			String location = AppContext.getValue("medsession.crypto.keystore.location");
			String fileNameJKS = AppContext.getValue("medsession.crypto.keystore.jks.name");
			String fileNameJCEKS = AppContext.getValue("medsession.crypto.keystore.jceks.name");

			char[] pass = AppContext.getValue("medsession.crypto.keystore.pass").toCharArray();
			char[] keypass = AppContext.getValue("medsession.crypto.key.pass").toCharArray();

			KeyStore jksKS = KeyStore.getInstance("JKS");
			jksKS.load(null, pass);

			KeyStore jceksKS = KeyStore.getInstance("JCEKS");
			jceksKS.load(null, pass);

			for (int i = 1; i <= 500; i++) {
				String aliasA = "key" + i;
				String dnNameA = "cn = CA Certificate " + i;
				KeyPair keyPairA = generateKeyPair();
				addKeyPairToKeyStore(keyPairA, jksKS, keypass, dnNameA, aliasA);

				// pairing
				String aliasB = "key" + (500 + i);
				String dnNameB = "cn = CA Certificate " + (500 + i);
				KeyPair keyPairB = generateKeyPair();
				addKeyPairToKeyStore(keyPairB, jksKS, keypass, dnNameB, aliasB);

				// create secret keys
				SecretKey secret = generateSecretKey(keyPairA, keyPairB);
				addSecretToKeyStore(secret, jceksKS, keypass, aliasA);
			}

			// persist theJKS keystore
			File fileJKS = new File(location + fileNameJKS);
			if (fileJKS.exists()) {
				fileJKS.delete();
			}
			FileOutputStream fosJKS = new FileOutputStream(fileJKS);
			jksKS.store(fosJKS, pass);
			fosJKS.close();

			// persist the JCEKS keystore
			File fileJCEKS = new File(location + fileNameJCEKS);
			if (fileJCEKS.exists()) {
				fileJCEKS.delete();
			}
			FileOutputStream fosJCEKS = new FileOutputStream(fileJCEKS);
			jceksKS.store(fosJCEKS, pass);
			fosJCEKS.close();

			System.out.println("==================== Done =========================");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void addSecretToKeyStore(SecretKey secret, KeyStore ks, char[] keypass, String alias)
			throws KeyStoreException {
		KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(secret);
		ks.setEntry(alias, entry, new KeyStore.PasswordProtection(keypass));
	}

	private static void addKeyPairToKeyStore(KeyPair kp, KeyStore ks, char[] keypass, String certName, String alias)
			throws CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchProviderException,
			NoSuchAlgorithmException, SignatureException, KeyStoreException {
		Calendar cal = Calendar.getInstance();
		cal.set(2018, 8, 18);
		// time from which certificate is valid
		Date startDate = cal.getTime();
		cal.set(2019, 8, 18);
		// time after which certificate is not valid
		Date expiryDate = cal.getTime();
		X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
		certGen = new X509V1CertificateGenerator();
		X500Principal dnName = new X500Principal("CN=Test CA Certificate 4");
		BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
		certGen.setSerialNumber(serialNumber);
		certGen.setIssuerDN(dnName);
		certGen.setNotBefore(startDate);
		certGen.setNotAfter(expiryDate);
		certGen.setSubjectDN(dnName); // note: same as issuer
		certGen.setPublicKey(kp.getPublic());
		certGen.setSignatureAlgorithm("SHA256withECDSA");
		X509Certificate cert = certGen.generate(kp.getPrivate(), BouncyCastleProvider.PROVIDER_NAME);
		ks.setKeyEntry(alias, (Key) kp.getPrivate(), keypass, new X509Certificate[] { cert });
	}

	private static SecretKey generateSecretKey(KeyPair pairA, KeyPair pairB) {
		SecretKey secret = null;
		try {
			KeyAgreement ka = KeyAgreement.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME);
			ka.init(pairA.getPrivate());
			ka.doPhase(pairB.getPublic(), true);
			secret = ka.generateSecret("AES");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return secret;
	}

	private static KeyPair generateKeyPair() {
		KeyPair pair = null;
		try {
			Security.addProvider(new BouncyCastleProvider());
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME);
			keyGen.initialize(ECNamedCurveTable.getParameterSpec("prime256v1"), SecureRandom.getInstance("SHA1PRNG"));
			pair = keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pair;
	}

}
