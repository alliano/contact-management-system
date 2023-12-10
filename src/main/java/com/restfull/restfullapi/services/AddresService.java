package com.restfull.restfullapi.services;

import java.util.List;

import com.restfull.restfullapi.dtos.AddressResponse;
import com.restfull.restfullapi.dtos.CreateAddressRequest;
import com.restfull.restfullapi.dtos.UpdateAddressRequest;
import com.restfull.restfullapi.entities.User;

public interface AddresService {
    
    public AddressResponse create(User user, CreateAddressRequest request);

    public AddressResponse get(User user, String contactId, String addressId);

    public AddressResponse update(User user, UpdateAddressRequest request);

    public void delete(User user, String contactId, String addressId);

    public List<AddressResponse> list(User user, String contactId);
}
