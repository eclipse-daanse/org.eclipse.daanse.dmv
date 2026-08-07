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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.eclipse.daanse.dmv.parser.api.DmvParserException;
import org.eclipse.daanse.dmv.parser.api.DmvParserProvider;
import org.junit.jupiter.api.Test;
import org.osgi.service.component.annotations.RequireServiceComponentRuntime;
import org.osgi.test.common.annotation.InjectService;

/**
 * What a parser has to refuse, and that it refuses with a
 * {@link DmvParserException} rather than with anything else.
 */
@RequireServiceComponentRuntime
public class DmvParseErrorTest {

    @Test
    void nullAndEmptyAreRefusedAtCreation(@InjectService DmvParserProvider provider) {
        assertThatThrownBy(() -> provider.newParser(null)).isInstanceOf(DmvParserException.class);
        assertThatThrownBy(() -> provider.newParser("")).isInstanceOf(DmvParserException.class);
    }

    @Test
    void theProjectionIsMandatory(@InjectService DmvParserProvider provider) {
        assertThatThrownBy(() -> provider.newParser("SELECT FROM $SYSTEM.MDSCHEMA_CUBES").parseDmvStatement())
                .isInstanceOf(DmvParserException.class);
    }

    @Test
    void theSystemSchemaIsMandatory(@InjectService DmvParserProvider provider) {
        assertThatThrownBy(() -> provider.newParser("SELECT * FROM MDSCHEMA_CUBES").parseDmvStatement())
                .isInstanceOf(DmvParserException.class);
    }

    @Test
    void whatTheLanguageExcludesIsRefused(@InjectService DmvParserProvider provider) {
        assertThatThrownBy(() -> provider.newParser("SELECT * FROM $SYSTEM.T GROUP BY A").parseDmvStatement())
                .isInstanceOf(DmvParserException.class);
        assertThatThrownBy(() -> provider.newParser("SELECT * FROM $SYSTEM.T WHERE A LIKE 'x'").parseDmvStatement())
                .isInstanceOf(DmvParserException.class);
    }

    @Test
    void theErrorNamesThePlace(@InjectService DmvParserProvider provider) {
        DmvParserException failure = catchThrowableOfType(DmvParserException.class,
                () -> provider.newParser("SELECT * FROM $SYSTEM.").parseDmvStatement());
        assertThat(failure).isNotNull();
        assertThat(failure.line()).isGreaterThanOrEqualTo(1);
    }
}
