package com.Bassbazaar.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.Bassbazaar.library.*", "com.Bassbazaar.customer"})
@EnableJpaRepositories(value = "com.Bassbazaar.library.repository")
@EntityScan(value = "com.Bassbazaar.library.model")
public class CustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

}
