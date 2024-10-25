package frgp.utn.edu.ar.quepasa.service.validators.objects;

import frgp.utn.edu.ar.quepasa.service.validators.commons.StringValidator;
import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.StringValidatorBuilder;
import frgp.utn.edu.ar.quepasa.service.validators.commons.builders.ValidatorBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartFileValidator extends ValidatorBuilder<MultipartFileValidator, MultipartFile> {

    public static final long KB = 1024L;
    public static final long MB = 1024L * KB;
    public static final long GB = 1024L * MB;

    public MultipartFileValidator(MultipartFile file, String fieldName) {
        super(file, fieldName);
    }

    public MultipartFileValidator(MultipartFile file) {
        this(file, "file");
    }

    public MultipartFileValidator isNotEmpty() {
        if(getValue().isEmpty())
            super.invalidate("El archivo no puede estar vacío. ");
        return this;
    }

    public MultipartFileValidator isNotNull() {
        if(getValue() == null)
            super.invalidate("Se recibió un valor nulo en lugar de un archivo. ");
        return this;
    }

    public MultipartFileValidator hasContentType() {
        if(getValue().getContentType() == null)
            super.invalidate("No se puede determinar el tipo de archivo. ");
        return this;
    }

    public MultipartFileValidator isPDF() {
        this.hasContentType();
        if(!getValue().getContentType().equals("application/pdf"))
            super.invalidate("No es un archivo PDF. ");
        return this;
    }

    public MultipartFileValidator isPicture() {
        this.hasContentType();
        if(!getValue().getContentType().startsWith("image/"))
            super.invalidate("No es un archivo PDF. ");
        return this;
    }

    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unitPrefix = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), unitPrefix);
    }

    public MultipartFileValidator weighsLessThan(long maximumSize) {
        var size = getValue().getSize();
        if(size > maximumSize)
            super.invalidate("El archivo supera el peso máximo de " + formatBytes(maximumSize) + ". ");
        return this;
    }

    public MultipartFileValidator weighsMoreThan(long minimumSize) {
        var size = getValue().getSize();
        if(size < minimumSize)
            super.invalidate("El archivo supera el peso mínimo de " + formatBytes(minimumSize) + ". ");
        return this;
    }

    public MultipartFileValidator meetsMaximumSizeForPDF() {
        return this.weighsLessThan(15 * MB);
    }

    public MultipartFileValidator meetsMaximumSizeForPicture() {
        return this.weighsLessThan(10 * MB);
    }

    public StringValidator validateContentAsString() {
        byte[] b = {};
        try {
            b = getValue().getBytes();
        } catch(IOException expected) {
            super.invalidate("No se puede leer el contenido del archivo como una cadena de texto. ");
        }
        return new StringValidator(new String(b));
    }

}
