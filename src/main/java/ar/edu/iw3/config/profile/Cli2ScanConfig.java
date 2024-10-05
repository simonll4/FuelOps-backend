package ar.edu.iw3.config.profile;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
//Repositorios
@EnableJpaRepositories(basePackages = "ar.edu.iw3", // ar.edu.iw3.integration.cli2.model.controller
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "ar\\.edu\\.iw3\\.integration\\.cli1\\..*" )
                //Se pueden definir más filtros de exclusión
                //,@ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.magm\\.backend\\.integration\\.cliN\\..*" )
        })


//Entidades
@EntityScan(basePackages = {
        "ar.edu.iw3.model",
        "ar.edu.iw3.auth",
        "ar.edu.iw3.integration.cli2.model"
},
        basePackageClasses = {
                // Se pueden cargar entidades particulares que no estén en los paquetes base
                //ar.edu.iua.iw3.integration.cliN.model.Entidad1.class,
                //ar.edu.iua.iw3.integration.cliN.model.Entidad2.class
        })
//Las dos líneas que siguen son equivalentes, se puede usar cualquiera
@ConditionalOnExpression(value = "'${spring.profiles.active:-}'=='cli2' OR '${spring.profiles.active:-}'=='mysqldev'")
@Profile({"cli2","mysqldev"})
public class Cli2ScanConfig {

}
