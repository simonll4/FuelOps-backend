package ar.edu.iw3.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import ar.edu.iw3.model.*;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.ICustomerBusiness;
import ar.edu.iw3.model.business.interfaces.IDriverBusiness;
import ar.edu.iw3.model.business.interfaces.IProductBusiness;
import ar.edu.iw3.model.business.interfaces.ITruckBusiness;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JsonUtiles {
    @SuppressWarnings({ "unchecked", "rawtypes" })
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
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
     *    el primero que encuentra será el valor retornado.
     * 2) Si no se encuentra ninguno de los atributos del punto 1), se
     *    retorna "defaultValue".
     * Ejemplo: supongamos que "node" represente: {"code":"c1, "codigo":"c11", "stock":true}
     *   getString(node, String[]{"codigo","cod"},"-1") retorna: "cl1"
     *   getString(node, String[]{"cod_prod","c_prod"},"-1") retorna: "-1"
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
        if (parsedDate == null && defaultValue != null) {
            for (SimpleDateFormat format : formats) {
                try {
                    parsedDate = format.parse(defaultValue);
                    if (parsedDate != null) {
                        return parsedDate; // Si parsea el default, devolverlo
                    }
                } catch (ParseException e) {
                    // Si falla el default, seguir intentando con otros formatos
                }
            }
        }

        return parsedDate; // Si no se pudo parsear, devolver null
    }

    public static Driver getDriver(JsonNode node, String[] attrs, IDriverBusiness driverBusiness) throws BusinessException, NotFoundException {
        String driverDocument = getString(node, attrs, null);  // Obtener documento del driver desde los atributos
        if (driverDocument != null) {
            return driverBusiness.load(driverDocument); // Si se encuentra, cargar la entidad Driver desde el negocio
        }
        return null; // Si no se encuentra el documento, retorna null
    }

    public static Truck getTruck(JsonNode node, String[] attrs, ITruckBusiness truckBusiness) throws BusinessException, NotFoundException {
        String truckLicensePlate = getString(node, attrs, null);  // Obtener placa del camión desde los atributos
        if (truckLicensePlate != null) {
            Truck truck = truckBusiness.load(truckLicensePlate); // Si se encuentra, cargar la entidad Truck desde el negocio

            // Si existe un nodo de "tanks" en el JSON, procesar los Tanker
            JsonNode tanksNode = node.get("tanks");
            if (tanksNode != null && tanksNode.isArray()) {
                Set<Tanker> tankers = new HashSet<>();
                for (JsonNode tankNode : tanksNode) {
                    Tanker tanker = new Tanker();
                    tanker.setCapacity_liters((long) getValue(tankNode, new String[]{"capacity_liters"}, 0)); // Set capacity
                    tanker.setLicense(getString(tankNode, new String[]{"license"}, "")); // Set license
                    tankers.add(tanker);
                }
                truck.setTanks(tankers); // Asignar los tanks al truck
            }
            return truck;
        }
        return null; // Si no se encuentra la placa, retorna null
    }

    public static Customer getCustomer(JsonNode node, String[] attrs, ICustomerBusiness customerBusiness) throws BusinessException, NotFoundException {
        String customerName = getString(node, attrs, null);  // Obtener nombre del cliente desde los atributos
        if (customerName != null) {
            return customerBusiness.load(customerName);  // Si se encuentra, cargar la entidad Customer desde el negocio
        }
        return null;  // Si no se encuentra el nombre, retorna null
    }

    public static Product getProduct(JsonNode node, String[] attrs, IProductBusiness productBusiness) throws NotFoundException {
        String productName = null;

        // Recorremos los atributos buscando el nombre del producto
        for (String attr : attrs) {
            if (node.get(attr) != null) {
                productName = node.get(attr).asText(); // Extraemos el texto del atributo
                break;
            }
        }

        // Si encontramos un nombre válido, intentamos cargar el producto
        if (productName != null) {
            try {
                return productBusiness.load(productName); // Cargamos el producto desde el negocio
            } catch (NotFoundException | BusinessException e) {
                throw NotFoundException.builder().message("No se encontró el producto " + productName).build();
            }
        }

        // Si no encontramos nada o hay un error, retornamos null
        return null;
    }

}