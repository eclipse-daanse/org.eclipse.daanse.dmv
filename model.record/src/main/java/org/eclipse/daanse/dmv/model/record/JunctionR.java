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

import org.eclipse.daanse.dmv.model.api.DmvPredicate;
import org.eclipse.daanse.dmv.model.api.Junction;

public record JunctionR(Junction.Operator operator, List<DmvPredicate> predicates) implements Junction {

    public JunctionR {
        Objects.requireNonNull(operator, "operator must not be null");
        predicates = List.copyOf(predicates);
        if (predicates.size() < 2) {
            throw new IllegalArgumentException("a junction joins at least 2 predicates");
        }
    }
}
