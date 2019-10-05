package co.edu.unisabana.config;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public interface AesUtil {
	public byte[] encrypt(byte[] b);

	public byte[] decrypt(byte[] b) throws BadPaddingException;

	public String getPassword();

	public void changePassword(String password);
}