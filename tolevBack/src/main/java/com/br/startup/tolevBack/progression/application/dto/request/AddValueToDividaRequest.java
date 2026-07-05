package com.br.startup.tolevBack.progression.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddValueToDividaRequest {
    private Long id;
    private BigDecimal value;
}
