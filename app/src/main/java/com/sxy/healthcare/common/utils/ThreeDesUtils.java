package com.sxy.healthcare.common.utils;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;


/**
 * 3DES 加解密
 * Created by pcg on 2017/10/24.
 */
public class ThreeDesUtils {

	/**
	 * 默认iv
	 */
	private static final String IV = "sxy$#api";

	/**
	 * 秘钥工厂
	 */
	static SecretKeyFactory keyFactory = null;
	/**
	 * Cipher
	 */
	static Cipher cipher = null;/*
	final static BASE64Decoder decoder = new BASE64Decoder();
	final static BASE64Encoder encoder = new BASE64Encoder();*/
	/**
	 * vi向量
	 */
	final static IvParameterSpec ips = new IvParameterSpec(IV.getBytes());
	static {
		try {
			keyFactory = SecretKeyFactory.getInstance("desede");
			cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 3DESECB加密,key必须是长度大于等于 3*8 = 24 位哈
	 * 
	 * @param src
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptThreeDESECB(final String src, final String key) throws Exception {
		final DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
		final SecretKey securekey = keyFactory.generateSecret(dks);
		cipher.init(Cipher.ENCRYPT_MODE, securekey, ips);
		final byte[] b = cipher.doFinal(src.getBytes());
		return EncryptUtils.encryptBASE64(b).replaceAll("\r", "").replaceAll("\n", "");//encoder.encode(b).replaceAll("\r", "").replaceAll("\n", "");

	}

	/**
	 * 3DESECB解密,key必须是长度大于等于 3*8 = 24 位哈
	 * 
	 * @param src
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decryptThreeDESECB(final String src, final String key) throws Exception {
		// --通过base64,将字符串转成byte数组
		//final byte[] bytesrc = decoder.decodeBuffer(src);
		final byte[] bytesrc = EncryptUtils.decryptBASE64(src);
		// --解密的key
		final DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
		final SecretKey securekey = keyFactory.generateSecret(dks);
		cipher.init(Cipher.DECRYPT_MODE, securekey, ips);
		final byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte);
	}

	public static void main(String[] args) throws Exception {

		// {"data":{"secretKey":"wXYF0SldUitkcGQTOhdEhEyvSzwUKs75","token":"C0DB81EBFCFD44532DEC01CEE0BB071D"},"success":true}
		String key1 = "Gb1F90kzDo1MaOvxgQ2JQ3eKJBxbC0fX";
		String text = "{'type' : '2' ,'fbAccount':'peichengguo88@163.com','fbNickName':'裴承国','headImgUrl':'http://rzk-in.wo946.com/rzkCrm/img/20171018/899917156744919721784.jpg','sex':'1','fbFriendNum':'5'}";
		// text = "{'type' : '2'
		// ,'fbAccount':'470393147@qq.com','fbNickName':'杨继磊','headImgUrl':'http://rzk-in.wo946.com/rzkCrm/img/20171018/899917156744919721784.jpg','sex':'1','fbFriendNum':'5'}";//
		// String text =
		// "{\"fbAccount\":\"test1@163.com\",\"fbFriendNum\":\"15\",\"fbNickName\":\"张文文文\",\"headImgUrl\":\"http://www.baidu.com/a.jpg\",\"sex\":\"1\",\"type\":\"2\"}";
		// String text = "{'payType':'0','buyNum':'5'}";//OGqY5oBPoOGSQtizXxjJ5pGumvuaqrficHBp7PptHfU=
		// String text = "{'payType':'1','payMoney':'100'}";//OGqY5oBPoOFX9QEokUpbffUcRqn4aosbgUp2cFEcRaWm+C9oPUKAEw==
		// String text = "{'payType':'1','orderId':'723ae40fce444f82816d595e0a8c4fec','nonce':'123456'}";//
		// String text = "{'pushToken':'123456789','osType':'0'}";//
		// String text = "{'osType':'0'}";//
		// text = "{'jsonPurchaseInfo':'1111'}";
		text = "{'type':'1','pageNo':'1'}";
		// text = "{'type':'1','pageNo':'1','tYear':'2017','tMonth':'12'}";

		String sc = encryptThreeDESECB(text, key1);
		System.out.println(sc);

		// {"data":{"secretKey":"Gb1F90kzDo1MaOvxgQ2JQ3eKJBxbC0fX","token":"C0DB81EBFCFD44532DEC01CEE0BB071D"},"success":true}

		// String key = "CA7BF4E1AEEE6E114B5D1CB043F219D9";
		String key = "Gb1F90kzDo1MaOvxgQ2JQ3eKJBxbC0fX";
		String src = "wHES22f+J+CRE8OlRR3Yx552sR2EEBxQV5ypFUuXxdo=";
		System.out.println(decryptThreeDESECB(src, key));

		String text1 = "{'type':'1','pageNo':'1','tYear':'2017','tMonth':'12'}";

		System.out.println(encryptThreeDESECB(text1, key));

		String src1 = "wHES22f+J+CRE8OlRR3Yx552sR2EEBxQfjGU2xx6edviUZGvdLM3eEIHyGjRX+O3p46crS0I05A=";
		System.out.println(decryptThreeDESECB(src1, key));

	}

}
