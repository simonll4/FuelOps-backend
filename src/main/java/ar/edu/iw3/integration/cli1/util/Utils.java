package ar.edu.iw3.integration.cli1.util;

import ar.edu.iw3.integration.cli1.model.OrderCli1;
import ar.edu.iw3.model.Customer;
import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.Tanker;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.util.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.Set;

import static ar.edu.iw3.util.JsonAttributeConstants.*;

public class Utils {
    public static Driver buildDriver(JsonNode driverNode) {
        Driver newDriver = new Driver();

        String name = JsonUtils.getString(driverNode, DRIVER_NAME_ATTRIBUTES,"");
        if(name != null && !name.isEmpty()){
            newDriver.setName(name);
        }

        String lastName = JsonUtils.getString(driverNode,DRIVER_LASTNAME_ATTRIBUTES,"");
        if(lastName != null && !lastName.isEmpty()){
            newDriver.setLastName(lastName);
        }

        String identityDocument = JsonUtils.getString(driverNode,DRIVER_DOCUMENT_ATTRIBUTES,"");
        if(identityDocument != null && !identityDocument.isEmpty()){
            newDriver.setDocument(identityDocument);
        }

        return newDriver;
    }

    public static Truck buildTruck(JsonNode truckNode, JsonNode tanksNode) {
        Truck newTruck = new Truck();

        String licensePlate = JsonUtils.getString(truckNode,TRUCK_LICENSE_PLATE_ATTRIBUTES,"");
        if(licensePlate != null && !licensePlate.isEmpty()){
            newTruck.setLicensePlate(licensePlate);
        }

        String description = JsonUtils.getString(truckNode,TRUCK_DESCRIPTION_ATTRIBUTES,"");
        if(description != null && !description.isEmpty()){
            newTruck.setDescription(description);
        }

        Set<Tanker> newTankers = new HashSet<>();
        if (tanksNode != null && tanksNode.isArray()) {
            for (JsonNode tankNode : tanksNode) {
                Tanker tanker = new Tanker();

                long capacityLiters = (long) JsonUtils.getValue(tankNode,TANKER_CAPACITY_ATTRIBUTES,0);
                if (capacityLiters > 0) {
                    tanker.setCapacity_liters(capacityLiters);
                }

                String license = JsonUtils.getString(tankNode,TANKER_LICENSE_ATTRIBUTES,"");
                if(license != null && !license.isEmpty()){
                    tanker.setLicense(license);
                }

                // Agregar al set
                newTankers.add(tanker);
            }
        }

        newTruck.setTankers(newTankers);
        return newTruck;
    }

    public static Customer buildCustomer(JsonNode customerNode) {
        Customer newCustomer = new Customer();

        String businessName = JsonUtils.getString(customerNode,CUSTOMER_NAME_ATTRIBUTES, "");
        if(businessName != null && !businessName.isEmpty()){
            newCustomer.setBusinessName(businessName);
        }

        String email = JsonUtils.getString(customerNode,CUSTOMER_EMAIL_ATTRIBUTES,"");
        if(email != null && !email.isEmpty()){
            newCustomer.setEmail(email);
        }

        return newCustomer;
    }
}
