package com.russ.forkwenhao.jpa;

import static javax.persistence.criteria.Predicate.BooleanOperator.AND;
import static javax.persistence.criteria.Predicate.BooleanOperator.OR;

/**
 * @author russ
 */
public class SpecificationsEX {
    public static <T> PredicateBuilder<T> and() {
        return new PredicateBuilder<>(AND);
    }

    public static <T> PredicateBuilder<T> or() {
        return new PredicateBuilder<>(OR);
    }
}
