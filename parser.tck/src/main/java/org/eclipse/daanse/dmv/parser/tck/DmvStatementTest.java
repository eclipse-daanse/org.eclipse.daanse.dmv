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

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.daanse.dmv.model.api.ComparisonOperator;
import org.eclipse.daanse.dmv.model.api.DmvStatement;
import org.eclipse.daanse.dmv.model.api.Junction;
import org.eclipse.daanse.dmv.model.record.BareColumnR;
import org.eclipse.daanse.dmv.model.record.ComparisonR;
import org.eclipse.daanse.dmv.model.record.JunctionR;
import org.eclipse.daanse.dmv.model.record.NumericLiteralR;
import org.eclipse.daanse.dmv.model.record.ParameterReferenceR;
import org.eclipse.daanse.dmv.model.record.StringLiteralR;
import org.eclipse.daanse.dmv.parser.api.DmvParserException;
import org.eclipse.daanse.dmv.parser.api.DmvParserProvider;
import org.junit.jupiter.api.Test;
import org.osgi.service.component.annotations.RequireServiceComponentRuntime;
import org.osgi.test.common.annotation.InjectService;

/**
 * The statements clients actually send, ported from the MDX TCK's DMV suite.
 * Names are normalized (brackets stripped), so the expected shapes are one-line
 * record equalities.
 */
@RequireServiceComponentRuntime
public class DmvStatementTest {

    @Test
    void test1(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("SELECT nameColumn from $SYSTEM.tableName").parseDmvStatement();
        assertThat(clause.columns()).containsExactly("nameColumn");
        assertThat(clause.allColumns()).isFalse();
        assertThat(clause.table()).isEqualTo("tableName");
        assertThat(clause.where()).isEmpty();
        assertThat(clause.distinct()).isFalse();
        assertThat(clause.top()).isEmpty();
        assertThat(clause.orderBy()).isEmpty();
        assertThat(clause.restrictions()).isEmpty();
    }

    @Test
    void selectStarMeansEveryColumn(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("SELECT * FROM $SYSTEM.MDSCHEMA_CUBES").parseDmvStatement();
        assertThat(clause.allColumns()).isTrue();
        assertThat(clause.columns()).isEmpty();
        assertThat(clause.table()).isEqualTo("MDSCHEMA_CUBES");
        assertThat(clause.where()).isEmpty();
    }

    @Test
    void selectStarWithWhere(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider
                .newParser("SELECT * FROM $SYSTEM.DBSCHEMA_CATALOGS WHERE CATALOG_NAME = \"FoodMart\"")
                .parseDmvStatement();
        assertThat(clause.columns()).isEmpty();
        assertThat(clause.table()).isEqualTo("DBSCHEMA_CATALOGS");
        assertThat(clause.where())
                .contains(new ComparisonR("CATALOG_NAME", ComparisonOperator.EQ, new StringLiteralR("FoodMart")));
    }

    @Test
    void test2(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("SELECT nameColumn from $SYSTEM.tableName where nameColumn = \"name\"")
                .parseDmvStatement();
        assertThat(clause.columns()).containsExactly("nameColumn");
        assertThat(clause.table()).isEqualTo("tableName");
        assertThat(clause.where())
                .contains(new ComparisonR("nameColumn", ComparisonOperator.EQ, new StringLiteralR("name")));
    }

    @Test
    void test3(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider
                .newParser("select [CUBE_NAME] from $system.MDSCHEMA_CUBES where [CUBE_SOURCE] = 1")
                .parseDmvStatement();
        assertThat(clause.columns()).containsExactly("CUBE_NAME");
        assertThat(clause.table()).isEqualTo("MDSCHEMA_CUBES");
        assertThat(clause.where()).contains(
                new ComparisonR("CUBE_SOURCE", ComparisonOperator.EQ, new NumericLiteralR(new BigDecimal(1))));
    }

    @Test
    void test4(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("select [CUBE_NAME], [BASE_CUBE_NAME], [CUBE_CAPTION]"
                + " from $system.mdschema_cubes where [CUBE_SOURCE] = 1").parseDmvStatement();
        assertThat(clause.columns()).containsExactly("CUBE_NAME", "BASE_CUBE_NAME", "CUBE_CAPTION");
    }

    @Test
    void mdschemaMeasures(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("select [MEASURE_UNIQUE_NAME], [MEASURE_CAPTION], [DATA_TYPE],"
                + " [MEASUREGROUP_NAME], [MEASURE_DISPLAY_FOLDER]" + " from $system.mdschema_measures"
                + " where [CUBE_NAME] = @CubeName and [MEASURE_IS_VISIBLE]").parseDmvStatement();
        assertThat(clause.columns()).hasSize(5);
        assertThat(clause.where()).contains(new JunctionR(Junction.Operator.AND,
                List.of(new ComparisonR("CUBE_NAME", ComparisonOperator.EQ, new ParameterReferenceR("CubeName")),
                        new BareColumnR("MEASURE_IS_VISIBLE"))));
    }

    @Test
    void mdschemaKpis(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("select [KPI_NAME], [KPI_CAPTION], [MEASUREGROUP_NAME],"
                + " [KPI_DISPLAY_FOLDER], [KPI_GOAL], [KPI_STATUS], [KPI_TREND],"
                + " [KPI_VALUE] from $system.mdschema_kpis where [CUBE_NAME] = @CubeName").parseDmvStatement();
        assertThat(clause.columns()).hasSize(8);
        assertThat(clause.where())
                .contains(new ComparisonR("CUBE_NAME", ComparisonOperator.EQ, new ParameterReferenceR("CubeName")));
    }

    @Test
    void mdschemaDimensions(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider
                .newParser("select [DIMENSION_UNIQUE_NAME], [DIMENSION_CAPTION]" + " from $system.mdschema_dimensions"
                        + " where [CUBE_NAME] = @CubeName" + " and [DIMENSION_UNIQUE_NAME] <> '[Measures]'")
                .parseDmvStatement();
        assertThat(clause.columns()).hasSize(2);
        // The single-quoted string keeps its brackets as content.
        assertThat(clause.where()).contains(new JunctionR(Junction.Operator.AND, List.of(
                new ComparisonR("CUBE_NAME", ComparisonOperator.EQ, new ParameterReferenceR("CubeName")),
                new ComparisonR("DIMENSION_UNIQUE_NAME", ComparisonOperator.NE, new StringLiteralR("[Measures]")))));
    }

    @Test
    void mdschemaHierarchies(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("select [DIMENSION_UNIQUE_NAME], [HIERARCHY_UNIQUE_NAME],"
                + " [HIERARCHY_CAPTION], [HIERARCHY_DISPLAY_FOLDER], [HIERARCHY_ORIGIN],"
                + " [HIERARCHY_IS_VISIBLE] from $system.mdschema_hierarchies" + " where [CUBE_NAME] = @CubeName"
                + " and [DIMENSION_UNIQUE_NAME] <> '[Measures]'").parseDmvStatement();
        assertThat(clause.columns()).hasSize(6);
    }

    @Test
    void mdschemaLevels(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("select [DIMENSION_UNIQUE_NAME], [HIERARCHY_UNIQUE_NAME],"
                + " [LEVEL_UNIQUE_NAME], [LEVEL_NUMBER], [LEVEL_CAPTION]" + " from $system.mdschema_levels"
                + " where [CUBE_NAME] = @CubeName and [LEVEL_NAME] <> '(All)'"
                + " and [DIMENSION_UNIQUE_NAME] <> '[Measures]'").parseDmvStatement();
        assertThat(clause.columns()).hasSize(5);
        // One flattened AND over all three, not a nested pair.
        assertThat(clause.where()).isPresent();
        Junction junction = (Junction) clause.where().orElseThrow();
        assertThat(junction.operator()).isEqualTo(Junction.Operator.AND);
        assertThat(junction.predicates()).hasSize(3);
    }

    @Test
    void mdschemaMeasuregroups(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("select [MEASUREGROUP_NAME], [MEASUREGROUP_CAPTION]"
                + " from $system.mdschema_measuregroups where [CUBE_NAME] = @CubeName").parseDmvStatement();
        assertThat(clause.columns()).hasSize(2);
    }
}
