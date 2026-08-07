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

import org.eclipse.daanse.dmv.parser.api.DmvParser;
import org.eclipse.daanse.dmv.parser.api.DmvParserException;
import org.eclipse.daanse.dmv.parser.api.DmvParserProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(scope = ServiceScope.SINGLETON, property = {
        "parser.type=ccc" }, configurationPid = CCCDmvParserProvider.PID, service = DmvParserProvider.class)
public class CCCDmvParserProvider implements DmvParserProvider {

    public static final String PID = "daanse.dmv.parser.ccc.CCCDmvParserProvider";

    @Override
    public DmvParser newParser(CharSequence dmv) throws DmvParserException {
        return new DmvParserWrapper(dmv);
    }
}
