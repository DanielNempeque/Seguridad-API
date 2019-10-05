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

public class EncryptionRSA {	
	/*public static void main(String[] args) throws Exception {
		this.RsaUtil(plainText);
		// Encryption
		byte[] cipherTextArray = encrypt(plainText, "C:\\Users\\dani2\\eclipse-workspace\\springLog\\src\\main\\resources\\public.keystore");
		String encryptedText = Base64.getEncoder().encodeToString(cipherTextArray);
		System.out.println("Encrypted Text : " + encryptedText);

		// Decryption
		String decryptedText = decrypt(cipherTextArray, "C:\\Users\\dani2\\eclipse-workspace\\springLog\\src\\main\\resources\\private.keystore");
		System.out.println("DeCrypted Text : " + decryptedText);

	}*/
	/**
	 * 
	 * @param plainText
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public EncryptionRSA(String plainText) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
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
 * Genera las key publica y privada 
 * @param fileName nombre del archivo que donde se va a guardar la key
 * @param n modulo a el cual se le va a aplicar la funcion de RSA
 * @param potencia Potencia a la cual se elevara y posteriormente aplicar el modulo
 * @throws IOException
 */
	public static void saveKeyToFile(String fileName, BigInteger n, BigInteger potencia) throws IOException {
		ObjectOutputStream ObjOutputStream = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(fileName)));
		try {
			ObjOutputStream.writeObject(n);
			ObjOutputStream.writeObject(potencia);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ObjOutputStream.close();
		}
	}
/**
 * Lee el archivo de keystore para recuperar la key (publica o privada)
 * @param keyFileName nombre del archivo de la key
 * @return key retorna la key obtenida de el archivo keystore
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
 * Encripcion usando RSA modo cifrador de bloques ECB
 * @param txt Texto plano a cifrar
 * @param fileKey nombre del archivo de la key formato keystore
 * @return cipherText texto cifrado usando RSA
 * @throws Exception
 */
	public static byte[] encrypt(String txt, String fileKey) throws Exception {
		Key publicKey = readKeyFromFile("public.key");

		// Modo de cifrado RSA ECB
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

		// Inicializar en modo encripcion con la clave
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		// Encriptar usando RSA y la key
		byte[] cipherText = cipher.doFinal(txt.getBytes());

		return cipherText;
	}
/**
 * Descifrado usando RSA modo cifrador de bloques ECB
 * @param cipheredTxt
 * @param fileKey
 * @return
 * @throws Exception
 */
	public static String decrypt(byte[] cipheredTxt, String fileKey) throws Exception {
		Key privateKey = readKeyFromFile("private.key");

		// Modo de cifrado RSA ECB
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

		// Inicializar en modo encripcion con la clave
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		// Perform Decryption
		byte[] decryptedTextArray = cipher.doFinal(cipheredTxt);

		return new String(decryptedTextArray);
	}
}