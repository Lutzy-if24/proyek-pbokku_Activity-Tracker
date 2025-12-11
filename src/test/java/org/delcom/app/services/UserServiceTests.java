package org.delcom.app.services;

import java.util.Optional;
import java.util.UUID;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // 1. Test Create User
    @Test
    void testCreateUser() {
        User user = new User("Test", "test@email.com", "pass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User created = userService.createUser("Test", "test@email.com", "pass");
        
        assertNotNull(created);
        assertEquals("Test", created.getName());
    }

    // 2. Test Get User By Email
    @Test
    void testGetUserByEmail_Found() {
        User user = new User();
        user.setEmail("test@email.com");
        when(userRepository.findFirstByEmail("test@email.com")).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("test@email.com");
        assertNotNull(result);
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findFirstByEmail("unknown@email.com")).thenReturn(Optional.empty());

        User result = userService.getUserByEmail("unknown@email.com");
        assertNull(result);
    }

    // 3. Test Get User By ID
    @Test
    void testGetUserById_Found() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(new User()));

        User result = userService.getUserById(id);
        assertNotNull(result);
    }

    @Test
    void testGetUserById_NotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        User result = userService.getUserById(id);
        assertNull(result);
    }

    // 4. Test Update User
    @Test
    void testUpdateUser_Success() {
        UUID id = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setName("Old Name");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updated = userService.updateUser(id, "New Name", "new@email.com");

        assertNotNull(updated);
        assertEquals("New Name", updated.getName());
    }

    @Test
    void testUpdateUser_NotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        User result = userService.updateUser(id, "Name", "email");
        assertNull(result); // Baris merah di method updateUser jadi hijau
    }

    // 5. Test Update Password
    @Test
    void testUpdatePassword_Success() {
        UUID id = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setPassword("OldPass");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updated = userService.updatePassword(id, "NewPass");

        assertNotNull(updated);
        assertEquals("NewPass", updated.getPassword());
    }

    // --- INI TEST YANG MENGHIJAUKAN BARIS MERAH KAMU ---
    @Test
    void testUpdatePassword_NotFound() {
        UUID id = UUID.randomUUID();
        
        // Simulasi: Cari user by ID, tapi return Empty (User tidak ada)
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        User result = userService.updatePassword(id, "NewPass");
        
        // Assert harus NULL, karena masuk blok if (user == null) -> return null
        assertNull(result); 
    }
}