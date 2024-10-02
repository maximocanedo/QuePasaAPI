package frgp.utn.edu.ar.quepasa.utils;

import java.util.Base64;

public class JwtTestUtils {
    private static boolean isBase64(String str) {
        try {
            Base64.getUrlDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isJwt(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) return false;
        if (!isBase64(parts[0]) || !isBase64(parts[1])) return false;
        try {
            String header = new String(Base64.getUrlDecoder().decode(parts[0]));
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
