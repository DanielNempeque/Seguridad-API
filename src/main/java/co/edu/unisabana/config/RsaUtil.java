package co.edu.unisabana.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RsaUtil {	
	
	/**
	 * Constructor that gets plain texts and ciphers it using RSA
	 * This method reads the keys stored in the private and public keystores
	 * @param plainText
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public RsaUtil(String plainText) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		// Get an instance of the RSA key generator
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(4096);

		// Generate the KeyPair
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// Get the public and private key
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		// Get the RSAPublicKeySpec and RSAPrivateKeySpec
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
		RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);

		// Saving the Key to the file
		saveKeyToFile("public.key", publicKeySpec.getModulus(), publicKeySpec.getPublicExponent());
		saveKeyToFile("private.key", privateKeySpec.getModulus(), privateKeySpec.getPrivateExponent());

		System.out.println("Original Text  : " + plainText);
	}
	
	/**
	 * Saves the key to a file
	 * @param fileName
	 * @param modulus
	 * @param exponent
	 * @throws IOException
	 */
	public static void saveKeyToFile(String fileName, BigInteger modulus, BigInteger exponent) throws IOException {
		ObjectOutputStream ObjOutputStream = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(fileName)));
		try {
			ObjOutputStream.writeObject(modulus);
			ObjOutputStream.writeObject(exponent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ObjOutputStream.close();
		}
	}
	
	/**
	 * Reads the different keys from file
	 * @param keyFileName
	 * @return Key
	 * @throws IOException
	 */
	public static Key readKeyFromFile(String keyFileName) throws IOException {
		Key key = null;
		InputStream inputStream = new FileInputStream(keyFileName);
		ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(inputStream));
		try {
			BigInteger modulus = (BigInteger) objectInputStream.readObject();
			BigInteger exponent = (BigInteger) objectInputStream.readObject();
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			if (keyFileName.startsWith("public"))
				key = keyFactory.generatePublic(new RSAPublicKeySpec(modulus, exponent));
			else
				key = keyFactory.generatePrivate(new RSAPrivateKeySpec(modulus, exponent));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			objectInputStream.close();
		}
		return key;
	}
	/**
	 * Encrypts the plain text and returns a byte array that contains the result of
	 * the RSA encryption
	 * @param plainText
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(String plainText, String fileName) throws Exception {
		Key publicKey = readKeyFromFile("public.key");

		// Get Cipher Instance
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

		// Initialize Cipher for ENCRYPT_MODE
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		// Perform Encryption
		byte[] cipherText = cipher.doFinal(plainText.getBytes());

		return cipherText;
	}
	/**
	 * Decrypts the byte array using the private key, the method returns the original plain text
	 * @param cipherTextArray
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(byte[] cipherTextArray, String fileName) throws Exception {
		Key privateKey = readKeyFromFile("private.key");

		// Get Cipher Instance
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

		// Initialize Cipher for DECRYPT_MODE
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		// Perform Decryption
		byte[] decryptedTextArray = cipher.doFinal(cipherTextArray);

		return new String(decryptedTextArray);
	}
}