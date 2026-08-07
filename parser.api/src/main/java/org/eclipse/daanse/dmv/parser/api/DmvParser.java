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
package org.eclipse.daanse.dmv.parser.api;

import org.eclipse.daanse.dmv.model.api.DmvPredicate;
import org.eclipse.daanse.dmv.model.api.DmvStatement;

public interface DmvParser {

    DmvStatement parseDmvStatement() throws DmvParserException;

    /**
     * Parses the whole input as a standalone predicate - for the TCK and for tools.
     */
    DmvPredicate parsePredicate() throws DmvParserException;
}
