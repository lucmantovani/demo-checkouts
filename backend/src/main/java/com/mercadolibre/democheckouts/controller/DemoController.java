package com.mercadolibre.democheckouts.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.mercadolibre.democheckouts.dtos.PaymentDTO;
import com.mercadopago.MercadoPago;
import com.mercadopago.core.MPApiResponse;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.exceptions.MPRestException;
import com.mercadopago.resources.Card;
import com.mercadopago.resources.Customer;
import com.mercadopago.resources.Payment;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.Preference.AutoReturn;
import com.mercadopago.resources.datastructures.customer.card.Issuer;
import com.mercadopago.resources.datastructures.payment.Identification;
import com.mercadopago.resources.datastructures.payment.Payer;
import com.mercadopago.resources.datastructures.preference.BackUrls;
import com.mercadopago.resources.datastructures.preference.Item;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DemoController {

  @PostMapping("process_pro_checkout")
  public ResponseEntity<Preference> processProCheckout() throws MPException {
    Preference preference = new Preference();

    preference.setAutoReturn(AutoReturn.all);
    preference.setBackUrls(new BackUrls("http://localhost:5500", "http://localhost:5500", "http://localhost:5500"));

    Item item = new Item();
    item.setTitle("Test product").setQuantity(1).setUnitPrice((float) 75.56);
    preference.appendItem(item);
    preference.save();

    return ResponseEntity.status(201).body(preference);
  }

  @PostMapping("process_card_payment")
  public ResponseEntity<Payment> processCardPayment(@RequestBody PaymentDTO paymentDTO) throws MPException {
    Payment payment = new Payment();
    payment.setTransactionAmount(paymentDTO.getTransactionAmount())
      .setToken(paymentDTO.getToken())
      .setDescription(paymentDTO.getDescription())
      .setInstallments(paymentDTO.getInstallments())
      .setPaymentMethodId(paymentDTO.getPaymentMethodId());

    Identification identification = new Identification();
    identification
      .setType(paymentDTO.getPayer().getIdentification().getType())
      .setNumber(paymentDTO.getPayer().getIdentification().getNumber());

    Payer payer = new Payer();
    payer.setEmail(paymentDTO.getPayer().getEmail())
      .setIdentification(identification);
    payment.setPayer(payer);

    payment.save();

    if (paymentDTO.getSaveCard()) {
      HashMap<String, String> filters = new HashMap<>();
      String email = paymentDTO.getPayer().getEmail();
      filters.put("email", email);

      ArrayList<Customer> customers = Customer.search(filters, true).resources();
      
      Customer customer;
      if (customers != null && customers.size() > 0) {
        customer = customers.get(0);
      } else {
        customer = new Customer();
        customer.setEmail(email);
        customer.save();
      }

      if (!customer.getId().isEmpty()) {
        Issuer issuer = new Issuer();
        issuer.setId(paymentDTO.getIssuerId());
  
        Card card = new Card();
        card.setToken(paymentDTO.getToken());
        card.setCustomerId(customer.getId());
        card.setIssuer(issuer);
        card.setPaymentMethodId(paymentDTO.getPaymentMethodId());
        card.save();
      }

    }

    return ResponseEntity.status(201).body(payment);
  }

  @PostMapping("process_saved_card_payment")
  public ResponseEntity<Payment> processSavedCardPayment(@RequestBody PaymentDTO paymentDTO) throws MPException {
    Payment payment = new Payment();
    payment.setTransactionAmount(paymentDTO.getTransactionAmount())
      .setToken(paymentDTO.getToken())
      .setInstallments(paymentDTO.getInstallments())
      .setPaymentMethodId(paymentDTO.getPaymentMethodId());

    Payer payer = new Payer();
    payer.setType(Payer.type.customer)
      .setId(paymentDTO.getPayer().getId());

    payment.setPayer(payer);

    payment.save();

    return ResponseEntity.status(201).body(payment);
  }

  @GetMapping("customer_cards/{email}")
  public ResponseEntity<ArrayList<Card>> getCustomerCards(@PathVariable String email) throws MPException {
    HashMap<String, String> filters = new HashMap<>();
    filters.put("email", email);

    ArrayList<Card> cards = new ArrayList<>();

    ArrayList<Customer> customers = Customer.search(filters, false).resources();
    if (customers != null && customers.size() > 0) {
      Customer customer = customers.get(0);
      cards = customer.getCards();
    }

    return ResponseEntity.status(200).body(cards);
  }

  @GetMapping("payment_methods")
  public ResponseEntity<JsonElement> getPaymentMethods() throws MPRestException {
    MPApiResponse response = MercadoPago.SDK.Get("/v1/payment_methods");

    return ResponseEntity.status(200).body(response.getJsonElementResponse());
  }

  @PostMapping("process_other_methods_payment")
  public ResponseEntity<Payment> processTicketPayment(@RequestBody PaymentDTO paymentDTO) throws MPException {
    Payment payment = new Payment();

    payment.setTransactionAmount(paymentDTO.getTransactionAmount())
      .setDescription(paymentDTO.getDescription())
      .setPaymentMethodId(paymentDTO.getPaymentMethodId())
      .setPayer(new Payer()
        .setEmail(paymentDTO.getPayer().getEmail())
        .setFirstName(paymentDTO.getPayer().getFirstName())
        .setLastName(paymentDTO.getPayer().getLastName())
        .setIdentification(new Identification()
          .setType(paymentDTO.getPayer().getIdentification().getType())
          .setNumber(paymentDTO.getPayer().getIdentification().getNumber()))
      );

    payment.save();

    return ResponseEntity.status(201).body(payment);
  }

  @PostMapping("process_web_tokenizer_payment")
  public ResponseEntity<Payment> processWebTokenizerPayment(@RequestParam Map<String, Object> body) throws MPException {
    Payment payment = new Payment();

    payment.setTransactionAmount(4000F)
      .setToken(body.get("token").toString())
      .setInstallments(Integer.valueOf(body.get("installments").toString()))
      .setPaymentMethodId(body.get("payment_method_id").toString())
      .setIssuerId(body.get("issuer_id").toString())
      .setPayer(new Payer()
        .setEmail("test_payer_99999999@test.com"));
    payment.save();

    return ResponseEntity.status(201).body(payment);
  }
}
