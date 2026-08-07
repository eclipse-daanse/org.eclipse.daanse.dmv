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
package org.eclipse.daanse.dmv.parser.tck;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.function.Function;

import org.eclipse.daanse.dmv.model.api.DmvPredicate;
import org.eclipse.daanse.dmv.model.api.DmvPredicates;
import org.eclipse.daanse.dmv.parser.api.DmvParserException;
import org.eclipse.daanse.dmv.parser.api.DmvParserProvider;
import org.junit.jupiter.api.Test;
import org.osgi.service.component.annotations.RequireServiceComponentRuntime;
import org.osgi.test.common.annotation.InjectService;

/**
 * The one WHERE evaluation every consumer shares, driven through parsed
 * predicates - grammar and semantics tested together, the way a server uses
 * them.
 */
@RequireServiceComponentRuntime
public class DmvPredicatesTest {

    private static final Function<String, String> ROW = columnOf(Map.of("CATALOG_NAME", "FoodMart", "CUBE_SOURCE", "1",
            "LEVEL_NUMBER", "9", "VISIBLE", "true", "HIDDEN", "false"));

    private static Function<String, String> columnOf(Map<String, String> values) {
        return values::get;
    }

    private static boolean matches(DmvParserProvider provider, String predicate) throws DmvParserException {
        DmvPredicate parsed = provider.newParser(predicate).parsePredicate();
        return DmvPredicates.matches(parsed, ROW);
    }

    @Test
    void equalityIsStringEquality(@InjectService DmvParserProvider provider) throws DmvParserException {
        assertThat(matches(provider, "CATALOG_NAME = 'FoodMart'")).isTrue();
        assertThat(matches(provider, "CATALOG_NAME = 'foodmart'")).isFalse();
        assertThat(matches(provider, "CATALOG_NAME <> 'Adventure'")).isTrue();
    }

    @Test
    void aMissingColumnEqualsNothing(@InjectService DmvParserProvider provider) throws DmvParserException {
        assertThat(matches(provider, "NO_SUCH_COLUMN = 'x'")).isFalse();
        assertThat(matches(provider, "NO_SUCH_COLUMN <> 'x'")).isTrue();
    }

    @Test
    void junctionsShortCircuit(@InjectService DmvParserProvider provider) throws DmvParserException {
        assertThat(matches(provider, "CATALOG_NAME = 'FoodMart' AND CUBE_SOURCE = 1")).isTrue();
        assertThat(matches(provider, "CATALOG_NAME = 'x' AND CUBE_SOURCE = 1")).isFalse();
        assertThat(matches(provider, "CATALOG_NAME = 'x' OR CUBE_SOURCE = 1")).isTrue();
        assertThat(matches(provider, "NOT CATALOG_NAME = 'x'")).isTrue();
    }

    @Test
    void aBareColumnMatchesOnTrue(@InjectService DmvParserProvider provider) throws DmvParserException {
        assertThat(matches(provider, "VISIBLE")).isTrue();
        assertThat(matches(provider, "HIDDEN")).isFalse();
        assertThat(matches(provider, "NOT HIDDEN")).isTrue();
    }

    @Test
    void orderingIsNumericWhenBothSidesAreNumbers(@InjectService DmvParserProvider provider) throws DmvParserException {
        // "9" < "10" numerically - lexicographically it would be the other way around.
        assertThat(matches(provider, "LEVEL_NUMBER < 10")).isTrue();
        assertThat(matches(provider, "LEVEL_NUMBER >= 9")).isTrue();
        assertThat(matches(provider, "LEVEL_NUMBER > 9")).isFalse();
        // Not numeric on the left: lexicographic.
        assertThat(matches(provider, "CATALOG_NAME < 'Z'")).isTrue();
    }

    @Test
    void nullIsNotOrdered(@InjectService DmvParserProvider provider) throws DmvParserException {
        assertThat(matches(provider, "NO_SUCH_COLUMN < 10")).isFalse();
        assertThat(matches(provider, "NO_SUCH_COLUMN >= 10")).isFalse();
    }

    @Test
    void parametersResolveLazily(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvPredicate parsed = provider.newParser("CATALOG_NAME = @CatalogName").parsePredicate();
        Function<String, String> parameters = Map.of("CatalogName", "FoodMart")::get;
        assertThat(DmvPredicates.matches(parsed, ROW, parameters)).isTrue();
        // A missing parameter resolves to null - and null equals nothing that is there.
        assertThat(DmvPredicates.matches(parsed, ROW)).isFalse();
    }

    @Test
    void parenthesesGroup(@InjectService DmvParserProvider provider) throws DmvParserException {
        assertThat(matches(provider, "(CATALOG_NAME = 'x' OR CUBE_SOURCE = 1) AND VISIBLE")).isTrue();
    }
}
