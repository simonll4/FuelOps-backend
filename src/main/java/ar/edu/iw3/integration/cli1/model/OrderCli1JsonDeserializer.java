package ar.edu.iw3.integration.cli1.model;

import java.io.IOException;
import java.util.Date;

import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.interfaces.*;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
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

    public OrderCli1JsonDeserializer(Class<?> vc, IDriverBusiness driverBusiness, ITruckBusiness truckBusiness,
            ICustomerBusiness customerBusiness, IProductBusiness productBusiness) {
        super(vc);
        this.driverBusiness = driverBusiness;
        this.truckBusiness = truckBusiness;
        this.customerBusiness = customerBusiness;
        this.productBusiness = productBusiness;
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

        try {
            r.setDriver(JsonUtiles.getDriver(node, new String[]{"driver_document", "driver_document_number"}, driverBusiness));
        } catch (NotFoundException | BusinessException ignored) {
        }

        try {
            r.setTruck(JsonUtiles.getTruck(node, new String[]{"truck_plate", "truck_plate_number"}, truckBusiness));
        } catch (NotFoundException | BusinessException ignored) {
        }

        try {
            r.setCustomer(JsonUtiles.getCustomer(node, new String[]{"name", "customer_name", "business_name"}, customerBusiness));
        } catch (NotFoundException | BusinessException ignored) {
        }

        Product product = null;
        try {
            product = JsonUtiles.getProduct(node, "product,product_name".split(","), productBusiness);
        } catch (NotFoundException e) {
            // TODO Ver como resolver esta parte cuando no encuentra el producto
        }
        if (product != null) {
            r.setProduct(product);
        }

        return r;
    }
}