package com.Mixer.library.service;

import com.Mixer.library.model.Customer;
import com.Mixer.library.model.Wishlist;

import java.util.List;

public interface WishlistService
{
    List<Wishlist> findAllByCustomer(Customer customer);
    boolean findByProductId(long id,Customer customer);
    Wishlist save(long productId,Customer customer);
    void deleteWishlist(long id);
}
