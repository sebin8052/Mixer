package com.Bassbazaar.library.service;

import com.Bassbazaar.library.model.Customer;
import com.Bassbazaar.library.model.Wishlist;

import java.util.List;

public interface WishlistService
{
    List<Wishlist> findAllByCustomer(Customer customer);
    boolean findByProductId(long id,Customer customer);
    Wishlist save(long productId,Customer customer);
    void deleteWishlist(long id);
}
