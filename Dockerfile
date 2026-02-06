#ETAPA 1: Construccion del JAR
#Dockerfile: Divido en dos capas
FROM maven:3.8.5-openjdk-17 AS build

#Directorio del trabajo dentro del contenedor
WORKDIR /app

#Copiar el archivo de configuracion 
COPY pom.xml /
#Descarga las dependencias
RUN mvn dependency:go-offline

# Concederle el codigo
COPY src ./src
RUN mvn clean package -DskipTests

RUN  ls -la /app/target 

#ETAPA 2: Construccion de la imagen final
FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/spring-boot-starter-parent-0.0.1-SNAPSHOT.jar /app/spring-boot-starter-parent.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar","/app/spring-boot-starter-parent.jar" ]




