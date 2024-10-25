package ar.edu.iw3.integration.cli1.model;

import java.io.IOException;
import java.util.Date;

import ar.edu.iw3.integration.cli1.model.business.interfaces.ICustomerCli1Business;
import ar.edu.iw3.integration.cli1.model.business.interfaces.IDriverCli1Business;

import ar.edu.iw3.integration.cli1.model.business.interfaces.IProductCli1Business;

import ar.edu.iw3.integration.cli1.model.business.interfaces.ITruckCli1Business;
import ar.edu.iw3.integration.cli1.util.Utils;
import ar.edu.iw3.model.*;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.interfaces.*;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.edu.iw3.util.JsonUtils;

import static ar.edu.iw3.util.JsonAttributeConstants.*;

public class OrderCli1JsonDeserializer extends StdDeserializer<OrderCli1> {

    private static final long serialVersionUID = -3881285352118964728L;

    protected OrderCli1JsonDeserializer(Class<?> vc) {
        super(vc);
    }

    private ICustomerCli1Business customerBusiness;

    private IProductCli1Business productBusiness;

    private IDriverCli1Business driverBusiness;
    private ITruckCli1Business truckBusiness;
    private ITankBusiness tankBusiness;

    public OrderCli1JsonDeserializer(Class<?> vc, IDriverCli1Business driverBusiness, ITruckCli1Business truckBusiness,

                                     ICustomerCli1Business customerBusiness, IProductCli1Business productBusiness, ITankBusiness tankBusiness) {

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

        String orderNumber = JsonUtils.getString(node, ORDER_NUMBER_ATTRIBUTES, "");
        Date estimatedTime = JsonUtils.getDate(node, ORDER_ESTIMATED_DATE_ATTRIBUTES, String.valueOf(new Date()));
        float preset = JsonUtils.getValue(node, ORDER_PRESET_ATTRIBUTES, 0);

        r.setOrderNumberCli1(orderNumber);
        r.setEstimatedTime(estimatedTime);
        r.setPreset(preset);

        Driver driver = JsonUtils.getDriver(node, DRIVER_DOCUMENT_ATTRIBUTES, driverBusiness);

        Truck truck = (JsonUtils.getTruck(node, TRUCK_LICENSE_PLATE_ATTRIBUTES, truckBusiness, tankBusiness));

        Customer customer = (JsonUtils.getCustomer(node,CUSTOMER_NAME_ATTRIBUTES, customerBusiness));

        Product product = JsonUtils.getProduct(node, PRODUCT_NAME_ATTRIBUTES, productBusiness);

        if (product != null && customer != null && truck != null && driver != null) {
            r.setCustomer(customer);
            r.setDriver(driver);
            r.setProduct(product);
            r.setTruck(truck);
        }

        r.setAlarmAccepted(true);
        r.setStatus(Order.Status.ORDER_RECEIVED);

        return r;

    }
}
