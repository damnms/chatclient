package chatclient.services;

import java.util.Base64;

public class StringXORer {

    public String encode(String s, String key) {
        return new String(Base64.getEncoder().encode(xorWithKey(s.getBytes(), key.getBytes())));
    }

    public String decode(String s, String key) {
        return new String(xorWithKey(Base64.getDecoder().decode(s), key.getBytes()));
    }

    private byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i % key.length]);
        }
        return out;
    }

}
