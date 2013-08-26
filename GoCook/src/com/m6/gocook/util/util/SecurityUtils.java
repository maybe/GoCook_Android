package com.m6.gocook.util.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.text.TextUtils;

public class SecurityUtils {

	// 定义加密算法，有DES、DESede(即3DES)、Blowfish
	private static final String Algorithm = "DESede/CBC/PKCS5Padding";
	public static final String PASSWORD_CRYPT_KEY = "DAB578EC-6C01-4180-939A-37E6BE8A81AF";
	public static final String PASSWORD_CRYPT_IV = "117A5C0F";

	
	/**
	 * 3DES加密。Key是24位，IV是8位。
	 * 
	 * @param src 源数据的字符串
	 * @return
	 */
	public static String encryptMode(String src) {
		try {
			DESedeKeySpec dks = new DESedeKeySpec(getEncryKey(PASSWORD_CRYPT_KEY));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			SecretKey securekey = keyFactory.generateSecret(dks);
			IvParameterSpec iv = new IvParameterSpec(getEncryIV(PASSWORD_CRYPT_IV));
			Cipher c1 = Cipher.getInstance(Algorithm); // 实例化负责加密/解密的Cipher工具类
			c1.init(Cipher.ENCRYPT_MODE, securekey, iv); // 初始化为加密模式
			return Base64.encodeToString(c1.doFinal(src.getBytes()), Base64.DEFAULT);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 计算24位key
	 * 
	 * @param key
	 * @return
	 */
	private static byte[] getEncryKey(String key) {
		byte[] bkey = getMD5EncryBytes(key);
		byte[] ebkey = new byte[24];
		for(int i = 0; i < 24; i++) {
			ebkey[i] = 0;
		}
		System.arraycopy(bkey, 0, ebkey, 0, bkey.length);
		return ebkey;
	}
	
	/**
	 * 计算8位IV
	 * 
	 * @param iv
	 * @return
	 */
	private static byte[] getEncryIV(String iv) {
		byte[] bIV = getMD5EncryBytes(iv);
		byte[] ebIV = new byte[8];
		for (int i = 0; i < 8; i++) {
			ebIV[i] = (byte) Math.abs((byteToInt(bIV[i]) - byteToInt(bIV[i + 1])));
		}
		return ebIV;
	}
	
	/**
	 * byte转换为int。（由于C#和Java byte范围不同，C#:0~255, Java:-128~127
	 * 在经过byte的加减运算时造成加密编码后无法正确还原，因此先把byte转换为int，计算后再转回byte）
	 * @param src
	 * @return
	 */
	private static int byteToInt(byte src) {
		return src & 0XFF;
	}
	
	/**
	 * 3DES解密
	 * 
	 * @param src 密文的Base64字符串
	 * @return
	 */
	public static String decryptMode(String src) {
		try {
			DESedeKeySpec dks = new DESedeKeySpec(getEncryKey(PASSWORD_CRYPT_KEY));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			SecretKey securekey = keyFactory.generateSecret(dks);
			IvParameterSpec iv = new IvParameterSpec(getEncryIV(PASSWORD_CRYPT_IV));
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, securekey, iv); // 初始化为解密模式
			return new String(c1.doFinal(Base64.decode(src, Base64.DEFAULT)));
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取MD5计算后的byte数组
	 * 
	 * @param text
	 * @return
	 */
	public static byte[] getMD5EncryBytes(String text) {
		if (TextUtils.isEmpty(text)) {
			return null;
		}
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(text.getBytes());
			return md5.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * MD5加密
	 * 
	 * @param text
	 * @return
	 */
	public static String MD5Encry(String text) {
		byte[] md = getMD5EncryBytes(text);
		return trimLineFeed(Base64.encodeToString(md, Base64.DEFAULT));
	}
	
	/**
	 * 去除换行符
	 * 
	 * @param src
	 * @return
	 */
	public static String trimLineFeed(String src) {
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(src);
        return m.replaceAll("");
	}

}
