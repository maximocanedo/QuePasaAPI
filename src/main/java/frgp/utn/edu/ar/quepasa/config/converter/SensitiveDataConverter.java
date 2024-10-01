package frgp.utn.edu.ar.quepasa.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Converter
public class SensitiveDataConverter implements AttributeConverter<String, String> {

    @Value("${token.sensitive.key}")
    private String secretKey;

    @Value("${token.sensitive.algorithm}")
    private String algorithm;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKey secretKey = new SecretKeySpec(this.secretKey.getBytes(), algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(attribute.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar el dato. ", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            SecretKey secretKey = new SecretKeySpec(this.secretKey.getBytes(), algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(dbData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error al desencriptar el dato. ", e);
        }
    }

}
