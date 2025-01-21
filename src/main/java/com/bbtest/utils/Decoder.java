package com.bbtest.utils;

import com.bbtest.records.Task;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.UnaryOperator;

public class Decoder {

    public static final Byte BASE64 = 1;
    public static final Byte ROT13 = 2;

    private static final Base64.Decoder DECODER = Base64.getDecoder();

    public static Task decryptIfPossible(Task task) {
        if (BASE64.equals(task.encrypted())) {
            return decrypt(task, Decoder::decodeBase64);
        } else if (ROT13.equals(task.encrypted())) {
            return decrypt(task, Decoder::decryptROT13);
        } else {
            return task;
        }
    }

    private static Task decrypt(Task encoded, UnaryOperator<String> decodingFunction) {
        return new Task(
                decodingFunction.apply(encoded.adId()),
                decodingFunction.apply(encoded.probability()),
                encoded.expiresIn(),
                encoded.reward(),
                decodingFunction.apply(encoded.message()),
                null
        );
    }

    private static String decryptROT13(String encrypted) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < encrypted.length(); i++) {
            char c = encrypted.charAt(i);
            if       (c >= 'a' && c <= 'm') c += 13;
            else if  (c >= 'A' && c <= 'M') c += 13;
            else if  (c >= 'n' && c <= 'z') c -= 13;
            else if  (c >= 'N' && c <= 'Z') c -= 13;
            sb.append(c);
        }
        return sb.toString();
    }

    private static String decodeBase64(String encoded) {
        return toString(DECODER.decode(getBytes(encoded)));
    }

    private static byte[] getBytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    private static String toString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
