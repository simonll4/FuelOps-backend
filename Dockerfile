FROM magm3333/tomcat:10.1.28

COPY target/ROOT.war /opt/tomcat/webapps/ROOT/ROOT.zip

# Descomprime el archivo ZIP en el directorio de destino
RUN cd /opt/tomcat/webapps/ROOT && unzip ROOT.zip

# Exponer el puerto 8080
EXPOSE 8080

# Levantar el servicio de Tomcat
CMD ["catalina.sh", "run"]
