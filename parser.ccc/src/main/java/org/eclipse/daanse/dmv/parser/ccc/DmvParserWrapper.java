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
package org.eclipse.daanse.dmv.parser.ccc;

import org.eclipse.daanse.dmv.model.api.DmvPredicate;
import org.eclipse.daanse.dmv.model.api.DmvStatement;
import org.eclipse.daanse.dmv.parser.api.DmvParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmvParserWrapper implements org.eclipse.daanse.dmv.parser.api.DmvParser {

    private static final Logger logger = LoggerFactory.getLogger(DmvParserWrapper.class);

    private final DmvParser delegate;

    public DmvParserWrapper(CharSequence dmv) throws DmvParserException {
        if (dmv == null) {
            throw new DmvParserException("statement must not be null");
        }
        if (dmv.length() == 0) {
            throw new DmvParserException("statement must not be empty");
        }
        try {
            delegate = new DmvParser(dmv);
        } catch (Exception e) {
            throw new DmvParserException("Failed to create DmvParser delegate", e);
        }
    }

    @Override
    public DmvStatement parseDmvStatement() throws DmvParserException {
        return parse("DMV statement", delegate::parseDmvStatement);
    }

    @Override
    public DmvPredicate parsePredicate() throws DmvParserException {
        return parse("DMV predicate", delegate::parsePredicateStandalone);
    }

    private <T> T parse(String what, ParseAction<T> action) throws DmvParserException {
        try {
            return action.run();
        } catch (ParseException pe) {
            logger.debug("Failed to parse {}", what, pe);
            throw toDmvParserException(pe);
        } catch (Exception e) {
            logger.debug("Failed to parse {}", what, e);
            throw new DmvParserException(e);
        }
    }

    /** ParseException may carry no token (message-only constructor). */
    private static DmvParserException toDmvParserException(ParseException pe) {
        if (pe.getToken() != null) {
            return new DmvParserException(pe.getMessage(), pe, pe.getToken().getBeginLine(),
                    pe.getToken().getBeginColumn());
        }
        return new DmvParserException(pe.getMessage(), pe);
    }
}
