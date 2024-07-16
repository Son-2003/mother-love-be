package com.motherlove.services;

import com.motherlove.models.payload.dto.DashboardReportDto;

public interface IDashboardService {

    DashboardReportDto generateCommonReport(int year);
}
