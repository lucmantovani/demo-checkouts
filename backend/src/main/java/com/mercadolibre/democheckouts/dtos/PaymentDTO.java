package com.mercadolibre.democheckouts.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private String token;
    private String issuerId;
    private String paymentMethodId;
    private Float transactionAmount;
    private Integer installments;
    private String description;
    private PayerDTO payer;
    private Boolean saveCard;
}
