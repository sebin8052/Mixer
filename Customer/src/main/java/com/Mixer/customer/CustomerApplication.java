package com.Mixer.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.Mixer.library.*", "com.Mixer.customer"})
@EnableJpaRepositories(value = "com.Mixer.library.repository")
@EntityScan(value = "com.Mixer.library.model")
public class CustomerApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(CustomerApplication.class, args);
    }

}
