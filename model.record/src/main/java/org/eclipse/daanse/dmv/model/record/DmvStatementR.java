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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.daanse.dmv.model.api.DmvPredicate;
import org.eclipse.daanse.dmv.model.api.DmvStatement;
import org.eclipse.daanse.dmv.model.api.OrderByItem;
import org.eclipse.daanse.dmv.model.api.Restriction;

public record DmvStatementR(boolean distinct, Optional<Integer> top, List<String> columns, String table,
        List<Restriction> restrictions, Optional<DmvPredicate> where, List<OrderByItem> orderBy)
        implements DmvStatement {

    public DmvStatementR {
        Objects.requireNonNull(top, "top must not be null");
        Objects.requireNonNull(columns, "columns must not be null");
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(restrictions, "restrictions must not be null");
        Objects.requireNonNull(where, "where must not be null");
        Objects.requireNonNull(orderBy, "orderBy must not be null");
        columns = List.copyOf(columns);
        restrictions = List.copyOf(restrictions);
        orderBy = List.copyOf(orderBy);
    }

    /**
     * Derived, not stored: the grammar makes the projection mandatory, so empty
     * means star.
     */
    @Override
    public boolean allColumns() {
        return columns.isEmpty();
    }
}
