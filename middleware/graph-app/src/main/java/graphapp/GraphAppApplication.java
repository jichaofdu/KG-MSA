package graphapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories("graphapp.repositories")
public class GraphAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphAppApplication.class, args);
    }

}