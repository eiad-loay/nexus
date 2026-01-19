package com.nexus.ecommerce.service;

import com.nexus.ecommerce.dto.entity.AddressDto;
import com.nexus.ecommerce.entity.Address;
import com.nexus.ecommerce.entity.User;
import com.nexus.ecommerce.exception.custom.EntityNotFoundException;
import com.nexus.ecommerce.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public List<AddressDto> getAddressesForActiveUser() {
        User user = getActiveUser();
        return addressRepository.findByUser(user).stream()
                .map(this::mapToDto)
                .toList();
    }

    public AddressDto getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        return mapToDto(address);
    }

    @Transactional
    public AddressDto createAddress(AddressDto addressDto) {
        User user = getActiveUser();

        Address address = Address.builder()
                .street(addressDto.getStreet())
                .city(addressDto.getCity())
                .state(addressDto.getState())
                .postalCode(addressDto.getPostalCode())
                .country(addressDto.getCountry())
                .isDefault(addressDto.isDefault())
                .user(user)
                .build();

        // If this is set as default, unset other default addresses
        if (addressDto.isDefault()) {
            unsetDefaultAddresses(user);
        }

        Address savedAddress = addressRepository.save(address);
        return mapToDto(savedAddress);
    }

    @Transactional
    public AddressDto updateAddress(Long id, AddressDto addressDto) {
        User user = getActiveUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        // Verify the address belongs to the active user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Address not found");
        }

        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setPostalCode(addressDto.getPostalCode());
        address.setCountry(addressDto.getCountry());

        // Handle default address logic
        if (addressDto.isDefault() && !address.isDefault()) {
            unsetDefaultAddresses(user);
        }
        address.setDefault(addressDto.isDefault());

        Address updatedAddress = addressRepository.save(address);
        return mapToDto(updatedAddress);
    }

    @Transactional
    public void deleteAddress(Long id) {
        User user = getActiveUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        // Verify the address belongs to the active user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Address not found");
        }

        addressRepository.delete(address);
    }

    @Transactional
    public AddressDto setDefaultAddress(Long id) {
        User user = getActiveUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        // Verify the address belongs to the active user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Address not found");
        }

        unsetDefaultAddresses(user);
        address.setDefault(true);
        Address updatedAddress = addressRepository.save(address);
        return mapToDto(updatedAddress);
    }

    private void unsetDefaultAddresses(User user) {
        addressRepository.findByUserAndIsDefaultTrue(user)
                .ifPresent(defaultAddress -> {
                    defaultAddress.setDefault(false);
                    addressRepository.save(defaultAddress);
                });
    }

    private User getActiveUser() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        return userService.findByEmail(email);
    }

    public AddressDto mapToDto(Address address) {
        return AddressDto.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.isDefault())
                .createdOn(address.getCreatedOn())
                .updatedOn(address.getUpdatedOn())
                .build();
    }

    public Address mapToEntity(AddressDto addressDto, User user) {
        return Address.builder()
                .street(addressDto.getStreet())
                .city(addressDto.getCity())
                .state(addressDto.getState())
                .postalCode(addressDto.getPostalCode())
                .country(addressDto.getCountry())
                .isDefault(addressDto.isDefault())
                .user(user)
                .build();
    }
}
