package com.nexus.ecommerce.controller;

import com.nexus.ecommerce.dto.entity.AddressDto;
import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<List<AddressDto>> getAddresses() {
        List<AddressDto> addresses = addressService.getAddressesForActiveUser();
        return Response.<List<AddressDto>>builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .data(addresses)
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response<AddressDto> getAddress(@PathVariable Long id) {
        AddressDto address = addressService.getAddressById(id);
        return Response.<AddressDto>builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .data(address)
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<AddressDto> createAddress(@RequestBody @Valid AddressDto addressDto) {
        AddressDto createdAddress = addressService.createAddress(addressDto);
        return Response.<AddressDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Address created successfully")
                .data(createdAddress)
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response<AddressDto> updateAddress(@PathVariable Long id, @RequestBody @Valid AddressDto addressDto) {
        AddressDto updatedAddress = addressService.updateAddress(id, addressDto);
        return Response.<AddressDto>builder()
                .status(HttpStatus.OK.value())
                .message("Address updated successfully")
                .data(updatedAddress)
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return Response.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Address deleted successfully")
                .build();
    }

    @PatchMapping("/{id}/default")
    @ResponseStatus(HttpStatus.OK)
    public Response<AddressDto> setDefaultAddress(@PathVariable Long id) {
        AddressDto address = addressService.setDefaultAddress(id);
        return Response.<AddressDto>builder()
                .status(HttpStatus.OK.value())
                .message("Default address set successfully")
                .data(address)
                .build();
    }
}
