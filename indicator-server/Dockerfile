# TWO-STAGE BUILD DOCKER FILE

ARG BUILD_IMAGE=maven:3.8.3-openjdk-17
ARG RUNTIME_IMAGE=openjdk:18

#############################################################################################
###                Stage where Docker is pulling all maven dependencies                   ###
#############################################################################################
FROM ${BUILD_IMAGE} as dependencies

COPY ./pom.xml ./

RUN mvn -B dependency:go-offline
#############################################################################################


#############################################################################################
###              Stage where Docker is building spring boot app using maven               ###
#############################################################################################
FROM dependencies as build

COPY ./src ./src

RUN mvn -B clean package -DskipTests
#############################################################################################


#############################################################################################
### Stage where Docker is running a java process to run a service built in previous stage ###
#############################################################################################
FROM ${RUNTIME_IMAGE}
ARG JAR_FILE=target/indicator-server-0.0.1-SNAPSHOT.jar
COPY --from=build ${JAR_FILE} app.jar
RUN echo "#!/bin/bash" >> entrypoint.sh && \
    echo "sleep \$ENTRY_DELAY" >> entrypoint.sh && \
    echo "java -jar app.jar" >> entrypoint.sh   && \
    chmod +x entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
#############################################################################################