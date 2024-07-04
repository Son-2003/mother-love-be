package com.motherlove.controllers;

import com.motherlove.models.payload.dto.BlogDto;
import com.motherlove.models.payload.dto.ReportDto;
import com.motherlove.models.payload.dto.VoucherDto;
import com.motherlove.models.payload.responseModel.CustomBlogResponse;
import com.motherlove.services.IReportService;
import com.motherlove.utils.AppConstants;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {
    private IReportService reportService;

    public ReportController(IReportService reportService) {
        this.reportService = reportService;
    }

    @ApiResponse(responseCode = "201", description = "Http Status 201 Created")
    @SecurityRequirement(name = "Bear Authentication")
    @PostMapping
    public ResponseEntity<ReportDto> addReport(@RequestBody @Valid ReportDto reportDto) {
        return ResponseEntity.ok(reportService.saveReport(reportDto));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReport(id));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok("Report deleted successfully");
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')")
    @PutMapping()
    public ResponseEntity<ReportDto> updateReport(@RequestBody @Valid ReportDto reportDto) {
        return ResponseEntity.ok(reportService.updateReport(reportDto));
    }

    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @GetMapping
    public ResponseEntity<Page<ReportDto>> getAllReports(@RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNo,
                                                                @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
                                                                @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY_REPORT_ID) String sortBy,
                                                                @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {
        return ResponseEntity.ok(reportService.getReports(pageNo, pageSize, sortBy, sortDir));
    }
}
