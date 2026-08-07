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

/**
 * Creates parsers for DMV query text. Unlike the MDX parser there are no
 * property words: DMV has no compound identifiers and no member-property
 * navigation, so there is nothing a word set could disambiguate.
 */
public interface DmvParserProvider {

    DmvParser newParser(CharSequence dmv) throws DmvParserException;
}
