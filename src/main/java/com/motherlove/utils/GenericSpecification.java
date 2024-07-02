package com.motherlove.utils;

import lombok.*;
import org.springframework.data.jpa.domain.Specification;


@Getter
@Setter
@NoArgsConstructor
public class GenericSpecification{
    public static <T> Specification<T> fieldContains(String fieldName, String keyword) {
        return (root, query, builder) -> builder.like(builder.lower(root.get(fieldName)), "%" + keyword.toLowerCase() + "%");
    }

    public static <T> Specification<T> fieldEquals(String fieldName, Object value) {
        return (root, query, builder) -> builder.equal(root.get(fieldName), value);
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
}
