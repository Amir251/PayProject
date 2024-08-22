package com.snap.wallet.demo.wallet_demo.repository;

import com.snap.wallet.demo.wallet_demo.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByEmail() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail("amir@yahoo.com");
        user.setFirstName("Amir");
        user.setLastName("Shahravi");
        user.setUserId("user123");
        userRepository.save(user);

        // Act
        Optional<UserEntity> foundUser = userRepository.findByEmailIgnoreCase("amir@yahoo.com");

        // Assert
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getEmail()).isEqualTo("amir@yahoo.com");
    }

    @Test
    public void testFindByUserId() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail("amir@yahoo.com");
        user.setFirstName("Amir");
        user.setLastName("Shahravi");
        user.setUserId("user123");
        userRepository.save(user);

        // Act
        Optional<UserEntity> foundUser = userRepository.findByUserId("user123");

        // Assert
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getUserId()).isEqualTo("user123");
    }

    @Test
    public void testFindByPhone() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail("amir@yahoo.com");
        user.setFirstName("Amir");
        user.setLastName("Shahravi");
        user.setUserId("user123");
        user.setPhone("0935");
        userRepository.save(user);

        // Act
        Optional<UserEntity> foundUser = userRepository.findByPhone("0935");

        // Assert
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getPhone()).isEqualTo("0935");
    }

}