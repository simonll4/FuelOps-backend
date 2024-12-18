# Backend - FuelOps

FuelOps es un sistema backend diseñado para administrar y monitorear órdenes de carga de gas líquido en una planta de
combustible,
integrando datos en tiempo real y sincronizándose con sistemas externos. Este servicio backend permite la creación,
actualización
y cierre de órdenes de carga, así como el reporte conciliación de datos ,de pesaje y carga, al final del proceso.

# Funcionalidades Principales

- Autenticación y autorizacion de usuarios.
- Sincronización con sistemas externos para recibir datos iniciales de ordenes y de pesajes.
- Creación y actualización de órdenes de carga.
- Gestión de estados de la orden (creación, carga en proceso, cierre).
- Registro de datos en tiempo real para monitoreo y almacenamiento de carga.
- Reportes conciliación de cargas.

## Modulos

- Auth: Modulo encargado de exponer API para la autenticación y autorización de usuarios.
- Integration: Modulo encargado de exponer API para la integración con sistemas externos.
- General Services: Modulo encargado de exponer API para cliente web.

## Stack Tecnológico

- Java
- Spring Framework
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL

## Perfiles de Ejecución

- **mysqldev**: Perfil de desarrollo que activa el perfil de Spring `mysqldev`. Utiliza una base de datos MySQL
  configurada para desarrollo.
- **mysqlprod**: Perfil de producción que activa el perfil de Spring `mysqlprod`. Utiliza una base de datos MySQL
  configurada para producción.
- **war-build**: Perfil de empaquetado que configura el proyecto para ser empaquetado como un archivo WAR. Este perfil
  es activado por defecto si la propiedad `build` está configurada como `war`.
- **jar-build**: Perfil de empaquetado que configura el proyecto para ser empaquetado como un archivo JAR. Este perfil
  es activado si la propiedad `build` está configurada como `jar`.

Para activar un perfil específico, puedes usar el siguiente comando de Maven:

```sh
mvn clean install -P<profile-id>
```

## Instalacion y Ejecucion del Proyecto: Dev

Antes de iniciar el servidor en modo desarrollo, ejecute el archivo `docker-compose-dev.yml`.
Este archivo configura y levanta una base de datos para el perfil de ejecución mysqldev.

Para iniciar el servidor en modo produccion se uilizo el archivo `docker-compose-prod.yml`.



## Postman Collection
La colección de Postman para probar la API se encuentra en el siguiente enlace:
[Postman Collection - FuelOps API](https://drive.google.com/file/d/10PLZQ9K2Ebvr_ctLqo4R80MPGvfnf38l/view?usp=sharing)


## API Documentation
La documentacion de la API se encuentra disponible en Swagger UI. Para acceder
a la documentación de la API Utilizar el siguiente enlace: http://localhost:8080/swagger-ui.html


## Contributors
- [Simonll4](https://github.com/simonll4)
- [MattGoode7](https://github.com/MattGoode7)
- [Pandulc](https://github.com/Pandulc)