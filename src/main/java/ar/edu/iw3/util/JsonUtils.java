package ar.edu.iw3.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ar.edu.iw3.integration.cli1.model.business.interfaces.ICustomerCli1Business;
import ar.edu.iw3.integration.cli1.model.business.interfaces.IDriverCli1Business;
import ar.edu.iw3.integration.cli1.model.business.interfaces.IProductCli1Business;
import ar.edu.iw3.integration.cli1.model.business.interfaces.ITruckCli1Business;
import ar.edu.iw3.integration.cli1.util.BuildEntityUtils;
import ar.edu.iw3.model.*;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import static ar.edu.iw3.integration.cli1.util.JsonAttributeConstants.*;


public class JsonUtils {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ObjectMapper getObjectMapper(Class clazz, StdSerializer ser, String dateFormat) {
        ObjectMapper mapper = new ObjectMapper();
        String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
        if (dateFormat != null)
            defaultFormat = dateFormat;
        SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
        SimpleModule module = new SimpleModule();
        if (ser != null) {
            module.addSerializer(clazz, ser);
        }
        mapper.setDateFormat(df);
        mapper.registerModule(module);
        return mapper;

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ObjectMapper getObjectMapper(Class clazz, StdDeserializer deser, String dateFormat) {
        ObjectMapper mapper = new ObjectMapper();
        String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
        if (dateFormat != null)
            defaultFormat = dateFormat;

        SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
        SimpleModule module = new SimpleModule();
        if (deser != null) {
            module.addDeserializer(clazz, deser);
        }

        mapper.setDateFormat(df);
        mapper.registerModule(module);
        return mapper;
    }

    /**
     * Obtiene una cadena con la siguiente lógica:
     * 1) Busca en cada uno de los atributos definidos en el arreglo "attrs",
     * el primero que encuentra será el valor retornado.
     * 2) Si no se encuentra ninguno de los atributos del punto 1), se
     * retorna "defaultValue".
     * Ejemplo: supongamos que "node" represente: {"code":"c1, "codigo":"c11", "stock":true}
     * getString(node, String[]{"codigo","cod"},"-1") retorna: "cl1"
     * getString(node, String[]{"cod_prod","c_prod"},"-1") retorna: "-1"
     *
     * @param node
     * @param attrs
     * @param defaultValue
     * @return
     */

    public static String getString(JsonNode node, String[] attrs, String defaultValue) {
        String r = null;
        for (String attr : attrs) {
            if (node.get(attr) != null) {
                r = node.get(attr).asText();
                break;
            }
        }
        if (r == null)
            r = defaultValue;
        return r;
    }

    public static float getValue(JsonNode node, String[] attrs, float defaultValue) {
        Float r = null;
        for (String attr : attrs) {
            if (node.get(attr) != null) {
                // Intentamos manejar el valor como float sin depender de si el tipo es específicamente un float
                if (node.get(attr).isFloat() || node.get(attr).isDouble() || node.get(attr).isInt()) {
                    r = node.get(attr).floatValue(); // Convertimos cualquiera de estos tipos a float
                    break;
                }
            }
        }
        if (r == null)
            r = defaultValue;
        return r;
    }

    public static boolean getBoolean(JsonNode node, String[] attrs, boolean defaultValue) {
        Boolean r = null;
        for (String attr : attrs) {
            if (node.get(attr) != null && node.get(attr).isBoolean()) {
                r = node.get(attr).asBoolean();
                break;
            }
        }
        if (r == null)
            r = defaultValue;
        return r;
    }

    public static Date getDate(JsonNode node, String[] attrs, String defaultValue) {
        Date parsedDate = null;

        // Formatos de fecha para intentar
        SimpleDateFormat[] formats = {
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()), // Con zona horaria
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())   // Sin zona horaria
        };

        // Intentar obtener la fecha desde uno de los atributos
        for (String attr : attrs) {
            if (node.get(attr) != null) {
                String dateStr = node.get(attr).asText();
                for (SimpleDateFormat format : formats) {
                    try {
                        parsedDate = format.parse(dateStr);
                        if (parsedDate != null) {
                            return parsedDate; // Si parsea correctamente, devolvemos la fecha
                        }
                    } catch (ParseException e) {
                        // Continuar con el siguiente formato si falla el parseo
                    }
                }
            }
        }

        // Si no se encontró un valor válido, intentar con el valor por defecto
        if (defaultValue != null) {
            for (SimpleDateFormat format : formats) {
                try {
                    parsedDate = format.parse(defaultValue);
                    if (parsedDate != null) {
                        return parsedDate; // Si parsea el default, devolverlo
                    }
                } catch (ParseException e) {
                    // TODO Si falla el default, seguir intentando con otros formatos
                }
            }
        }

        return parsedDate; // Si no se pudo parsear, devolver null
    }

    public static JsonNode getJsonNode(JsonNode node, String[] attrs) {
        JsonNode r = null;
        for (String attr : attrs) {
            if (node.get(attr) != null) {
                r = node.get(attr);
                break;
            }
        }
        return r;
    }
}