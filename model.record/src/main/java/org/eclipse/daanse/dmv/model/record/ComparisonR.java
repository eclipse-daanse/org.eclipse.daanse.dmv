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

import java.util.Objects;

import org.eclipse.daanse.dmv.model.api.Comparison;
import org.eclipse.daanse.dmv.model.api.ComparisonOperator;
import org.eclipse.daanse.dmv.model.api.DmvValue;

public record ComparisonR(String column, ComparisonOperator operator, DmvValue value) implements Comparison {

    public ComparisonR {
        Objects.requireNonNull(column, "column must not be null");
        Objects.requireNonNull(operator, "operator must not be null");
        Objects.requireNonNull(value, "value must not be null");
    }
}
