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

import org.eclipse.daanse.dmv.model.api.DmvStatement;
import org.eclipse.daanse.dmv.model.record.NumericLiteralR;
import org.eclipse.daanse.dmv.model.record.RestrictionR;
import org.eclipse.daanse.dmv.model.record.StringLiteralR;
import org.eclipse.daanse.dmv.parser.api.DmvParserException;
import org.eclipse.daanse.dmv.parser.api.DmvParserProvider;
import org.junit.jupiter.api.Test;
import org.osgi.service.component.annotations.RequireServiceComponentRuntime;
import org.osgi.test.common.annotation.InjectService;

/**
 * SYSTEMRESTRICTSCHEMA is how a DMV supplies the restrictions a rowset demands
 * - the documented form for rowsets with mandatory restrictions.
 */
@RequireServiceComponentRuntime
public class SystemRestrictSchemaTest {

    @Test
    void theDocumentedExample(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("Select * from SYSTEMRESTRICTSCHEMA ($System.Discover_csdl_metadata,"
                + " [CATALOG_NAME] = 'Adventure Works DW')").parseDmvStatement();
        assertThat(clause.table()).isEqualTo("Discover_csdl_metadata");
        assertThat(clause.restrictions())
                .isEqualTo(List.of(new RestrictionR("CATALOG_NAME", new StringLiteralR("Adventure Works DW"))));
    }

    @Test
    void mixedNamesAndValueKinds(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("SELECT * FROM SYSTEMRESTRICTSCHEMA($SYSTEM.MDSCHEMA_MEMBERS,"
                + " [CATALOG_NAME] = 'FoodMart', CUBE_NAME = 'Sales', TREE_OP = 2)").parseDmvStatement();
        assertThat(clause.table()).isEqualTo("MDSCHEMA_MEMBERS");
        assertThat(clause.restrictions())
                .isEqualTo(List.of(new RestrictionR("CATALOG_NAME", new StringLiteralR("FoodMart")),
                        new RestrictionR("CUBE_NAME", new StringLiteralR("Sales")),
                        new RestrictionR("TREE_OP", new NumericLiteralR(new BigDecimal(2)))));
    }

    @Test
    void combinesWithWhereAndOrderBy(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser(
                "SELECT MEMBER_NAME FROM" + " SYSTEMRESTRICTSCHEMA($SYSTEM.MDSCHEMA_MEMBERS, CUBE_NAME = 'Sales')"
                        + " WHERE MEMBER_TYPE = 1 ORDER BY MEMBER_NAME DESC")
                .parseDmvStatement();
        assertThat(clause.restrictions()).hasSize(1);
        assertThat(clause.where()).isPresent();
        assertThat(clause.orderBy()).hasSize(1);
    }
}
