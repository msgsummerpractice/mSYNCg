package com.example.demo.filtering.events;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import net.kaczmarzyk.spring.data.jpa.domain.PathSpecification;
import net.kaczmarzyk.spring.data.jpa.utils.Converter;
import net.kaczmarzyk.spring.data.jpa.utils.QueryContext;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;

/**
 * Matches an Instant field by calendar day in Europe/Bucharest time.
 */
public class EqualDayInZone<T> extends PathSpecification<T> {

    private static final long serialVersionUID = 1L;
    private static final ZoneId FILTER_ZONE = ZoneId.of("Europe/Bucharest");

    private final String expectedDay;

    public EqualDayInZone(QueryContext queryContext, String path, String[] httpParamValues, Converter converter) {
        super(queryContext, path);
        if (httpParamValues == null || httpParamValues.length != 1) {
            throw new IllegalArgumentException(
                    "Invalid size of 'httpParamValues' array, expected 1 but was " + Arrays.toString(httpParamValues));
        }
        this.expectedDay = httpParamValues[0];
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        LocalDate localDate = LocalDate.parse(expectedDay);
        Instant dayStartUtc = localDate.atStartOfDay(FILTER_ZONE).toInstant();
        Instant nextDayStartUtc = localDate.plusDays(1).atStartOfDay(FILTER_ZONE).toInstant();

        Expression<Instant> targetExpression = path(root);
        Predicate lowerBoundary = criteriaBuilder.greaterThanOrEqualTo(targetExpression, dayStartUtc);
        Predicate upperBoundary = criteriaBuilder.lessThan(targetExpression, nextDayStartUtc);
        return criteriaBuilder.and(lowerBoundary, upperBoundary);
    }
}