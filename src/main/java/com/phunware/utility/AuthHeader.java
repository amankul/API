package com.phunware.utility;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * Created by knguyen on 8/2/16.
 */

/**
  For GET requests, pass queryParameters in the method parameter "body" or else generated xAuth will not work
 */
public class AuthHeader {

   public static String generateAuthHeader(String httpMethod, String accessKey, String signatureKey, String url, String body) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        Long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        String signatureString = httpMethod + "&" + accessKey + "&" + timestamp + "&" + body;
        byte[] signatureBytes = signatureKey.getBytes();
        Key sk = new SecretKeySpec(signatureBytes, "HmacSHA256");
        Mac mac = Mac.getInstance(sk.getAlgorithm());
        mac.init(sk);
        final byte[] hmac = mac.doFinal(signatureString.getBytes());
        String signatureHash = new String(Hex.encodeHex(hmac));
        String authHeader = accessKey + ":" + timestamp + ":" + signatureHash;

        return authHeader;
    }

}