package com.mercadolibre.democheckouts.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayerDTO {

    private String email;
    private IdentificationDTO identification;
    private String firstName;
    private String lastName;
    private AddressDTO address;
    private String type;
    private String id;
}
