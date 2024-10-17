package ar.edu.iw3.util;

import ar.edu.iw3.model.Customer;
import ar.edu.iw3.model.Driver;
import ar.edu.iw3.model.Tanker;
import ar.edu.iw3.model.Truck;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashSet;
import java.util.Set;

public class Utils {
    public static Driver buildDriver(JsonNode driverNode) {
        Driver newDriver = new Driver();

        String name = JsonUtiles.getString(driverNode,"name,driver_name,driver".split(","),"");
        if(name != null && !name.isEmpty()){
            newDriver.setName(name);
        }

        String lastName = JsonUtiles.getString(driverNode,"lastname,driver_lastname,last_name,driver_last_name".split(","),"");
        if(lastName != null && !lastName.isEmpty()){
            newDriver.setLastName(lastName);
        }

        String identityDocument = JsonUtiles.getString(driverNode,"driver_document,driver_document_number,document".split(","),"");
        if(identityDocument != null && !identityDocument.isEmpty()){
            newDriver.setDocument(identityDocument);
        }

        return newDriver;
    }

    public static Truck buildTruck(JsonNode truckNode, JsonNode tanksNode) {
        Truck newTruck = new Truck();

        String licensePlate = JsonUtiles.getString(truckNode,"truck_plate,truck_plate_number,license_plate,truck_license_plate".split(","),"");
        if(licensePlate != null && !licensePlate.isEmpty()){
            newTruck.setLicensePlate(licensePlate);
        }

        String description = JsonUtiles.getString(truckNode,"description,truck_description".split(","),"");
        if(description != null && !description.isEmpty()){
            newTruck.setDescription(description);
        }

        Set<Tanker> newTankers = new HashSet<>();
        if (tanksNode != null && tanksNode.isArray()) {
            for (JsonNode tankNode : tanksNode) {
                Tanker tanker = new Tanker();

                long capacityLiters = (long) JsonUtiles.getValue(tankNode,"capacity_liters,capacity".split(","),0);
                if (capacityLiters > 0) {
                    tanker.setCapacity_liters(capacityLiters);
                }

                String license = JsonUtiles.getString(tankNode,"license,license_plate,plate".split(","),"");
                if(license != null && !license.isEmpty()){
                    tanker.setLicense(license);
                }

                // Agregar al set
                newTankers.add(tanker);
            }
        }

        newTruck.setTanks(newTankers);
        return newTruck;
    }

    public static Customer buildCustomer(JsonNode customerNode) {
        Customer newCustomer = new Customer();

        String businessName = JsonUtiles.getString(customerNode,"name,customer_name,business_name,customer".split(","), "");
        if(businessName != null && !businessName.isEmpty()){
            newCustomer.setBusinessName(businessName);
        }

        String email = JsonUtiles.getString(customerNode,"mail,email,contact,mail_contact".split(","),"");
        if(email != null && !email.isEmpty()){
            newCustomer.setEmail(email);
        }

        return newCustomer;
    }
}
