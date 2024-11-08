package ar.edu.iw3.integration.cli1.util;

import ar.edu.iw3.integration.cli1.model.*;
import ar.edu.iw3.model.Tanker;
import ar.edu.iw3.util.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashSet;
import java.util.Set;
import static ar.edu.iw3.integration.cli1.util.JsonAttributeConstants.*;

public class BuildEntityUtils {

    public static DriverCli1 buildDriver(JsonNode driverNode) {
        DriverCli1 newDriver = new DriverCli1();
        String codeCli1Temp = System.currentTimeMillis() + "";

        String idCli1 = JsonUtils.getString(driverNode, DRIVER_IDCLI1_ATTRIBUTES, codeCli1Temp);
        if (idCli1 != null && !idCli1.isEmpty()) {
            newDriver.setIdCli1(idCli1);
            newDriver.setCodCli1Temp(idCli1.equals(codeCli1Temp));
        }

        String name = JsonUtils.getString(driverNode, DRIVER_NAME_ATTRIBUTES, "");
        if (name != null && !name.isEmpty()) {
            newDriver.setName(name);
        }

        String lastName = JsonUtils.getString(driverNode, DRIVER_LASTNAME_ATTRIBUTES, "");
        if (lastName != null && !lastName.isEmpty()) {
            newDriver.setLastName(lastName);
        }

        String identityDocument = JsonUtils.getString(driverNode, DRIVER_DOCUMENT_ATTRIBUTES, "");
        if (identityDocument != null && !identityDocument.isEmpty()) {
            newDriver.setDocument(identityDocument);
        }

        return newDriver;
    }


    public static TruckCli1 buildTruck(JsonNode truckNode, JsonNode tanksNode) {
        TruckCli1 newTruck = new TruckCli1();
        String codeCli1Temp = System.currentTimeMillis() + "";

        String idCli1 = JsonUtils.getString(truckNode, TRUCK_IDCLI1_ATTRIBUTES, codeCli1Temp);
        if (idCli1 != null && !idCli1.isEmpty()) {
            newTruck.setIdCli1(idCli1);
            newTruck.setCodCli1Temp(idCli1.equals(codeCli1Temp));
        }

        String licensePlate = JsonUtils.getString(truckNode, TRUCK_LICENSE_PLATE_ATTRIBUTES, "");
        if (licensePlate != null && !licensePlate.isEmpty()) {
            newTruck.setLicensePlate(licensePlate);
        }

        String description = JsonUtils.getString(truckNode, TRUCK_DESCRIPTION_ATTRIBUTES, "");
        if (description != null && !description.isEmpty()) {
            newTruck.setDescription(description);
        }

        Set<Tanker> newTankers = new HashSet<>();
        if (tanksNode != null && tanksNode.isArray()) {
            for (JsonNode tankNode : tanksNode) {
                TankerCli1 tanker = new TankerCli1();
                String tankerIdCli1Temp = System.currentTimeMillis() + "";

                String tankerIdCli1 = JsonUtils.getString(tankNode, TANKER_IDCLI1_ATTRIBUTES, tankerIdCli1Temp);
                if (tankerIdCli1 != null && !tankerIdCli1.isEmpty()) {
                    tanker.setIdCli1(tankerIdCli1);
                    tanker.setCodCli1Temp(tankerIdCli1.equals(tankerIdCli1Temp));
                }

                long capacityLiters = (long) JsonUtils.getValue(tankNode, TANKER_CAPACITY_ATTRIBUTES, 0);
                if (capacityLiters > 0) {
                    tanker.setCapacity_liters(capacityLiters);
                }

                String license = JsonUtils.getString(tankNode, TANKER_LICENSE_ATTRIBUTES, "");
                if (license != null && !license.isEmpty()) {

                    tanker.setLicense(license);
                }
                // Agregar al set
                newTankers.add(tanker);
            }
        }
        newTruck.setTankers(newTankers);
        return newTruck;
    }


    public static CustomerCli1 buildCustomer(JsonNode customerNode) {
        CustomerCli1 newCustomer = new CustomerCli1();
        String codeCli1Temp = System.currentTimeMillis() + "";

        String idCli1 = JsonUtils.getString(customerNode, CUSTOMER_IDCLI1_ATTRIBUTES, codeCli1Temp);
        if (idCli1 != null && !idCli1.isEmpty()) {
            newCustomer.setIdCli1(idCli1);
            newCustomer.setCodCli1Temp(idCli1.equals(codeCli1Temp));
        }

        String businessName = JsonUtils.getString(customerNode, CUSTOMER_NAME_ATTRIBUTES, "");
        if (businessName != null && !businessName.isEmpty()) {
            newCustomer.setBusinessName(businessName);
        }

        String email = JsonUtils.getString(customerNode, CUSTOMER_EMAIL_ATTRIBUTES, "");
        if (email != null && !email.isEmpty()) {

            newCustomer.setEmail(email);
        }
        return newCustomer;
    }


    public static ProductCli1 buildProduct(JsonNode productNode) {
        ProductCli1 newProduct = new ProductCli1();
        String codeCli1Temp = System.currentTimeMillis() + "";

        String idCli1 = JsonUtils.getString(productNode, PRODUCT_IDCLI1_ATTRIBUTES, codeCli1Temp);
        if (idCli1 != null && !idCli1.isEmpty()) {
            newProduct.setIdCli1(idCli1);
            newProduct.setCodCli1Temp(idCli1.equals(codeCli1Temp));
        }

        String product = JsonUtils.getString(productNode, PRODUCT_NAME_ATTRIBUTES, "");
        if (product != null && !product.isEmpty()) {
            newProduct.setProduct(product);
        }
        return newProduct;
    }

}
