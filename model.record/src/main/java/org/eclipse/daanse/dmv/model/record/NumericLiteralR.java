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
package org.eclipse.daanse.dmv.model.record;

import java.math.BigDecimal;
import java.util.Objects;

import org.eclipse.daanse.dmv.model.api.NumericLiteral;

public record NumericLiteralR(BigDecimal value) implements NumericLiteral {

    public NumericLiteralR {
        Objects.requireNonNull(value, "value must not be null");
    }
}
