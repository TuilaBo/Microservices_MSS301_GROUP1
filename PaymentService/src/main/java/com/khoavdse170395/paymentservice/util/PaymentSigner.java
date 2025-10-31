package com.khoavdse170395.paymentservice.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PaymentSigner {

    public static String buildSignedQuery(Map<String,String> params, String secret){
        Map<String,String> sorted = new TreeMap<>(params);
        String data = sorted.entrySet().stream()
                .map(e -> enc(e.getKey()) + "=" + enc(e.getValue()))
                .collect(Collectors.joining("&"));
        return data + "&vnp_SecureHash=" + hmac(secret, data);
    }

    public static boolean verify(Map<String,String> params, String secret){
        String received = params.remove("vnp_SecureHash");
        String data = params.entrySet().stream()
                .filter(e -> e.getKey().startsWith("vnp_"))
                .sorted(Map.Entry.comparingByKey())
                .map(e -> enc(e.getKey()) + "=" + enc(e.getValue()))
                .collect(Collectors.joining("&"));
        return hmac(secret, data).equalsIgnoreCase(received);
    }

    private static String enc(String s){
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String hmac(String key, String data){
        try{
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length*2);
            for (byte b: bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
