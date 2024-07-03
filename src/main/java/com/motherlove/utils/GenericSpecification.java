package com.motherlove.utils;

import lombok.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;


@Getter
@Setter
@NoArgsConstructor
public class GenericSpecification{
    public static <T> Specification<T> fieldContains(String fieldName, String keyword) {
        return (root, query, builder) -> builder.like(builder.lower(root.get(fieldName)), "%" + keyword.toLowerCase() + "%");
    }

    public static <T, U extends Comparable<? super U>> Specification<T> fieldBetween(String fieldName, U minValue, U maxValue) {
        return (root, query, builder) -> builder.between(root.get(fieldName), minValue, maxValue);
    }

    public static <T, U extends Comparable<? super U>> Specification<T> fieldGreaterThan(String fieldName, U value) {
        return (root, query, builder) -> builder.greaterThan(root.get(fieldName), value);
    }

    public static <T, U extends Comparable<? super U>> Specification<T> fieldLessThan(String fieldName, U value) {
        return (root, query, builder) -> builder.lessThan(root.get(fieldName), value);
    }

    // New method for handling joins
    public static <T> Specification<T> joinFieldContains(String joinField, String fieldName, String keyword) {
        return (root, query, builder) -> builder.like(builder.lower(root.join(joinField).get(fieldName)), "%" + keyword.toLowerCase() + "%");
    }

    public static <T> Specification<T> joinFieldIn(String joinField, String fieldName, Collection<?> values) {
        return (root, query, builder) -> root.join(joinField).get(fieldName).in(values);
    }

    // Method for handling search statuses
    public static <T> Specification<T> fieldIn(String fieldName, Collection<?> values) {
        return (root, query, builder) -> root.get(fieldName).in(values);
    }
}
