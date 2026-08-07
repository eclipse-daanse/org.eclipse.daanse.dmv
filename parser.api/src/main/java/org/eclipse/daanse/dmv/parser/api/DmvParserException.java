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

public class DmvParserException extends Exception {

    private static final long serialVersionUID = 1L;
    private int line;
    private int column;

    @SuppressWarnings("unused")
    private DmvParserException() {
        super("");
    }

    public DmvParserException(String message) {
        super(message);
    }

    public DmvParserException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DmvParserException(Throwable throwable) {
        super(throwable);
    }

    public DmvParserException(String message, Throwable throwable, int line, int column) {
        super(message, throwable);
        this.line = line;
        this.column = column;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }
}
