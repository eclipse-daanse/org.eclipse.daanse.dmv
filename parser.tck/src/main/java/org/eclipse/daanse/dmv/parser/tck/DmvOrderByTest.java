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

import java.util.List;

import org.eclipse.daanse.dmv.model.api.DmvStatement;
import org.eclipse.daanse.dmv.model.record.OrderByItemR;
import org.eclipse.daanse.dmv.parser.api.DmvParserException;
import org.eclipse.daanse.dmv.parser.api.DmvParserProvider;
import org.junit.jupiter.api.Test;
import org.osgi.service.component.annotations.RequireServiceComponentRuntime;
import org.osgi.test.common.annotation.InjectService;

/**
 * ORDER BY: one key or several, each with its own direction, and the ascending
 * default when none is spelled out.
 */
@RequireServiceComponentRuntime
public class DmvOrderByTest {

    @Test
    void defaultIsAscending(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("SELECT * FROM $SYSTEM.DBSCHEMA_TABLES ORDER BY TABLE_NAME")
                .parseDmvStatement();
        assertThat(clause.orderBy()).isEqualTo(List.of(new OrderByItemR("TABLE_NAME", false)));
    }

    @Test
    void explicitDirections(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider
                .newParser("SELECT * FROM $SYSTEM.DBSCHEMA_TABLES" + " ORDER BY TABLE_TYPE ASC, [TABLE_NAME] DESC")
                .parseDmvStatement();
        assertThat(clause.orderBy())
                .isEqualTo(List.of(new OrderByItemR("TABLE_TYPE", false), new OrderByItemR("TABLE_NAME", true)));
    }

    @Test
    void theDocumentedExample(@InjectService DmvParserProvider provider) throws DmvParserException {
        // Straight from the Microsoft page: which rowsets can be queried as DMV.
        DmvStatement clause = provider.newParser(
                "SELECT * FROM $System.DBSchema_Tables" + " WHERE TABLE_TYPE = 'SCHEMA' ORDER BY TABLE_NAME ASC")
                .parseDmvStatement();
        assertThat(clause.table()).isEqualTo("DBSchema_Tables");
        assertThat(clause.where()).isPresent();
        assertThat(clause.orderBy()).isEqualTo(List.of(new OrderByItemR("TABLE_NAME", false)));
    }

    @Test
    void trailingSemicolonIsAllowed(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("SELECT * FROM $SYSTEM.DBSCHEMA_TABLES ORDER BY TABLE_NAME DESC;")
                .parseDmvStatement();
        assertThat(clause.orderBy()).isEqualTo(List.of(new OrderByItemR("TABLE_NAME", true)));
    }
}
