FROM openjdk:8
ADD target/eureka-server.jar eureka-server.jar
EXPOSE 9091
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]