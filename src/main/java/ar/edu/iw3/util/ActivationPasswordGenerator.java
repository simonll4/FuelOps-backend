package ar.edu.iw3.util;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

public class ActivationPasswordGenerator {

    public static String generateActivationPassword() {
        PasswordGenerator generator = new PasswordGenerator();
        CharacterRule digits = new CharacterRule(EnglishCharacterData.Digit);
        digits.setNumberOfCharacters(5);  // 5 dígitos
        String password = generator.generatePassword(5, digits);
        return String.format("%05d", Integer.parseInt(password));
    }

}