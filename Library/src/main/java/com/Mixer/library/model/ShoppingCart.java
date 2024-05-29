package com.Mixer.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Entity
@Data
@Table
public class ShoppingCart
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shopping_cart_id")
    private Long id;

    private double totalPrice;

    private int totalItems;



    @OneToMany(cascade = CascadeType.ALL,mappedBy = "cart")
    private Set<CartItem> cartItems;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="customer_id",referencedColumnName = "customer_id")
    private Customer customer;



    public ShoppingCart()
    {
        this.cartItems = new HashSet<>();
        this.totalItems = 0;
        this.totalPrice = 0.0;
    }
}
