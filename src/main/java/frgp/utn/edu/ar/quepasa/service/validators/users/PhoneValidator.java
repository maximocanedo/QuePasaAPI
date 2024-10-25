package frgp.utn.edu.ar.quepasa.service.validators.users;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import frgp.utn.edu.ar.quepasa.service.validators.ValidatorBuilder;

public class PhoneValidator extends ValidatorBuilder<PhoneValidator, String> {

    private final PhoneNumberUtil instance = PhoneNumberUtil.getInstance();

    public PhoneValidator(String value) {
        super(value, "phone");
    }

    public PhoneValidator isValidPhoneNumber() {
        PhoneNumber parsedPhoneNumber;
        try {
            parsedPhoneNumber = instance.parse(getValue(), "AR");
            if (!instance.isValidNumber(parsedPhoneNumber)) {
                super.invalidate("Número de teléfono inválido. ");
            }
        } catch (NumberParseException e) {
            super.invalidate("El número de teléfono no pudo ser parseado. ");
        }
        return this;
    }

    public PhoneValidator format() {
        PhoneNumber parsedPhoneNumber = new PhoneNumber();
        try {
            parsedPhoneNumber = instance.parse(getValue(), "AR");
            if (!instance.isValidNumber(parsedPhoneNumber)) {
                super.invalidate("Número de teléfono inválido. ");
            }
        } catch (NumberParseException e) {
            super.invalidate("El número de teléfono no pudo ser parseado. ");
            return this;
        } finally {
            String formattedPhoneNumber = instance.format(parsedPhoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            setValue(formattedPhoneNumber);
        }
        return this;
    }

}
