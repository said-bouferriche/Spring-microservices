package com.microsproject.inventoryservice;

import com.microsproject.inventoryservice.model.Inventory;
import com.microsproject.inventoryservice.repository.InventoryRepository;
import com.microsproject.inventoryservice.service.InventoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApplication {


    public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository){
        return args -> {
            Inventory inventory = new Inventory();
            inventory.setSkuCode("iphone 13");
            inventory.setQuantity(1000);

            Inventory inventory1 = new Inventory();
            inventory1.setSkuCode("iphone 13 blue");
            inventory1.setQuantity(1000);

            inventoryRepository.save(inventory1);
            inventoryRepository.save(inventory);

        };
    }
}
