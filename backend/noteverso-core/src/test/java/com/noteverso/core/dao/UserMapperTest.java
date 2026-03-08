package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void should_findUserByUsername_successfully() {
        // Arrange
        User user = constructUser("user1", "testuser", "test@test.com");
        userMapper.insert(user);

        // Act
        Optional<User> found = userMapper.findUserByUsername("testuser");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void should_findUserByUsername_returnEmpty_whenNotFound() {
        // Act
        Optional<User> found = userMapper.findUserByUsername("nonexistent");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void should_findUserByEmail_successfully() {
        // Arrange
        User user = constructUser("user2", "testuser2", "test2@test.com");
        userMapper.insert(user);

        // Act
        User found = userMapper.findUserByEmail("test2@test.com");

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test2@test.com");
        assertThat(found.getUsername()).isEqualTo("testuser2");
    }

    @Test
    void should_findUserByEmail_returnNull_whenNotFound() {
        // Act
        User found = userMapper.findUserByEmail("nonexistent@test.com");

        // Assert
        assertThat(found).isNull();
    }

    private User constructUser(String userId, String username, String email) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("hashedPassword");
        user.setAuthority("ROLE_USER");
        user.setHasPassword(1);
        user.setPremiumStatus(0);
        user.setIsPremium(0);
        user.setJoinedAt(Instant.now());
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return user;
    }
}
