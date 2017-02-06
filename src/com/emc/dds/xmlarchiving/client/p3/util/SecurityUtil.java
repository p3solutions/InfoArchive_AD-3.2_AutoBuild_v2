package com.emc.dds.xmlarchiving.client.p3.util;

import com.googlecode.gwt.crypto.bouncycastle.DataLengthException;
import com.googlecode.gwt.crypto.bouncycastle.InvalidCipherTextException;
import com.googlecode.gwt.crypto.client.AESCipher;
import com.googlecode.gwt.crypto.client.TripleDesCipher;

public class SecurityUtil {
	private byte[] GWT_DES_KEY;
	private String ENC_TYPE;
	
	public SecurityUtil(byte[] key, String type) {
		this.GWT_DES_KEY = key;
		this.ENC_TYPE = type;
	}
	
	public String encryption(String originalString) throws Exception {
		if(ENC_TYPE.equalsIgnoreCase("TDES"))
			return encryptTDES(originalString);
		else
			return encryptAES(originalString);	
	}

	public String decryption(String encryptedString) throws Exception {
		if(ENC_TYPE.equalsIgnoreCase("TDES"))
			return decryptTDES(encryptedString);
		else
			return decryptAES(encryptedString);	
	}

	public String encryptAES(String value) {
		AESCipher cipher = new AESCipher();
		cipher.setKey(this.GWT_DES_KEY);
		String enc = value;
		try {
			enc = cipher.encrypt(String.valueOf(value));
		} catch (DataLengthException e1) {
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (InvalidCipherTextException e1) {
			e1.printStackTrace();
		}
		return enc;
	}

	public String decryptAES(String value) {
		AESCipher cipher = new AESCipher();
		cipher.setKey(this.GWT_DES_KEY);
		String dec = value;
		try {
			dec = cipher.decrypt(value);
		} catch (DataLengthException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (InvalidCipherTextException e) {
			e.printStackTrace();
		}
		return dec;
	}
	
	public String encryptTDES(String value) {
		TripleDesCipher cipher = new TripleDesCipher();
		cipher.setKey(this.GWT_DES_KEY);
		String enc = value;
		try {
			enc = cipher.encrypt(String.valueOf(value));
		} catch (DataLengthException e1) {
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (InvalidCipherTextException e1) {
			e1.printStackTrace();
		}
		return enc;
	}

	public String decryptTDES(String value) {
		TripleDesCipher cipher = new TripleDesCipher();
		cipher.setKey(this.GWT_DES_KEY);
		String dec = value;
		try {
			dec = cipher.decrypt(value);
		} catch (DataLengthException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (InvalidCipherTextException e) {
			e.printStackTrace();
		}
		return dec;
	}

	public boolean isValid() {
		if(this.GWT_DES_KEY != null && this.ENC_TYPE != null)
			return true;
		return false;
	}
}