package com.Bassbazaar.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.Bassbazaar.library.*", "com.Bassbazaar.admin.*"})
@EnableJpaRepositories(value = "com.Bassbazaar.library.repository")
@EntityScan(value = "com.Bassbazaar.library.model")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

}
