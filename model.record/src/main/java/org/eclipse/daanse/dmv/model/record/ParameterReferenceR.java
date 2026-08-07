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

import java.util.Objects;

import org.eclipse.daanse.dmv.model.api.ParameterReference;

public record ParameterReferenceR(String name) implements ParameterReference {

    public ParameterReferenceR {
        Objects.requireNonNull(name, "name must not be null");
    }
}
