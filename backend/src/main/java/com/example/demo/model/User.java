package com.example.demo.model;


import java.util.Arrays;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private Location location;

    @Column(nullable = false)
    private Boolean status = true;

    @Column(columnDefinition = "bytea")
    private byte[] image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private UserRole role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && 
               Objects.equals(firstName, user.firstName) && 
               Objects.equals(lastName, user.lastName) && 
               Objects.equals(email, user.email) && 
               Objects.equals(password, user.password) && 
               location == user.location && 
               Objects.equals(status, user.status) && 
               role == user.role &&
               Arrays.equals(image, user.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, firstName, lastName, email, password, location, status, role);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", location=" + location +
                ", status=" + status +
                ", role=" + role +
                ", image=" + (image != null ? "[PREZENT]" : "[LIPSA]") +
                '}';
    }

    public User orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }
}