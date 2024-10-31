package ar.edu.iw3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // Crea un broker de mensajes en memoria, los suscriptores deben hacerlo con el
        // prefijo /topic
        // broker --> suscriptor (SUSCRIPCIONES)
        config.enableSimpleBroker("/topic");

        //Agrega un prefijo a los mensajes recibidos desde los publicadores, a esto lo definiremos en el
        //controlador con @MessageMapping("/algo"), por lo tanto el cliente que publique, deberá hacerlo en /ws/algo
        // publicador ---> broker (PUBLICACIONES)
        //config.setApplicationDestinationPrefixes("/ws");

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //Se agrega un endpoint denominado /chat y uno /graph, además de agrega la característica SockJS,
        //lo que permite disponer de vías alternativas en el caso de que el navegador no soporte websoket
        //o existan restricciones de proxy por ejemplo.
        //El endpoint es el punto en común "físico" de la comunicación
        // Ejemplos
        //registry.addEndpoint("/chat");
        //registry.addEndpoint("/chat").withSockJS();
        //registry.addEndpoint("/graph");
        // registry.addEndpoint("/graph").withSockJS();

        registry.addEndpoint("/alarms").setAllowedOrigins("http://localhost:3000");
        registry.addEndpoint("/alarms").withSockJS();

    }

}
