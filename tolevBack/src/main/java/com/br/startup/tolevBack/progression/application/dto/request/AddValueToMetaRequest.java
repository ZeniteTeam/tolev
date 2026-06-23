package com.br.startup.tolevBack.progression.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddValueToMetaRequest {
    private Long id;
    private BigDecimal value;

}
