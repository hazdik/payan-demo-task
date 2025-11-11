package com.payan.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payan.demo.entity.User;
import com.payan.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole("USER");
        testUser.setEnabled(true);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateUser_Success() throws Exception {
        // Arrange
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.fullName", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllUsers_Success() throws Exception {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testuser2");
        user2.setFullName("Test User 2");
        user2.setEmail("test2@example.com");

        List<User> users = Arrays.asList(testUser, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("testuser")))
                .andExpect(jsonPath("$[1].username", is("testuser2")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllUsers_NoContent() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUserById_Found() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.fullName", is("Test User")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUserById_NotFound() throws Exception {
        // Arrange
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUserByUsername_Found() throws Exception {
        // Arrange
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/username/testuser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUserByUsername_NotFound() throws Exception {
        // Arrange
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/username/nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateUser_Success() throws Exception {
        // Arrange
        User updatedUser = new User();
        updatedUser.setUsername("updateduser");
        updatedUser.setFullName("Updated User");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRole("ADMIN");
        updatedUser.setEnabled(false);

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("updateduser")))
                .andExpect(jsonPath("$.fullName", is("Updated User")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateUser_NotFound() throws Exception {
        // Arrange
        when(userService.updateUser(anyLong(), any(User.class)))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(put("/api/users/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteUser_Success() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteUser_NotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("User not found")).when(userService).deleteUser(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/users/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testToggleUserStatus_Success() throws Exception {
        // Arrange
        testUser.setEnabled(false);
        when(userService.toggleUserStatus(1L)).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(patch("/api/users/1/toggle-status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled", is(false)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testToggleUserStatus_NotFound() throws Exception {
        // Arrange
        when(userService.toggleUserStatus(anyLong()))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(patch("/api/users/999/toggle-status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCheckUsernameExists_True() throws Exception {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/users/exists/testuser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCheckUsernameExists_False() throws Exception {
        // Arrange
        when(userService.existsByUsername("nonexistent")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/users/exists/nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
