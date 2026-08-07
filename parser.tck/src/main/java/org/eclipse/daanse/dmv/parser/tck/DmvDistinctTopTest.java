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

import org.eclipse.daanse.dmv.model.api.DmvStatement;
import org.eclipse.daanse.dmv.parser.api.DmvParserException;
import org.eclipse.daanse.dmv.parser.api.DmvParserProvider;
import org.junit.jupiter.api.Test;
import org.osgi.service.component.annotations.RequireServiceComponentRuntime;
import org.osgi.test.common.annotation.InjectService;

/**
 * DISTINCT and TOP, the two modifiers a projection may carry - each alone and
 * both together.
 */
@RequireServiceComponentRuntime
public class DmvDistinctTopTest {

    @Test
    void distinctAlone(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("SELECT DISTINCT [CATALOG_NAME] FROM $SYSTEM.DBSCHEMA_CATALOGS")
                .parseDmvStatement();
        assertThat(clause.distinct()).isTrue();
        assertThat(clause.top()).isEmpty();
        assertThat(clause.columns()).containsExactly("CATALOG_NAME");
    }

    @Test
    void topAlone(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("SELECT TOP 5 * FROM $SYSTEM.MDSCHEMA_CUBES").parseDmvStatement();
        assertThat(clause.distinct()).isFalse();
        assertThat(clause.top()).contains(5);
        assertThat(clause.allColumns()).isTrue();
    }

    @Test
    void distinctTopColumns(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("SELECT DISTINCT TOP 10 [A], [B] FROM $SYSTEM.DBSCHEMA_TABLES")
                .parseDmvStatement();
        assertThat(clause.distinct()).isTrue();
        assertThat(clause.top()).contains(10);
        assertThat(clause.columns()).containsExactly("A", "B");
    }

    @Test
    void keywordsAreCaseInsensitive(@InjectService DmvParserProvider provider) throws DmvParserException {
        DmvStatement clause = provider.newParser("select distinct top 5 * from $system.DBSCHEMA_TABLES")
                .parseDmvStatement();
        assertThat(clause.distinct()).isTrue();
        assertThat(clause.top()).contains(5);
    }
}
