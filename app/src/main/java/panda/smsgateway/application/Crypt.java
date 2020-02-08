package panda.smsgateway.application;
/**
 * Created by alexey on 09.10.2017.
 */

import android.util.Base64;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Crypt {

    public static String decryptAes(String key, String iv, String encrypted) throws GeneralSecurityException {
        //Преобразование входных данных в массивы байт
        final byte[] keyBytes = key.getBytes();
        final byte[] ivBytes = iv.getBytes();
        final byte[] encryptedBytes = Base64.decode(encrypted, Base64.DEFAULT);
        //Инициализация и задание параметров расшифровки
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivBytes));
        //Расшифровка
        final byte[] resultBytes = cipher.doFinal(encryptedBytes);
        return new String(resultBytes);
    }
}
