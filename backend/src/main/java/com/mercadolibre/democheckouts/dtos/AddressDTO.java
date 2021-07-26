package com.mercadolibre.democheckouts.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

	private String zipCode;
	private String streetName;
	private Integer streetNumber;
	private String neighborhood;
	private String city;
	private String federalUnit;
}
