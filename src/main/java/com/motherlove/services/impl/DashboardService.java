package com.motherlove.services.impl;

import com.motherlove.models.entities.Order;
import com.motherlove.models.payload.dto.DashboardReportDto;
import com.motherlove.models.payload.dto.MonthlySaleDto;
import com.motherlove.repositories.OrderRepository;
import com.motherlove.services.IDashboardService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService implements IDashboardService {

    private final OrderRepository orderRepository;
    @PersistenceContext
    private final EntityManager entityManager;


    @Override
    public DashboardReportDto generateCommonReport(int year) {
        DashboardReportDto dto = new DashboardReportDto();

        this.populateMonthlyReport(dto, year);
        dto.totalYearlySale = orderRepository.totalYearlySale(year);
        return dto;
    }

    private void populateMonthlyReport(DashboardReportDto dto, int year) {
        try {
            List<Float> monthlyTotalByYear = this.getMonthlyTotalByYear(year);
            for (int i = 0; i < monthlyTotalByYear.size(); i++) {
                dto.monthlySale.getClass().getDeclaredFields()[i].setAccessible(true);
                dto.monthlySale.getClass().getDeclaredFields()[i].setFloat(dto.monthlySale,monthlyTotalByYear.get(i));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private List<Float> getMonthlyTotalByYear(int year) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Float> cq = cb.createQuery(Float.class);
        Root<Order> order = cq.from(Order.class);

        Expression<Integer> yearExpression = cb.function("YEAR", Integer.class, order.get("orderDate"));
        Expression<Integer> monthExpression = cb.function("MONTH", Integer.class, order.get("orderDate"));
        Expression<Float> sumExpression = cb.sum(order.get("totalAmount"));

        cq.select(sumExpression)
                .where(cb.equal(yearExpression, year))
                .groupBy(monthExpression)
                .orderBy(cb.asc(monthExpression));

        return entityManager.createQuery(cq).getResultList();
    }
}
