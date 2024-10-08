package ar.edu.iw3.integration.cli1.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.ICategoryBusiness;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.util.JsonUtiles;

public class ProductCli1JsonDeserializer extends StdDeserializer<ProductCli1> {

    private static final long serialVersionUID = -3881285352118964728L;

    protected ProductCli1JsonDeserializer(Class<?> vc) {
        super(vc);
    }

    private ICategoryBusiness categoryBusiness;

    public ProductCli1JsonDeserializer(Class<?> vc, ICategoryBusiness categoryBusiness) {
        super(vc);
        this.categoryBusiness = categoryBusiness;
    }

    @Override
    public ProductCli1 deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {

        ProductCli1 r = new ProductCli1();
        JsonNode node = jp.getCodec().readTree(jp);

        String codTemporal = System.currentTimeMillis() + "";
        String code = JsonUtiles.getString(node, "product_code,code_product,code".split(","),
                codTemporal);

        String productDesc = JsonUtiles.getString(node,
                "product,description,product_description,product_name".split(","), null);
        double price = JsonUtiles.getDouble(node, "product_price,price_product,price".split(","), 0);
        boolean stock = JsonUtiles.getBoolean(node, "stock,in_stock".split(","), false);
        r.setCodCli1(code);
        r.setProduct(productDesc);
        r.setPrice(price);
        r.setStock(stock);
        r.setCodCli1Temp(code.equals(codTemporal));

        String categoryName = JsonUtiles.getString(node, "category,product_category,category_product".split(","), null);
        if (categoryName != null) {
            try {
                r.setCategory(categoryBusiness.load(categoryName));
            } catch (NotFoundException | BusinessException ignored) {
            }
        }
        return r;
    }
}