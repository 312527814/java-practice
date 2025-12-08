package com.my;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.ECKeyUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.PlainDSAEncoding;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SM2CryptUtils {


    //生成秘钥对
    public static Map<String, String> createSM2Key(){
        SM2 sm2= SmUtil.sm2();
        sm2.setMode(SM2Engine.Mode.C1C3C2);
        String privateKey= HexUtil.encodeHexStr(BCUtil.encodeECPrivateKey(sm2.getPrivateKey()));
        String publicKey = HexUtil.encodeHexStr(((BCECPublicKey) sm2.getPublicKey()).getQ().getEncoded(false));
        Map<String, String> keys=new HashMap<>();
        keys.put(privateKey,publicKey);
        return keys;
    }

    //加密
    public static String encrypt(String data, String publicKey){
        String publicKeyTmp = publicKey;
        if (publicKey.length() == 130) {
            //这里需要去掉开始第一个字节 第一个字节表示标记
            publicKeyTmp = publicKey.substring(2);
        }
        String xhex = publicKeyTmp.substring(0, 64);
        String yhex = publicKeyTmp.substring(64, 128);
        ECPublicKeyParameters ecPublicKeyParameters = BCUtil.toSm2Params(xhex, yhex);
        //创建sm2 对象
        SM2 sm2 = new SM2(null, ecPublicKeyParameters);
        sm2.usePlainEncoding();
        sm2.setMode(SM2Engine.Mode.C1C3C2);
        String hex = sm2.encryptHex(data, KeyType.PublicKey);
        return hex;

    }

    //解密
    public static String decrypt(String data, String privateKey){
        SM2 sm2 = new SM2(ECKeyUtil.toSm2PrivateParams(privateKey), null);
        sm2.setMode(SM2Engine.Mode.C1C3C2);
        sm2.setEncoding(new PlainDSAEncoding());
        String encryptStr = sm2.decryptStr(data, KeyType.PrivateKey);
        return encryptStr;
    }



    public static void test2() {


    }

    public static void main(String[] args) {
        String publickey = "0491cdf3146f8bf09669d4c7bf6dd7409d8d0b24ec1677dbbfcce6e998b9c1cdde9ebf4dd997e591c8705b6363ab87b163f5f6fa4cc7b99f082fcda3e23b29f754";
        String privatekey = "5ab4bb0cfbb0baf6c0dd0415ed20bfadff922f107eb80be046755816fac4660a";

        System.out.println("SM2国密算法公钥:{}" + publickey);
        System.out.println("SM2国密算法私钥:{}" + privatekey);
        String data = "13305892042";
         data = SM2CryptUtils.encrypt(data,publickey);


        System.out.println("加密后数据：" + data);
        String data2 = "Aa666888";
        data2 = SM2CryptUtils.encrypt(data2,publickey);

        System.out.println("加密后数据：" + data2);

    }
}