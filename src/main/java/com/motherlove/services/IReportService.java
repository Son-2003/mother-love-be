package com.motherlove.services;

import com.motherlove.models.payload.dto.ReportDto;
import org.springframework.data.domain.Page;

public interface IReportService {
    ReportDto saveReport(ReportDto reportDto);
    Page<ReportDto> getReports(int pageNo, int pageSize, String sortBy, String sortDir);
    ReportDto getReport(Long reportId);
    ReportDto updateReport(ReportDto reportDto);
    void deleteReport(Long reportId);
}
