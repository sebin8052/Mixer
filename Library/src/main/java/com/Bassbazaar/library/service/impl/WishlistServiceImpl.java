package com.Bassbazaar.library.service.impl;

import com.Bassbazaar.library.model.Customer;
import com.Bassbazaar.library.model.Product;
import com.Bassbazaar.library.model.Wishlist;
import com.Bassbazaar.library.repository.WishlistRepository;
import com.Bassbazaar.library.service.ProductService;
import com.Bassbazaar.library.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistServiceImpl implements WishlistService
{
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private ProductService productService;

    /*     WishlistController    */
    @Override
    public List<Wishlist> findAllByCustomer(Customer customer)
    {
        List<Wishlist>Wishlists=wishlistRepository.findAllByCustomerId(customer.getId());
        return Wishlists;
    }

    /*     WishlistController    */
    @Override
    public boolean findByProductId(long productId, Customer customer) {
        boolean exists= wishlistRepository.findByProductIdAndCustomerId(productId,customer.getId());
        return exists;
    }

    /*     WishlistController    */
    @Override
    public Wishlist save(long productId, Customer customer) {
        Product product=productService.findBYId(productId);
        Wishlist wishlist=new Wishlist();
        wishlist.setProduct(product);
        wishlist.setCustomer(customer);
        wishlistRepository.save(wishlist);
        return wishlist;
    }

    /*     WishlistController    */
    @Override
    public void deleteWishlist(long id) {
        Wishlist wishlist=wishlistRepository.findById(id);
        wishlistRepository.delete(wishlist);
    }
}
