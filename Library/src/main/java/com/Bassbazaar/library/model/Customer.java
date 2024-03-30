package com.Bassbazaar.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@EqualsAndHashCode
@Table(name = "Customers", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Customer implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private long id;
    private String firstName;
    private String  lastName;
    private String email;
    private String password;
    private String mobileNumber;
    private String roles;
    @Column(name = "is_activated")
    private boolean isActivated;



}

//serializable: class is serialized
//   --
//means class can converted into bytestream  for storage and transmission

