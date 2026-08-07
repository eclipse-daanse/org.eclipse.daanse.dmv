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
import java.util.Optional;

/**
 * One DMV query, in parts:
 *
 * <pre>
 * SELECT [DISTINCT] [TOP n] ( * | col [, col]* )
 * FROM ( $SYSTEM.rowset | SYSTEMRESTRICTSCHEMA ( $SYSTEM.rowset [, name = literal]* ) )
 * [WHERE predicate]
 * [ORDER BY col [ASC|DESC] [, col [ASC|DESC]]*]
 * </pre>
 *
 * Column and rowset names are normalized: brackets removed, {@code ]]}
 * unescaped. JOIN, GROUP BY, LIKE, CAST and CONVERT are not part of the
 * language.
 */
public interface DmvStatement {

    boolean distinct();

    Optional<Integer> top();

    /**
     * {@code SELECT *} - true iff {@link #columns()} is empty; the star is the
     * empty projection.
     */
    boolean allColumns();

    /** The projected column names in SELECT order; empty for {@code SELECT *}. */
    List<String> columns();

    /** The rowset name after {@code $SYSTEM.}. */
    String table();

    /**
     * The SYSTEMRESTRICTSCHEMA restrictions; empty for the plain {@code $SYSTEM.}
     * form.
     */
    List<? extends Restriction> restrictions();

    Optional<DmvPredicate> where();

    /** The ORDER BY items in order; empty when absent. */
    List<? extends OrderByItem> orderBy();
}
