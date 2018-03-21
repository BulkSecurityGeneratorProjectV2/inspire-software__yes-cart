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

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.GenericFTSCapableDAO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.dto.impl.ProductSkuSearchResultDTOImpl;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.entity.AdapterUtils;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.service.domain.ProductSkuService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductSkuServiceImpl extends BaseGenericServiceImpl<ProductSku> implements ProductSkuService {


    private final GenericFTSCapableDAO<Product, Long, Object> productDao;
    private final GenericDAO<SkuPrice, Long> skuPriceDao;


    /**
     * Construct  service.
     * @param productSkuDao sku dao
     * @param productDao    product dao
     */
    public ProductSkuServiceImpl(final GenericFTSCapableDAO<ProductSku, Long, Object> productSkuDao,
                                 final GenericFTSCapableDAO<Product, Long, Object> productDao,
                                 final GenericDAO<SkuPrice, Long> skuPriceDao) {
        super(productSkuDao);
        this.productDao = productDao;
        this.skuPriceDao = skuPriceDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ProductSku> getAllProductSkus(final long productId) {
        final Product product = productDao.findById(productId);
        return product.getSku();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSku findProductSkuBySkuCode(final String skuCode) {
        return getGenericDao().findSingleByCriteria(
                " where e.code = ?1", skuCode
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSku getProductSkuBySkuCode(final String skuCode) {
        return findProductSkuBySkuCode(skuCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductSkuSearchResultDTO> getProductSkuSearchResultDTOByQuery(final NavigationContext context) {

        final Pair<List<Object[]>, Integer> searchRez = ((GenericFTSCapableDAO) getGenericDao()).fullTextSearch(
                context.getProductSkuQuery(),
                0,
                -1, /* no limit */
                null,
                false,
                AdapterUtils.FIELD_PK,
                AdapterUtils.FIELD_CLASS,
                AdapterUtils.FIELD_OBJECT
        );

        final List<ProductSkuSearchResultDTO> rez = new ArrayList<>(searchRez.getFirst().size());
        for (Object[] obj : searchRez.getFirst()) {
            final ProductSkuSearchResultDTO dto = AdapterUtils.readObjectFieldValue((String) obj[2], ProductSkuSearchResultDTOImpl.class);
            rez.add(dto);
        }

        return rez;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, SkuPrice>> getAllPrices(final long productId) {
        final List<SkuPrice> prices = skuPriceDao.findByNamedQuery("SKUPRICE.BY.PRODUCT", productId);
        if (CollectionUtils.isNotEmpty(prices)) {
            final List<Pair<String, SkuPrice>> rez = new ArrayList<>(prices.size());
            for (final SkuPrice price : prices) {
                rez.add(new Pair<>(price.getSkuCode(), price));
            }
            return rez;
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, SkuWarehouse>> getAllInventory(final long productId) {
        final List<Object[]> inventory = (List) getGenericDao().findQueryObjectsByNamedQuery("SKUWAREHOUSE.BY.PRODUCT", productId);
        if (CollectionUtils.isNotEmpty(inventory)) {
            final List<Pair<String, SkuWarehouse>> rez = new ArrayList<>(inventory.size());
            for (final Object[] price : inventory) {
                rez.add(new Pair<>((String) price[1], (SkuWarehouse) price[0]));
            }
            return rez;
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllPrices(final long productId) {
        final List<ProductSku> skus = getGenericDao().findByCriteria(" where e.product.productId = ?1" , productId);
        for (ProductSku sku : skus) {
            removeAllPrices( sku);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllPrices(final ProductSku sku) {
         getGenericDao().executeUpdate("REMOVE.ALL.SKUPRICE.BY.SKUCODE", sku.getCode());

     }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllInventory(final long productId) {
        final List<ProductSku> skus = getGenericDao().findByCriteria(" where e.product.productId = ?1" , productId);
        for (ProductSku sku : skus) {
            removeAllInventory(sku);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllInventory(final ProductSku sku) {
            getGenericDao().executeUpdate("REMOVE.ALL.SKU.INVENTORY", sku.getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllWishLists(final ProductSku sku) {
        getGenericDao().executeUpdate("REMOVE.ALL.WISHLIST.BY.SKUID", sku.getSkuId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllEnsembleOptions(final ProductSku sku) {
        getGenericDao().executeUpdate("REMOVE.ALL.ENSEMBLEOPTS.BY.SKUID", sku.getSkuId());
    }
}
