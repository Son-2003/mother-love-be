package com.motherlove.models.payload.dto;


import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class DashboardReportDto {

    public DashboardReportDto() {
        this.date = LocalDate.now();
        this.monthlySale = new MonthlySaleDto();
    }

    public LocalDate date;
    public BigDecimal totalYearlySale;
    public MonthlySaleDto monthlySale;
}
