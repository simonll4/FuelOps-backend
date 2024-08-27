package ar.edu.iw3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.business.IProductBusiness;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication 
@Slf4j
public class BackendApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		
	}

	@Autowired
	private IProductBusiness productBusiness;
	
	@Override
	public void run(String... args) throws Exception {
		
		/*
		try {
			Product p=new Product();
			p.setProduct("Arroz");
			p.setPrice(156.67);
			productBusiness.add(p);
			Product p1=productBusiness.load(p.getId());
			Product p2=productBusiness.load(p.getProduct());
			log.info(p1.toString());
			log.info(p2.toString());
		} catch (Exception e) {
			log.warn(e.getMessage());
		}*/
		
	}

}
