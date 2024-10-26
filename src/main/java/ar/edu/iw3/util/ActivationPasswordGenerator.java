package ar.edu.iw3.util;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

public class ActivationPasswordGenerator {

    public static Integer generateActivationPassword() {
        PasswordGenerator generator = new PasswordGenerator();
        CharacterRule digits = new CharacterRule(EnglishCharacterData.Digit);
        digits.setNumberOfCharacters(5);  // 5 dígitos
        return Integer.parseInt(generator.generatePassword(5, digits));
    }

}