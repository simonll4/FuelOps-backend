package ar.edu.iw3.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class FieldValidator {
    public static boolean isValidField(Class<?> entityClass, String fieldName) {
        Set<String> fieldNames = Arrays.stream(entityClass.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
        return !fieldNames.contains(fieldName);
    }
}
