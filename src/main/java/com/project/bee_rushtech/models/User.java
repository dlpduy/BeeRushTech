package com.project.bee_rushtech.models;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String password;
    private String address;
    private String phoneNumber;
    private Boolean isActive;
    private Date dateOfBirth;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String passwordResetToken;

    private String role;
}
