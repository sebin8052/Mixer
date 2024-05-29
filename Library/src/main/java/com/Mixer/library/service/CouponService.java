package com.Mixer.library.service;

import com.Mixer.library.dto.CouponDto;
import com.Mixer.library.model.Coupon;

import java.util.List;

public interface CouponService {
    Coupon save(CouponDto couponDto);
    List<CouponDto> getAllCoupons();
    double applyCoupon(String couponCode,double totalPrice);



    boolean findByCouponCode(String couponCode);
    Coupon findByCode(String couponCode);
    CouponDto findById(long id);
    Coupon update(CouponDto couponDto);
    void enable(long id);
    void disable(long id);
    void deleteCoupon(long id);
    boolean existsCouponByName(String name);
}
