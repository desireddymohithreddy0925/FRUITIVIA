package com.fruitivia.common.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.Collection;

public class SpecificationUtils {

    public static <T> Specification<T> likeIgnoreCase(String attribute, String value) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(value)) {
                return null;
            }
            return cb.like(cb.lower(root.get(attribute)), "%" + value.toLowerCase() + "%");
        };
    }
    
    public static <T> Specification<T> likeIgnoreCaseJoin(String joinTable, String attribute, String value) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(value)) {
                return null;
            }
            return cb.like(cb.lower(root.join(joinTable).get(attribute)), "%" + value.toLowerCase() + "%");
        };
    }

    public static <T> Specification<T> equal(String attribute, Object value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.equal(root.get(attribute), value);
        };
    }

    public static <T> Specification<T> equalJoin(String joinTable, String attribute, Object value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.equal(root.join(joinTable).get(attribute), value);
        };
    }

    public static <T> Specification<T> in(String attribute, Collection<?> values) {
        return (root, query, cb) -> {
            if (values == null || values.isEmpty()) {
                return null;
            }
            return root.get(attribute).in(values);
        };
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> greaterThanOrEqualTo(String attribute, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get(attribute), value);
        };
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> lessThanOrEqualTo(String attribute, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            return cb.lessThanOrEqualTo(root.get(attribute), value);
        };
    }

    public static <T> Specification<T> isTrue(String attribute, Boolean value) {
        return (root, query, cb) -> {
            if (value == null) {
                return null;
            }
            if (value) {
                return cb.isTrue(root.get(attribute));
            } else {
                return cb.isFalse(root.get(attribute));
            }
        };
    }
}
