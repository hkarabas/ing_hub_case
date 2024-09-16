package com.ing_hub_case.services;

import com.ing_hub_case.entities.CustomerAmount;
import com.ing_hub_case.repositories.CustomerAmountRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerAmountService {

    private final CustomerAmountRepository customerAmountRepository;


    public CustomerAmountService(CustomerAmountRepository customerAmountRepository) {
        this.customerAmountRepository = customerAmountRepository;
    }

    public Boolean withDrawMoney(String Iban,Double price) {
      CustomerAmount customerAmount = customerAmountRepository.findByIban(Iban).orElseThrow(()-> new IllegalArgumentException("Iban account not found !!!"));
      if( customerAmount.getAmount()<price )
          return Boolean.FALSE;

      customerAmount.setAmount(customerAmount.getAmount()-price);
      customerAmountRepository.save(customerAmount);
      return Boolean.TRUE;
    }

    public Boolean depositMoney(String Iban,Double price) {
        Optional<CustomerAmount> customerAmountOptional = customerAmountRepository.findByIban(Iban);
        if (customerAmountOptional.isEmpty())
            return Boolean.FALSE;
        CustomerAmount customerAmount = customerAmountOptional.get();
        customerAmount.setAmount(customerAmount.getAmount()+price);
        customerAmountRepository.save(customerAmount);
        return Boolean.TRUE;
    }

}
