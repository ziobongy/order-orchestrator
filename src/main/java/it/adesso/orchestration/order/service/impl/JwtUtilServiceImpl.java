package it.adesso.orchestration.order.service.impl;

import it.adesso.orchestration.order.service.JwtUtilService;
import org.springframework.stereotype.Service;

@Service
public class JwtUtilServiceImpl implements JwtUtilService {

    @Override
    public String getUserIdentifier() {
        return "QRT";
    }
}
