package ar.edu.iw3.integration.cli2.model;

public interface ProductCli2SlimView {
    Long getId();
    String getProduct();
    Double getPrice();
    Category getCategory();
    interface Category {
        String getCategory();
    }
}