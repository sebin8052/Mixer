package com.Bassbazaar.library.service.impl;

import com.Bassbazaar.library.dto.CouponDto;
import com.Bassbazaar.library.model.Coupon;
import com.Bassbazaar.library.repository.CouponRepository;
import com.Bassbazaar.library.service.CouponService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {
    private CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    /*   CouponController   */
    @Override
    public Coupon save(CouponDto couponDto) {
        Coupon coupon=new Coupon();
        coupon.setCode(couponDto.getCode());
        coupon.setDescription(couponDto.getDescription());
        coupon.setCount(couponDto.getCount());
        coupon.setOffPercentage(couponDto.getOffPercentage());
        coupon.setMaxOff(couponDto.getMaxOff());
        coupon.setExpiryDate(couponDto.getExpiryDate());
        coupon.setEnabled(true);
        couponRepository.save(coupon);
        return coupon;
    }

    /* OrderController / CouponController  */
    @Override
    public List<CouponDto> getAllCoupons() {
        List<Coupon> couponList=couponRepository.findAll();
        List<CouponDto>couponDtoList = transferData(couponList);
        return couponDtoList;
    }

    /*     OrderController   */
    @Override
    public double applyCoupon(String couponCode, double totalPrice) {
        Coupon coupon= couponRepository.findCouponByCode(couponCode);
        double discountPrice = totalPrice * (coupon.getOffPercentage() / 100.0);
        if(discountPrice > coupon.getMaxOff()){
            discountPrice = coupon.getMaxOff();
        }
        coupon.setCount(coupon.getCount()-1);
        couponRepository.save(coupon);
        double updatedTotalPrice = totalPrice-discountPrice;
        String formattedTotalPrice = String.format("%.2f", updatedTotalPrice);

        return Double.parseDouble(formattedTotalPrice);
    }

    /*     OrderController     */
    @Override
    public boolean findByCouponCode(String couponCode) {
        Coupon coupon=couponRepository.findCouponByCode(couponCode);
        if(coupon==null){
            return false;
        }else if(!coupon.isEnabled() || coupon.isExpired()){
            return false;
        }
        return true;
    }

    /*     OrderController     */
    @Override
    public Coupon findByCode(String couponCode) {
        return couponRepository.findCouponByCode(couponCode);
    }

    /*    CouponController   */
    @Override
    public CouponDto findById(long id) {
        Coupon coupon=couponRepository.findById(id);
        CouponDto couponDto=new CouponDto();
        couponDto.setId(coupon.getId());
        couponDto.setCode(coupon.getCode());
        couponDto.setDescription(coupon.getDescription());
        couponDto.setCount(coupon.getCount());
        couponDto.setOffPercentage(coupon.getOffPercentage());
        couponDto.setMaxOff(coupon.getMaxOff());
        couponDto.setExpiryDate(coupon.getExpiryDate());
        couponDto.setEnabled(coupon.isEnabled());
        return couponDto;
    }

    /*       CouponController     */
    @Override
    public Coupon update(CouponDto couponDto) {
        long id=couponDto.getId();
        Coupon coupon=couponRepository.findById(id);
        coupon.setCode(couponDto.getCode());
        coupon.setDescription(couponDto.getDescription());
        coupon.setCount(couponDto.getCount());
        coupon.setOffPercentage(couponDto.getOffPercentage());
        coupon.setMaxOff(couponDto.getMaxOff());
        coupon.setExpiryDate(couponDto.getExpiryDate());
        couponRepository.save(coupon);
        return coupon;
    }

    /*     CouponController    */
    @Override
    public void enable(long id) {
        Coupon coupon=couponRepository.findById(id);
        coupon.setEnabled(true);
        couponRepository.save(coupon);
    }

    /*        CouponController     */
    @Override
    public void disable(long id) {
        Coupon coupon=couponRepository.findById(id);
        coupon.setEnabled(false);
        couponRepository.save(coupon);
    }

    /*    CouponController */
    @Override
    public void deleteCoupon(long id) {
        Coupon coupon=couponRepository.findById(id);
        couponRepository.delete(coupon);
    }

    /*     CouponServiceImp     */
    public List<CouponDto> transferData(List<Coupon> couponList){
        List<CouponDto> couponDtoList=new ArrayList<>();
        for(Coupon coupon : couponList){
            CouponDto couponDto=new CouponDto();
            couponDto.setId(coupon.getId());
            couponDto.setCode(coupon.getCode());
            couponDto.setDescription(coupon.getDescription());
            couponDto.setCount(coupon.getCount());
            couponDto.setOffPercentage(coupon.getOffPercentage());
            couponDto.setMaxOff(coupon.getMaxOff());
            couponDto.setExpiryDate(coupon.getExpiryDate());
            couponDto.setEnabled(coupon.isEnabled());
            couponDtoList.add(couponDto);
        }
        return couponDtoList;
    }

    /*       CouponController         */
    @Override
    public boolean existsCouponByName(String name) {
        return couponRepository.existsByCode(name);
    }
}
