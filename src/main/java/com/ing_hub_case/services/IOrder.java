package com.ing_hub_case.services;

import org.springframework.http.ResponseEntity;

public interface IOrder<T> {
    ResponseEntity<T> doAction(T element);
}
