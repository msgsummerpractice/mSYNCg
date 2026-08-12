package com.example.demo.model;


import java.util.Objects;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "check_in")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "qr_code", nullable = false, length = 255)
    private String qrCode;

    @Column(nullable = false)
    private Long code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckIn checkIn = (CheckIn) o;
        return Objects.equals(id, checkIn.id) && 
               Objects.equals(qrCode, checkIn.qrCode) && 
               Objects.equals(code, checkIn.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, qrCode, code);
    }

    @Override
    public String toString() {
        return "CheckIn{" +
                "id=" + id +
                ", qrCode='" + qrCode + '\'' +
                ", code=" + code +
                '}';
    }
}