package com.example.demo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "telephone_nr", nullable = false, length = 20)
    private String telephoneNr;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return java.util.Objects.equals(id, driver.id) && 
               java.util.Objects.equals(name, driver.name) && 
               java.util.Objects.equals(telephoneNr, driver.telephoneNr);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, telephoneNr);
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", telephoneNr='" + telephoneNr + '\'' +
                '}';
    }
}