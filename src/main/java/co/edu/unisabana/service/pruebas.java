package co.edu.unisabana.service;

import java.util.Base64;

import javax.crypto.BadPaddingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unisabana.config.EncryptionRSA;

@RestController
public class pruebas {
	
	private EncryptionRSA rsa;
	static String plainText = "La vida es un ciclo...";
	
	@RequestMapping("/pruebas")
	public String cifrado() throws Exception {
		rsa = new EncryptionRSA(plainText);
		// Encryption
		byte[] cipherTextArray = rsa.encrypt(plainText, "C:\\Users\\dani2\\eclipse-workspace\\springLog\\src\\main\\resources\\public.keystore");
		String encryptedText = Base64.getEncoder().encodeToString(cipherTextArray);
		System.out.println("Encrypted Text : " + encryptedText);

		// Decryption
		String decryptedText = rsa.decrypt(cipherTextArray, "C:\\Users\\dani2\\eclipse-workspace\\springLog\\src\\main\\resources\\private.keystore");
		System.out.println("DeCrypted Text : " + decryptedText);
		
		/*rsa = new RsaUtil(plainText);
		byte[] byteArray = rsa.encrypt(plainText);
		String res = Base64.getEncoder().encodeToString(byteArray);
		String resA = rsa.decrypt(byteArray);*/
		
		String a = encryptedText + " // " + decryptedText;
		
		return a;
	}
}
