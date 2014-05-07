package com.mendeley.mendelyapi;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

public class CredentialsManager {

	//7
	//jwcVcop9EFHGeICghPsq+g==
	private final static String ouath2ClientId = "jwcVcop9EFHGeICghPsq+g==";
	//42FJEXdZ8q4wbQJnMpw7
	//d++XETQEE1X2i7O/8sE98/zII7g4zAvs/eDmDgEvMLM=
    private final static String ouath2ClientSecret = "d++XETQEE1X2i7O/8sE98/zII7g4zAvs/eDmDgEvMLM=";
	
	private static final String CLIENT_ID = "ouath2ClientId";
	private static final String CLIENT_SECRET = "ouath2ClientSecret";
 
	private SharedPreferences preferences;
 
	protected CredentialsManager(Context context) {
		preferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
		
		if (preferences.getString(CLIENT_ID, null) == null ||
			preferences.getString(CLIENT_SECRET, null) == null) {
			try {
				
				Log.e("", "inserting keys");
				
				setClientID(decrypt(ouath2ClientId));
				setClientSecret(decrypt(ouath2ClientSecret));
			} catch (Exception e) {
				Log.e("", "", e);
			}
		} else {
			Log.e("", "keys exist");
		}
	}
 
	protected void setClientID(String ouath2ClientId) {
		Editor editor = preferences.edit();
		editor.putString(CLIENT_ID, ouath2ClientId);
		editor.commit();
	}
 
	protected void setClientSecret(String ouath2ClientSecret) {
		Editor editor = preferences.edit();
		editor.putString(CLIENT_SECRET, ouath2ClientSecret);
		editor.commit();
	}
 
	protected String getClientID() {
		return preferences.getString(CLIENT_ID, null);
	}
 
	protected String getClientSecret() {
		return preferences.getString(CLIENT_SECRET, null);
	}
	
    private static byte[] keyBytes = new byte[] { 0x09, 0x0f, 0x07, 0x15, 0x02, 0x04, 0x17, 0x10, 0x00, 0x05, 
    	0x07, 0x08, 0x09, 0x11, 0x0a, 0x01, 0x0b, 0x0c, 0x0d, 0x0e, 0x12, 0x14, 0x06, 0x11 };

    
    public static String encrypt(String clearText) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
 
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] cipherText = cipher.doFinal(clearText.getBytes());

        return new String(Base64.encode(cipherText, Base64.DEFAULT), "UTF-8");
    }

    
   public static String decrypt(String cipherText) throws Exception {
       SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

       Cipher cipher = Cipher.getInstance("AES");
       cipher.init(Cipher.DECRYPT_MODE, keySpec);
       byte[] clearText = cipher.doFinal(Base64.decode(cipherText.getBytes(), Base64.DEFAULT));

       return new String(clearText);
   }
}
