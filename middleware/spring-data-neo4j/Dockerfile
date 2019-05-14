FROM java:8-jre

ADD ./target/spring-data-neo4j-1.0.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/spring-data-neo4j-1.0.jar"]

EXPOSE 19871