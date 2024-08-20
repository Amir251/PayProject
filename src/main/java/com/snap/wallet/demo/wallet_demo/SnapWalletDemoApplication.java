package com.snap.wallet.demo.wallet_demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "FinTech Microservice API",
                description = "This microservice handles core financial operations such as user account management, transactions, and wallet services.",
                version = "1.0.0",
                contact = @Contact(name = "Amir Shahravi", email = "amir.shahravi251@gmail.com")
        ),
        servers = {
                @Server(url = "http://localhost:8081", description = "Local development server")
        }
)
public class SnapWalletDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnapWalletDemoApplication.class, args);
    }

}
