package com.Bassbazaar.library.service;

import com.Bassbazaar.library.dto.AddressDto;
import com.Bassbazaar.library.model.Address;

public interface AddressService
{
    Address save(AddressDto addressDto,String username);

    Address update(AddressDto addressDto);

    AddressDto findById(long id);


    void deleteAddress(long id);

    void enable(long id);
    void disable(long id);

    Address findDefaultAddress(long customer_id);

    /* order*/

    public Address findByIdOrder(long id);

}
