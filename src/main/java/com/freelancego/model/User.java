package com.freelancego.model;

import com.freelancego.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;

    private String username;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    @Enumerated(EnumType.STRING)
    private Role role;

    public byte[] getImageData() {
        return imageData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
