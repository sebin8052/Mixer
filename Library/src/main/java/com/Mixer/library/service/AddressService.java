package com.Mixer.library.service;

import com.Mixer.library.dto.AddressDto;
import com.Mixer.library.model.Address;

public interface AddressService
{
    Address save(AddressDto addressDto,String username);

    Address update(AddressDto addressDto);

    AddressDto findById(long id);


    void deleteAddress(long id);

    void enable(long id);
    void disable(long id);

    Address findDefaultAddress(long customer_id);



    public Address findByIdOrder(long id);

}
