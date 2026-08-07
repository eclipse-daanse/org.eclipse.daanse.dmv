/*
* Copyright (c) 2026 Contributors to the Eclipse Foundation.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*   SmartCity Jena - initial
*   Stefan Bischof (bipolis.org) - initial
*/
package org.eclipse.daanse.dmv.model.api;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

/**
 * Evaluates a {@link DmvPredicate} against one row, the row given as a
 * column-name to string-value function. This is the one evaluation every
 * consumer shares - the semantics of a DMV WHERE clause are written here and
 * nowhere else.
 * <p>
 * Equality and inequality are string comparisons, {@code null} equal to
 * {@code null} - as DMV answers have always been matched here. The ordering
 * operators compare numerically when both sides are numbers and
 * lexicographically otherwise; {@code null} is not ordered against anything, so
 * an ordering comparison with a missing value matches nothing. A bare column
 * matches when its value is exactly {@code "true"}.
 */
public final class DmvPredicates {

    private DmvPredicates() {
        // static access only
    }

    /** Without parameters in scope: every {@code @reference} resolves to null. */
    public static boolean matches(DmvPredicate where, Function<String, String> columnValue) {
        return matches(where, columnValue, name -> null);
    }

    public static boolean matches(DmvPredicate where, Function<String, String> columnValue,
            Function<String, String> parameterValue) {
        if (where == null) {
            return true;
        }
        return switch (where) {
        case Junction junction -> junction(junction, columnValue, parameterValue);
        case Negation negation -> !matches(negation.predicate(), columnValue, parameterValue);
        case BareColumn bare -> "true".equals(columnValue.apply(bare.column()));
        case Comparison comparison -> compare(comparison, columnValue, parameterValue);
        };
    }

    private static boolean junction(Junction junction, Function<String, String> columnValue,
            Function<String, String> parameterValue) {
        boolean conjunction = junction.operator() == Junction.Operator.AND;
        for (DmvPredicate predicate : junction.predicates()) {
            boolean matched = matches(predicate, columnValue, parameterValue);
            if (conjunction && !matched) {
                return false;
            }
            if (!conjunction && matched) {
                return true;
            }
        }
        return conjunction;
    }

    private static boolean compare(Comparison comparison, Function<String, String> columnValue,
            Function<String, String> parameterValue) {
        String left = columnValue.apply(comparison.column());
        String right = switch (comparison.value()) {
        case StringLiteral literal -> literal.value();
        case NumericLiteral literal -> literal.value().toPlainString();
        case ParameterReference parameter -> parameterValue.apply(parameter.name());
        };
        return switch (comparison.operator()) {
        case EQ -> Objects.equals(left, right);
        case NE -> !Objects.equals(left, right);
        case LT -> ordered(left, right) == -1;
        case GT -> ordered(left, right) == 1;
        case LE -> ordered(left, right) == -1 || ordered(left, right) == 0;
        case GE -> ordered(left, right) == 1 || ordered(left, right) == 0;
        };
    }

    /**
     * -1, 0, 1 - or {@link Integer#MIN_VALUE} when one side is null and nothing is
     * ordered.
     */
    private static int ordered(String left, String right) {
        if (left == null || right == null) {
            return Integer.MIN_VALUE;
        }
        try {
            return Integer.signum(new BigDecimal(left).compareTo(new BigDecimal(right)));
        } catch (NumberFormatException notNumeric) {
            return Integer.signum(left.compareTo(right));
        }
    }
}
