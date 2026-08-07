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
package org.eclipse.daanse.dmv.model.api;

import java.util.List;

/**
 * AND or OR over two or more predicates; chains are flattened by the parser.
 */
public non-sealed interface Junction extends DmvPredicate {

    enum Operator {
        AND, OR
    }

    Operator operator();

    List<? extends DmvPredicate> predicates();
}
