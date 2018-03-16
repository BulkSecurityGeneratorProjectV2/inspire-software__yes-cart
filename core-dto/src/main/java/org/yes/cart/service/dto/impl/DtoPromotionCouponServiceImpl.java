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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.dto.PromotionCouponDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.PromotionCouponDTOImpl;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.service.dto.DtoPromotionCouponService;
import org.yes.cart.utils.HQLUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 5:49 PM
 */
public class DtoPromotionCouponServiceImpl
    extends AbstractDtoServiceImpl<PromotionCouponDTO, PromotionCouponDTOImpl, PromotionCoupon>
        implements DtoPromotionCouponService {

    /**
     * Construct base dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param promotionCouponGenericService  {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoPromotionCouponServiceImpl(final DtoFactory dtoFactory,
                                         final GenericService<PromotionCoupon> promotionCouponGenericService,
                                         final AdaptersRepository adaptersRepository) {
        super(dtoFactory, promotionCouponGenericService, adaptersRepository);
    }

    /** {@inheritDoc} */
    @Override
    public List<PromotionCouponDTO> getCouponsByPromotionId(Long promotionId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<PromotionCoupon> coupons = ((PromotionCouponService) service).findByPromotionId(promotionId);
        final List<PromotionCouponDTO> dtos = new ArrayList<>();
        fillDTOs(coupons, dtos);
        return dtos;
    }

    /** {@inheritDoc} */
    @Override
    public List<PromotionCouponDTO> findBy(final long promotionId, final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<PromotionCouponDTO> dtos = new ArrayList<>();

        if (promotionId > 0L) {

            List<PromotionCoupon> entities;

            if (StringUtils.isNotBlank(filter)) {

                entities = getService().getGenericDao().findRangeByCriteria(
                        " where e.promotion.promotionId = ?1 and lower(e.code) like ?2 order by e.code",
                        page * pageSize, pageSize,
                        promotionId,
                        HQLUtils.criteriaIlikeAnywhere(filter)
                );

            } else {

                entities = getService().getGenericDao().findRangeByCriteria(
                        " where e.promotion.promotionId = ?1 order by e.code",
                        page * pageSize, pageSize,
                        promotionId
                );

            }

            fillDTOs(entities, dtos);

        }

        return dtos;
    }

    @Override
    public List<PromotionCouponDTO> findBy(final long promotionId, final Instant createdAfter) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<PromotionCouponDTO> dtos = new ArrayList<>();

        if (promotionId > 0L) {

            List<PromotionCoupon> entities;

            if (createdAfter != null) {

                entities = getService().getGenericDao().findByCriteria(
                        " where e.promotion.promotionId = ?1 and e.createdTimestamp >= ?2 order by e.code",
                        promotionId,
                        createdAfter
                );

            } else {

                entities = getService().getGenericDao().findByCriteria(
                        " where e.promotion.promotionId = ?1 order by e.code",
                        promotionId
                );

            }

            fillDTOs(entities, dtos);

        }

        return dtos;
    }

    /** {@inheritDoc} */
    @Override
    public byte[] getCouponsByPromotionIdExport(Long promotionId) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final StringBuilder csv = new StringBuilder();

        final List<PromotionCoupon> coupons = ((PromotionCouponService) service).findByPromotionId(promotionId);
        for (final PromotionCoupon coupon : coupons) {

            csv
                    .append(coupon.getCode()).append(',')
                    .append(coupon.getUsageLimit()).append(',')
                    .append(coupon.getUsageLimitPerCustomer()).append(',')
                    .append(coupon.getUsageCount()).append('\n');

        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);

    }

    /** {@inheritDoc} */
    @Override
    public PromotionCouponDTO create(PromotionCouponDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        if (instance.getCode() != null) {
            // generate one specific
            ((PromotionCouponService) service).create(instance.getPromotionId(), instance.getCode(), instance.getUsageLimit(), instance.getUsageLimitPerCustomer());
        } else {
            // generate many single usage
            ((PromotionCouponService) service).create(instance.getPromotionId(), instance.getUsageLimit(), 1, 0);
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public PromotionCouponDTO update(PromotionCouponDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        // Coupons must not be updated via UI because we may cripple usage integrity. Make users generate new/delete coupons instead.
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public Class<PromotionCouponDTO> getDtoIFace() {
        return PromotionCouponDTO.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<PromotionCouponDTOImpl> getDtoImpl() {
        return PromotionCouponDTOImpl.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<PromotionCoupon> getEntityIFace() {
        return PromotionCoupon.class;
    }
}
