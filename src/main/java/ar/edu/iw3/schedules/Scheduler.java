package ar.edu.iw3.schedules;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

//import ar.edu.iw3.integration.cli2.model.ProductCli2;
//import ar.edu.iw3.integration.cli2.model.business.IProductCli2Business;
import ar.edu.iw3.util.EmailBusiness;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@EnableAsync
@Slf4j
public class Scheduler {
    // fixedDelay e initialDelay se miden por defecto en milisegundos, lo varía
    // timeUnit
    @Scheduled(fixedDelay = 5, initialDelay = 3, timeUnit = TimeUnit.SECONDS)
    public void schedule1() {
        log.trace("Evento calendarizado cada 5 segundos, 3 iniciales");
    }

    /**
     * A cron-like expression, extending the usual UN*X definition to include
     * triggers on the second, minute, hour, day of month, month, and day of week.
     * For example, "0 * * * * MON-FRI" means once per minute on weekdays
     * (at the top of the minute - the 0th second).
     * The fields read from left to right are interpreted as follows.
     * second / minute / hour / day of month / month / day of week
     */
    @Scheduled(cron="0 3 10 * * *")
    public void schedule2() {
        log.info("Evento calendarizado a las 10:03 AM de cada día");
    }

    //@Autowired(required = false)
    //private IProductCli2Business productBusiness;

    @Autowired
    private EmailBusiness emailBusiness;

    // todo poner nuestro mail
    @Value("${expired.product.send.to:aca-va-mail}")
    private String expiredProductSendTo;

    // @Async
    // @Scheduled(fixedDelayString = "${verify.product.expired:120}", initialDelay = 5, timeUnit = TimeUnit.SECONDS);
    public void expired() {
//        if(productBusiness!=null) {
//            log.info("Verificando productos expirados...");
//            Calendar c = Calendar.getInstance();
//            c.setTime(new Date());
//            c.add(Calendar.DAY_OF_MONTH, 1);
//            c.set(Calendar.HOUR_OF_DAY, 8);
//            c.set(Calendar.MINUTE, 0);
//            c.set(Calendar.SECOND, 0);
//            c.set(Calendar.MILLISECOND, 0);
//            try {
//                List<ProductCli2> list=productBusiness.listExpired(c.getTime());
//                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String text="Productos que vencen:\n";
//                for(ProductCli2 pc2: list) {
//                    log.info("Producto {} expirado {}",pc2.getProduct(), sdf.format(pc2.getExpirationDate()));
//                    text+=String.format("Producto %s vence %s\n", pc2.getProduct(), sdf.format(pc2.getExpirationDate()));
//                }
//                if(list.size()>0) {
//                    emailBusiness.sendSimpleMessage(expiredProductSendTo, "Productos que vencen!", text);
//                }
//            } catch (BusinessException e) {
//                log.error("{}.expired() Error: '{}'",this.getClass().getName(),e.getMessage());
//            }
//        }
    }
}