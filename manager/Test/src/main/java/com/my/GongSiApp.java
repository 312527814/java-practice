package com.my;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;
import java.util.Base64;

public class GongSiApp {
    public static void main(String[] args) throws Exception {
        String pword= "3sWRS0uXu9WVeUVy9+tDXQ==";
        String desWord = decrypt(pword, "5qtb0+Z/H1c=");
        System.out.println(desWord);
    }

    public static void main2(String[] args) throws Exception {
        String pword= "Tanzer@2025";
        String encrypt = encrypt(pword, "5qtb0+Z/H1c=");
        System.out.println(encrypt);
    }


    public static String decrypt(String data, String key) throws Exception{
        Key k = toKey(Base64.getDecoder().decode(key));
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, k);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data)),"UTF-8");
    }

    private static Key toKey(byte[] key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        return keyFactory.generateSecret(dks);
    }

    /**
     * 加密
     * @param data 待加密数据
     * @param key 密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String encrypt(String data, String key) throws Exception{
        BASE64Encoder encoder = new BASE64Encoder();
        BASE64Decoder decoder = new BASE64Decoder();
        Key k = toKey(decoder.decodeBuffer(key));
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, k);
        return encoder.encode(cipher.doFinal(data.getBytes()));
    }

}
