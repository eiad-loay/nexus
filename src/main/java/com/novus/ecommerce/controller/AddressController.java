package com.novus.ecommerce.controller;

import com.novus.ecommerce.dto.entity.AddressDto;
import com.novus.ecommerce.dto.response.Response;
import com.novus.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<List<AddressDto>> getAddresses() {
        log.info("GET /api/addresses - Fetching addresses for current user");
        List<AddressDto> addresses = addressService.getAddressesForActiveUser();
        log.debug("Found {} addresses", addresses.size());

        return Response.<List<AddressDto>>builder()
                .status(HttpStatus.OK.value())
                .message("Addresses retrieved successfully")
                .data(addresses)
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response<AddressDto> getAddress(@PathVariable Long id) {
        log.info("GET /api/addresses/{} - Fetching address by ID", id);
        AddressDto address = addressService.getAddressById(id);

        return Response.<AddressDto>builder()
                .status(HttpStatus.OK.value())
                .message("Address retrieved successfully")
                .data(address)
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<AddressDto> createAddress(@RequestBody @Valid AddressDto addressDto) {
        log.info("POST /api/addresses - Creating new address");
        AddressDto createdAddress = addressService.createAddress(addressDto);
        log.info("Address created successfully");

        return Response.<AddressDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Address created successfully")
                .data(createdAddress)
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response<AddressDto> updateAddress(@PathVariable Long id, @RequestBody @Valid AddressDto addressDto) {
        log.info("PUT /api/addresses/{} - Updating address", id);
        AddressDto updatedAddress = addressService.updateAddress(id, addressDto);
        log.info("Address {} updated successfully", id);

        return Response.<AddressDto>builder()
                .status(HttpStatus.OK.value())
                .message("Address updated successfully")
                .data(updatedAddress)
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Response<Void> deleteAddress(@PathVariable Long id) {
        log.info("DELETE /api/addresses/{} - Deleting address", id);
        addressService.deleteAddress(id);
        log.info("Address {} deleted successfully", id);

        return Response.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Address deleted successfully")
                .build();
    }

    @PatchMapping("/{id}/default")
    @ResponseStatus(HttpStatus.OK)
    public Response<AddressDto> setDefaultAddress(@PathVariable Long id) {
        log.info("PATCH /api/addresses/{}/default - Setting default address", id);
        AddressDto address = addressService.setDefaultAddress(id);
        log.info("Address {} set as default successfully", id);

        return Response.<AddressDto>builder()
                .status(HttpStatus.OK.value())
                .message("Default address updated successfully")
                .data(address)
                .build();
    }
}
