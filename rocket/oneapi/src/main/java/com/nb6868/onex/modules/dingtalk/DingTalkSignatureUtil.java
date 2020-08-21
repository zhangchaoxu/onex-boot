package com.nb6868.onex.modules.dingtalk;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * dingtalk util
 * <p>
 * source {taobao-sdk-java}
 */
public class DingTalkSignatureUtil {

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String ALGORITHM = "HmacSHA256";
    private static final String NEW_LINE = "\n";

    public DingTalkSignatureUtil() {
    }

    public static String getCanonicalStringForIsv(Long timestamp, String suiteTicket) {
        StringBuilder canonicalString = new StringBuilder();
        canonicalString.append(timestamp);
        if (suiteTicket != null) {
            canonicalString.append("\n").append(suiteTicket);
        }

        return canonicalString.toString();
    }

    public static String computeSignature(String canonicalString, String secret) {
        byte[] signData = sign(canonicalString.getBytes(StandardCharsets.UTF_8), secret.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.encodeBase64(signData, false));
    }

    private static byte[] sign(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException("Unsupported algorithm: HmacSHA256", var3);
        } catch (InvalidKeyException var4) {
            throw new RuntimeException("Invalid key: " + key, var4);
        }
    }

    public static String paramToQueryString(Map<String, String> params, String charset) {
        if (params != null && !params.isEmpty()) {
            StringBuilder paramString = new StringBuilder();
            boolean first = true;

            for (Iterator var4 = params.entrySet().iterator(); var4.hasNext(); first = false) {
                Entry<String, String> p = (Entry) var4.next();
                String key = (String) p.getKey();
                String value = (String) p.getValue();
                if (!first) {
                    paramString.append("&");
                }

                paramString.append(urlEncode(key, charset));
                if (value != null) {
                    paramString.append("=").append(urlEncode(value, charset));
                }
            }

            return paramString.toString();
        } else {
            return null;
        }
    }

    public static String urlEncode(String value, String encoding) {
        if (value == null) {
            return "";
        } else {
            try {
                String encoded = URLEncoder.encode(value, encoding);
                return encoded.replace("+", "%20").replace("*", "%2A").replace("~", "%7E").replace("/", "%2F");
            } catch (UnsupportedEncodingException var3) {
                throw new IllegalArgumentException("FailedToEncodeUri", var3);
            }
        }
    }

    public static String getRandomStr(int count) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < count; ++i) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }

        return sb.toString();
    }

}
