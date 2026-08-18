package com.example.demo.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;


public interface ServiceInterface<TRequest, TResponse, TViewResponse, TSpec> {
    TResponse create(TRequest request);

    Page<TViewResponse> getAll(TSpec spec, Pageable pageable);
}
