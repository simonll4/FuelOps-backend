package ar.edu.iw3;

import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class BackendApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);

    }

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${spring.jackson.time-zone:-}")
    private String backendTimezone;

    @Override
    public void run(String... args) throws Exception {
        String tzId = backendTimezone.equals("-") ? TimeZone.getDefault().getID() : backendTimezone;
        TimeZone.setDefault(TimeZone.getTimeZone(tzId));

        log.info("-------------------------------------------------------------------------------------------------------------------");
        log.info("- Initial TimeZone: {} ({})", TimeZone.getDefault().getDisplayName(), TimeZone.getDefault().getID());
        log.info("- Perfil activo {}", profile);

    }

}