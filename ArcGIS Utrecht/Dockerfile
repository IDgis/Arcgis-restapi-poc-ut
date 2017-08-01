FROM azul/zulu-openjdk

EXPOSE 8080

# COPY jar to the container
COPY build/libs/arcgis-restapi-utrecht-0.1.1.jar /app/arcgis-restapi-utrecht-0.1.1.jar

# COPY needed files to container
COPY examples/ /examples/

ENTRYPOINT ["java", \
			"-jar", \
			"-DDB_USER=$DB_USER", \
			"-DDB_PASSWORD=$DB_PASSWORD", \
			"/app/arcgis-restapi-utrecht-0.1.1.jar"]