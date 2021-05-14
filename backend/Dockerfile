FROM hub.segma.tech/library/java:8-jre-centos7-apollo

COPY target/service-1.0.0.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
