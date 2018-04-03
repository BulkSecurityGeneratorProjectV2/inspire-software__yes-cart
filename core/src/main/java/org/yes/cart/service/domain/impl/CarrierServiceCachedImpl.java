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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.service.domain.CarrierService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 15:48
 */
public class CarrierServiceCachedImpl implements CarrierService {

    private final CarrierService carrierService;

    public CarrierServiceCachedImpl(final CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Carrier> findCarriersByShopId(final long shopId, final boolean includeDisabled) {
        return carrierService.findCarriersByShopId(shopId, includeDisabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable("carrierService-getCarriersByShopIdAndCurrency")
    public List<Carrier> getCarriersByShopId(final long shopId) {
        return carrierService.getCarriersByShopId(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Carrier> findAll() {
        return carrierService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<Carrier> callback) {
        carrierService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<Carrier> callback) {
        carrierService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Carrier findById(final long pk) {
        return carrierService.findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "carrierService-getCarriersByShopIdAndCurrency"
    }, allEntries = true)
    public Carrier create(final Carrier instance) {
        return carrierService.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "carrierService-getCarriersByShopIdAndCurrency"
    }, allEntries = true)
    public Carrier update(final Carrier instance) {
        return carrierService.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = {
            "carrierService-getCarriersByShopIdAndCurrency"
    }, allEntries = true)
    public void delete(final Carrier instance) {
        carrierService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Carrier> findByCriteria(final String eCriteria, final Object... parameters) {
        return carrierService.findByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return carrierService.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Carrier findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return carrierService.findSingleByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericDAO<Carrier, Long> getGenericDao() {
        return carrierService.getGenericDao();
    }
}
