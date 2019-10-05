package co.edu.unisabana.config;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import net.bytebuddy.asm.Advice.This;

public class RsaUtilOriginal {
	private static PublicKey publicKey;
	private static PrivateKey privateKey;
	public RsaUtilOriginal(String plainText) throws Exception {
		// Get an instance of the RSA key generator
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(512);

		// Generate the KeyPair
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// Get the public and private key
		publicKey = getPublicKey("C:\\Users\\dani2\\eclipse-workspace\\springLog\\src\\main\\resources\\private.keystore");
		privateKey = getPrivateKey("private.keystore");
		System.out.println("Original Text  : " + plainText);

		// Encryption
		byte[] cipherTextArray = encrypt(plainText);
		String encryptedText = Base64.getEncoder().encodeToString(cipherTextArray);
		System.out.println("Encrypted Text : " + encryptedText);

		// Decryption
		String decryptedText = decrypt(cipherTextArray);
		System.out.println("DeCrypted Text : " + decryptedText);
	}

	public static byte[] encrypt(String plainText) throws Exception {
		// Get Cipher Instance RSA With ECB Mode and OAEPWITHSHA-512ANDMGF1PADDING
		// Padding
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

		// Initialize Cipher for ENCRYPT_MODE
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		// Perform Encryption
		byte[] cipherText = cipher.doFinal(plainText.getBytes());

		return cipherText;
	}

	public static String decrypt(byte[] cipherTextArray) throws Exception {
		// Get Cipher Instance RSA With ECB Mode and OAEPWITHSHA-512ANDMGF1PADDING
		// Padding
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

		// Initialize Cipher for DECRYPT_MODE
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		// Perform Decryption
		byte[] decryptedTextArray = cipher.doFinal(cipherTextArray);

		return new String(decryptedTextArray);
	}

	public static PrivateKey getPrivateKey(String filename) throws Exception {

		byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	public static PublicKey getPublicKey(String filename) throws Exception {

		byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}
}
