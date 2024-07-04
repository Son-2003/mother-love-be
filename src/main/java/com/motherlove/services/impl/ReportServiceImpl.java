package com.motherlove.services.impl;

import com.motherlove.models.entities.Report;
import com.motherlove.models.entities.User;
import com.motherlove.models.exception.MotherLoveApiException;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.ReportDto;
import com.motherlove.repositories.ReportRepository;
import com.motherlove.repositories.UserRepository;
import com.motherlove.services.IReportService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements IReportService {
    ReportRepository reportRepository;
    UserRepository userRepository;
    ModelMapper mapper;

    public ReportServiceImpl(ReportRepository reportRepository, UserRepository userRepository, ModelMapper mapper) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public ReportDto saveReport(ReportDto reportDto) {
        Report report = mapper.map(reportDto, Report.class);
        User questioner = userRepository.findById(reportDto.getQuestioner().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Questioner", "Id", reportDto.getQuestioner().getUserId()));
        report.setQuestioner(questioner);
        return mapper.map(reportRepository.save(report), ReportDto.class);
    }

    @Override
    public Page<ReportDto> getReports(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Report> reports = reportRepository.findAll(pageable);
        return reports.map(report -> mapper.map(report, ReportDto.class));
    }

    @Override
    public ReportDto getReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "Id", reportId));
        return mapper.map(report, ReportDto.class);
    }

    @Override
    public ReportDto updateReport(ReportDto reportDto) {
        Report report = reportRepository.findById(reportDto.getReportId())
                .orElseThrow(() -> new ResourceNotFoundException("Report", "Id", reportDto.getReportId()));
        User questioner = userRepository.findById(reportDto.getQuestioner().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Questioner", "Id", reportDto.getQuestioner().getUserId()));
        User answerer = userRepository.findById(reportDto.getAnswerer().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Answerer", "Id", reportDto.getAnswerer().getUserId()));
        if (!reportDto.getContent().equalsIgnoreCase(report.getContent())) {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Report content cannot be changed");
        }
        if (reportDto.getResponse().isEmpty()) {
            throw new MotherLoveApiException(HttpStatus.BAD_REQUEST, "Response cannot be empty");
        }
        return mapper.map(reportRepository.save(mapper.map(reportDto, Report.class)), ReportDto.class);
    }

    @Override
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "Id", reportId));
        reportRepository.delete(report);
    }
}
