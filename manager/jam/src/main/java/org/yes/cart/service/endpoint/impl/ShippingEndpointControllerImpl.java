/*
 * Copyright 2009 Inspire-Software.com
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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.endpoint.ShippingEndpointController;
import org.yes.cart.service.vo.VoShippingService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 29/07/2016
 * Time: 10:48
 */
@Component
public class ShippingEndpointControllerImpl implements ShippingEndpointController {

    private final VoShippingService voShippingService;

    @Autowired
    public ShippingEndpointControllerImpl(final VoShippingService voShippingService) {
        this.voShippingService = voShippingService;
    }

    @Override
    public @ResponseBody
    VoSearchResult<VoCarrierInfo> getFilteredCarriers(@RequestBody final VoSearchContext filter) throws Exception {
        return voShippingService.getFilteredCarriers(filter);
    }

    @Override
    public @ResponseBody
    List<VoShopCarrierAndSla> getShopCarriers(@PathVariable("id") final long shopId) throws Exception {
        return voShippingService.getShopCarriersAndSla(shopId);
    }

    @Override
    public @ResponseBody
    VoCarrier getCarrierById(@PathVariable("id") final long id) throws Exception {
        return voShippingService.getCarrierById(id);
    }

    @Override
    public @ResponseBody
    VoCarrier createCarrier(@RequestBody final VoCarrier vo) throws Exception {
        return voShippingService.createCarrier(vo);
    }

    @Override
    public @ResponseBody
    VoCarrier createShopCarrier(@RequestBody final VoCarrierInfo vo, @PathVariable("id") final long shopId) throws Exception {
        return voShippingService.createShopCarrier(vo, shopId);
    }

    @Override
    public @ResponseBody
    VoCarrier updateCarrier(@RequestBody final VoCarrier vo) throws Exception {
        return voShippingService.updateCarrier(vo);
    }

    @Override
    public @ResponseBody
    List<VoShopCarrierAndSla> updateShopCarriers(@RequestBody final List<VoShopCarrierAndSla> vo) throws Exception {
        return voShippingService.updateShopCarriersAndSla(vo);
    }

    @Override
    public @ResponseBody
    void removeCarrier(@PathVariable("id") final long id) throws Exception {
        voShippingService.removeCarrier(id);
    }

    @Override
    public @ResponseBody
    List<VoCarrierSla> getCarrierSlaAll(@PathVariable("id") final long carrierId) throws Exception {
        return voShippingService.getCarrierSlas(carrierId);
    }

    @Override
    public @ResponseBody
    VoSearchResult<VoCarrierSlaInfo> getFilteredCarrierSlas(@RequestBody final VoSearchContext filter) throws Exception {
        return voShippingService.getFilteredCarrierSlas(filter);
    }

    @Override
    public @ResponseBody
    VoCarrierSla getCarrierSlaById(@PathVariable("id") final long id) throws Exception {
        return voShippingService.getCarrierSlaById(id);
    }

    @Override
    public @ResponseBody
    VoCarrierSla createCarrierSla(@RequestBody final VoCarrierSla vo) throws Exception {
        return voShippingService.createCarrierSla(vo);
    }

    @Override
    public @ResponseBody
    VoCarrierSla updateCarrierSla(@RequestBody final VoCarrierSla vo) throws Exception {
        return voShippingService.updateCarrierSla(vo);
    }

    @Override
    public @ResponseBody
    void removeCarrierSla(@PathVariable("id") final long id) throws Exception {
        voShippingService.removeCarrierSla(id);
    }
}
