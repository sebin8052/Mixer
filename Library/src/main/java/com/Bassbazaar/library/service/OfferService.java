package com.Bassbazaar.library.service;

import com.Bassbazaar.library.dto.OfferDto;
import com.Bassbazaar.library.model.Offer;

import java.util.List;

public interface OfferService
{
    List<OfferDto> getAllOffers();
    Offer Save(OfferDto offerDto);
    OfferDto findById(long id);
    Offer update(OfferDto offerDto);
    void disable(long id);
    void enable(long id);
    void deleteOffer(long id);
}
