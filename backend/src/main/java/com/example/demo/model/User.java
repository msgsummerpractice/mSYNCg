package com.example.demo.model;


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
        User user = (User) o;
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return java.util.Objects.equals(id, user.id) && 
               java.util.Objects.equals(firstName, user.firstName) && 
               java.util.Objects.equals(lastName, user.lastName) && 
               java.util.Objects.equals(email, user.email) && 
               java.util.Objects.equals(password, user.password) && 
               location == user.location && 
               java.util.Objects.equals(status, user.status) && 
               role == user.role &&
               java.util.Arrays.equals(image, user.image);
    }

    @Override
    public int hashCode() {
        int result = java.util.Objects.hash(id, firstName, lastName, email, password, location, status, role);
        result = 31 * result + java.util.Arrays.hashCode(image);
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
}