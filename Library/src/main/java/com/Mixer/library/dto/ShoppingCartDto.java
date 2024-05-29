package com.Mixer.library.dto;

import com.Mixer.library.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDto
{
    private Long id;
    private double totalPrice;
    private int totalItems;
    private Customer customer;


    private Set<CartItemDto> cartItemDto;
}
