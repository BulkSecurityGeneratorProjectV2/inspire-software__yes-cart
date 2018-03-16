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

package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.service.domain.TaxService;
import org.yes.cart.utils.HQLUtils;

import java.util.List;

/**
 * User: denispavlov
 * Date: 27/10/2014
 * Time: 19:27
 */
public class TaxServiceImpl  extends BaseGenericServiceImpl<Tax> implements TaxService {

    public TaxServiceImpl(final GenericDAO<Tax, Long> genericDao) {
        super(genericDao);
    }

    /** {@inheritDoc} */
    @Override
    public Tax getById(final long pk) {
        return findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    public List<Tax> getTaxesByShopCode(final String shopCode, final String currency) {
        return getGenericDao().findByNamedQuery("TAX.BY.SHOPCODE.CURRENCY", shopCode, currency);
    }

    /** {@inheritDoc} */
    @Override
    public List<Tax> findByParameters(final String code, final String shopCode, final String currency) {

        return getGenericDao().findByNamedQuery(
                "TAX.BY.CODE.SHOPCODE.CURRENCY",
                shopCode,
                currency,
                HQLUtils.criteriaIlikeAnywhere(code)
        );

    }

    private void regenerateGuid(final Tax entity) {
        final StringBuilder guid = new StringBuilder();
        guid.append(entity.getShopCode()).append('_');
        guid.append(entity.getCurrency()).append('_');
        guid.append(entity.getCode());
        entity.setGuid(guid.toString());
    }

    /** {@inheritDoc} */
    @Override
    public Tax create(final Tax instance) {
        regenerateGuid(instance);
        return super.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    public Tax update(final Tax instance) {
        regenerateGuid(instance);
        return super.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final Tax instance) {
        super.delete(instance);
    }
}
