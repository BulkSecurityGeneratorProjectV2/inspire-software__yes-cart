/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.bulkexport.csv.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkcommon.model.ExtensibleValueAdapter;
import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.model.ValueAdapter;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: denispavlov
 * Date: 10/11/2017
 * Time: 22:52
 */
public class CsvDateValueAdapterImpl implements ValueAdapter {

    private static final Map<String, DateTimeFormatter> FORMATTERS = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Object fromRaw(final Object rawValue, final String requiredType, final ImpExColumn impExColumn, final ImpExTuple tuple) {

        final String pattern = impExColumn.getContext();

        if (rawValue != null && StringUtils.isNotBlank(pattern)) {

            final DateTimeFormatter dtf = FORMATTERS.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
            return dtf.format((TemporalAccessor) rawValue);
        }

        return rawValue;


    }

    /**
     * Spring IoC.
     *
     * @param extensibleValueAdapter extend
     */
    public void setExtensibleValueAdapter(ExtensibleValueAdapter extensibleValueAdapter) {
        extensibleValueAdapter.extend(this, "DATE");
    }

}
