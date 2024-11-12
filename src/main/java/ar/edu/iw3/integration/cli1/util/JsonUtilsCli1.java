package ar.edu.iw3.integration.cli1.util;

import ar.edu.iw3.integration.cli1.model.CustomerCli1;
import ar.edu.iw3.integration.cli1.model.DriverCli1;
import ar.edu.iw3.integration.cli1.model.business.interfaces.ICustomerCli1Business;
import ar.edu.iw3.integration.cli1.model.business.interfaces.IDriverCli1Business;
import ar.edu.iw3.integration.cli1.model.business.interfaces.IProductCli1Business;
import ar.edu.iw3.integration.cli1.model.business.interfaces.ITruckCli1Business;
import ar.edu.iw3.model.Customer;
import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BadRequestException;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;

import static ar.edu.iw3.integration.cli1.util.JsonAttributeConstants.*;
import static ar.edu.iw3.util.JsonUtils.getJsonNode;
import static ar.edu.iw3.util.JsonUtils.getString;

public class JsonUtilsCli1 {

    public static Driver getDriver(JsonNode node, String[] attrs, IDriverCli1Business driverCli1Business) throws FoundException, BusinessException, NotFoundException, BadRequestException {
        JsonNode driverNode = getJsonNode(node, DRIVER_NODE_ATTRIBUTES); // Buscar el nodo padre "driver"
        if (driverNode != null) {
            String driverDocument = null;
            // Recorremos los atributos dentro del nodo "driver"
            for (String attr : attrs) {
                if (driverNode.get(attr) != null) {
                    driverDocument = driverNode.get(attr).asText();
                    break;
                }
            }
            if (driverDocument != null) {
                DriverCli1 driver = BuildEntityUtils.buildDriver(driverNode);
                return driverCli1Business.addExternal(driver);
            } else {
                throw new BadRequestException("El campo documento del conductor no se recibió correctamente");
            }
        } else {
            throw new BadRequestException("El nodo conductor no se recibió correctamente");
        }
    }

    public static Truck getTruck(JsonNode node, String[] attrs, ITruckCli1Business truckCli1Business) throws FoundException, BusinessException, NotFoundException, BadRequestException {
        JsonNode truckNode = getJsonNode(node, TRUCK_NODE_ATTRIBUTES); // Buscar el nodo padre "truck"
        if (truckNode != null) {
            String truckLicensePlate = getString(truckNode, attrs, null);  // Obtener placa del camión desde los atributos
            if (truckLicensePlate != null) {
                JsonNode tanksNode = truckNode.get("tanks");
                return truckCli1Business.addExternal(BuildEntityUtils.buildTruck(truckNode, tanksNode));
            } else {
                throw new BadRequestException("El campo placa del camión no se recibió correctamente");
            }
        } else {
            throw new BadRequestException("El nodo camión no se recibió correctamente");
        }
    }

    public static Customer getCustomer(JsonNode node, String[] attrs, ICustomerCli1Business customerCli1Business) throws FoundException, BusinessException, NotFoundException, BadRequestException {
        JsonNode customerNode = getJsonNode(node, CUSTOMER_NODE_ATTRIBUTES); // Buscar el nodo padre "customer"
        if (customerNode != null) {
            String customerName = null;
            // Recorremos los atributos dentro del nodo "customer"
            for (String attr : attrs) {
                if (customerNode.get(attr) != null) {
                    customerName = customerNode.get(attr).asText();
                    break;
                }
            }
            if (customerName != null) {
                CustomerCli1 customer = BuildEntityUtils.buildCustomer(customerNode);
                return customerCli1Business.addExternal(customer); // Cargar el customer desde el business
            } else {
                throw new BadRequestException("El campo cliente no se recibió correctamente");
            }
        } else {
            throw new BadRequestException("El nodo cliente no se recibió correctamente");
        }
        //return null;
    }

    public static Product getProduct(JsonNode node, String[] attrs, IProductCli1Business productCli1Business) throws BusinessException, NotFoundException, FoundException, BadRequestException {
        JsonNode productNode = getJsonNode(node, PRODUCT_NODE_ATTRIBUTES); // Buscar el nodo padre "product"
        if (productNode != null) {
            String productName = null;
            // Recorremos los atributos dentro del nodo "product"
            for (String attr : attrs) {
                if (productNode.get(attr) != null) {
                    productName = productNode.get(attr).asText();
                    break;
                }
            }
            if (productName != null) {
                return productCli1Business.loadExternal(BuildEntityUtils.buildProduct(productNode)); // Cargar el producto desde el business
            } else {
                throw new BadRequestException("El campo producto no se recibió correctamente");
            }
        } else {
            throw new BadRequestException("El nodo producto no se recibió correctamente");
        }
    }
}