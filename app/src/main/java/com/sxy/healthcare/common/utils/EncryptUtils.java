package com.sxy.healthcare.common.utils;


import org.apache.commons.codec.binary.Base64;

public class EncryptUtils {

    /**
     * BASE64Encoder 加密
     *
     * @param data
     *            要加密的数据
     * @return 加密后的字符串
     */
    public static String encryptBASE64(byte[] data) {
        // BASE64Encoder encoder = new BASE64Encoder();
        // String encode = encoder.encode(data);
        // 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Encoder
        //Base64.Encoder encoder = Base64.getEncoder();
        //String encode = encoder.encodeToString(data);

        byte[] bytes = Base64.encodeBase64(data);
        String encode = new String(bytes);

        return encode;
    }
    /**
     * BASE64Decoder 解密
     *
     * @param data
     *            要解密的字符串
     * @return 解密后的byte[]
     * @throws Exception
     */
    public static byte[] decryptBASE64(String data) throws Exception {
        // BASE64Decoder decoder = new BASE64Decoder();
        // byte[] buffer = decoder.decodeBuffer(data);
        // 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Decoder
       // Base64.Decoder decoder = Base64.getDecoder();
       // byte[] buffer = decoder.decode(data);
        try {
           // byte[] buffer = Base64.decodeBase64(data);
            byte[] encodeBase64 = Base64.decodeBase64(new String(data).getBytes());
            return encodeBase64;
        }catch (Exception e){
            e.printStackTrace();
        }
      return null;
    }

}
