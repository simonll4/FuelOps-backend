package ar.edu.iw3.integration.cli1.model;

import java.io.IOException;
import java.util.Date;

import ar.edu.iw3.model.*;
import ar.edu.iw3.model.business.interfaces.*;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.edu.iw3.util.JsonUtiles;

public class OrderCli1JsonDeserializer extends StdDeserializer<OrderCli1> {

    private static final long serialVersionUID = -3881285352118964728L;

    protected OrderCli1JsonDeserializer(Class<?> vc) {
        super(vc);
    }

    private ICustomerBusiness customerBusiness;
    private IProductBusiness productBusiness;
    private IDriverBusiness driverBusiness;
    private ITruckBusiness truckBusiness;
    private ITankBusiness tankBusiness;

    public OrderCli1JsonDeserializer(Class<?> vc, IDriverBusiness driverBusiness, ITruckBusiness truckBusiness,
                                     ICustomerBusiness customerBusiness, IProductBusiness productBusiness, ITankBusiness tankBusiness) {
        super(vc);
        this.driverBusiness = driverBusiness;
        this.truckBusiness = truckBusiness;
        this.customerBusiness = customerBusiness;
        this.productBusiness = productBusiness;
        this.tankBusiness = tankBusiness;
    }

    @Override
    public OrderCli1 deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {

        OrderCli1 r = new OrderCli1();
        JsonNode node = jp.getCodec().readTree(jp);

        String orderNumber = JsonUtiles.getString(node, "number,order_number,order".split(","), "");
        Date estimatedTime = JsonUtiles.getDate(node, "estimated_date,estimated_date_order,estimated_time,estimated_time_order".split(","), String.valueOf(new Date()));
        float preset = JsonUtiles.getValue(node, "preset,order_preset".split(","), 0);

        r.setOrderNumberCli1(orderNumber);
        r.setEstimatedTime(estimatedTime);
        r.setPreset(preset);

        Driver driver = (JsonUtiles.getDriver(node, "driver_document,driver_document_number,document".split(","), driverBusiness));

        Truck truck = (JsonUtiles.getTruck(node, "truck_plate,truck_plate_number,license_plate,truck_license_plate".split(","), truckBusiness, tankBusiness));

        Customer customer = (JsonUtiles.getCustomer(node, "name,customer_name,business_name,customer".split(","), customerBusiness));

        Product product = JsonUtiles.getProduct(node, "product,product_name".split(","), productBusiness);

        if (product != null && customer != null && truck != null && driver != null) {
            r.setCustomer(customer);
            r.setDriver(driver);
            r.setProduct(product);
            r.setTruck(truck);
        }

        r.setStatus(Order.Status.ORDER_RECEIVED);

        return r;

    }
}
