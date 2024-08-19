package com.snap.wallet.demo.wallet_demo.config;

import com.snap.wallet.demo.wallet_demo.model.CredentialEntity;
import com.snap.wallet.demo.wallet_demo.model.RoleEntity;
import com.snap.wallet.demo.wallet_demo.model.UserEntity;
import com.snap.wallet.demo.wallet_demo.repository.CredentialRepository;
import com.snap.wallet.demo.wallet_demo.repository.RoleRepository;
import com.snap.wallet.demo.wallet_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    @Value("${user.first.name}")
    private String firstName;
    @Value("${user.last.name}")
    private String lastName;
    @Value("${user.email}")
    private String email;
    @Value("${user.phone}")
    private String phone;
    @Value("${user.passwd}")
    private String passwd;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public void run(String... args) {
        RoleEntity adminRole = roleRepository.findByNameIgnoreCase("ADMIN")
                .orElseGet(() -> roleRepository.save(new RoleEntity("ADMIN")));

        roleRepository.findByNameIgnoreCase("USER")
                .orElseGet(() -> roleRepository.save(new RoleEntity("USER")));

        if (userRepository.count() == 0) {
            UserEntity admin = createUserEntity(firstName, lastName, email, adminRole, phone);
            UserEntity userEntity = userRepository.save(admin);
            CredentialEntity credentialEntity = new CredentialEntity(encoder.encode(passwd), userEntity);
            credentialRepository.save(credentialEntity);
        }
    }


    public UserEntity createUserEntity(String firstName, String lastName, String email, RoleEntity roleByName, String phone) {
        return UserEntity.builder().userId(UUID.randomUUID().toString()).firstName(firstName).lastName(lastName).email(email).accountNonExpire(true).accountNonLocked(true).enabled(true).phone(phone).role(roleByName).build();
    }
}
