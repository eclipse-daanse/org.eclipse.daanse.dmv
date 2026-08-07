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

/**
 * The WHERE surface of a DMV: comparisons, AND/OR, NOT, a bare boolean column.
 * Sealed on purpose - every consumer switch is exhaustive, so an unhandled
 * construct is a compile error, not a silently matching row.
 */
public sealed interface DmvPredicate permits Comparison, Junction, Negation, BareColumn {
}
