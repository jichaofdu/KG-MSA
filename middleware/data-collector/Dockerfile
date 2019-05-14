FROM java:8-jre

ADD ./target/data-collector-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/data-collector-1.0.jar"]

EXPOSE 19871