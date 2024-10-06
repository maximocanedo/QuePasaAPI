package frgp.utn.edu.ar.quepasa.service.validators.users;

import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameValidatorBuilder extends ValidatorBuilder<String> {

    public NameValidatorBuilder(String value) {
        super(value, "name");
        this.setValue(value.trim());
    }

    public NameValidatorBuilder validateCompoundNames() {
        Pattern p = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúñÑ'’-]{2,}( [A-Za-zÁÉÍÓÚáéíóúñÑ'’-]{2,})*$");
        Matcher m = p.matcher(getValue());
        if(!m.matches())
            super.invalidate("Cada nombre debe tener al menos dos caracteres válidos. ");
        return this;
    }

}
