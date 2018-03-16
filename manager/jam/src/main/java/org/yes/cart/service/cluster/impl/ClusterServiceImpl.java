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

package org.yes.cart.service.cluster.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.cluster.node.RspMessage;
import org.yes.cart.cluster.node.impl.ContextRspMessageImpl;
import org.yes.cart.cluster.service.AlertDirector;
import org.yes.cart.cluster.service.BackdoorService;
import org.yes.cart.cluster.service.CacheDirector;
import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.async.model.AsyncContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.cluster.ClusterService;

import java.util.*;

/**
 * User: denispavlov
 */
public class ClusterServiceImpl implements ClusterService {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterServiceImpl.class);

    private final NodeService nodeService;
    private final BackdoorService localBackdoorService;
    private final CacheDirector localCacheDirector;
    private final AlertDirector localAlertDirector;

    public ClusterServiceImpl(final NodeService nodeService,
                              final BackdoorService localBackdoorService,
                              final CacheDirector localCacheDirector,
                              final AlertDirector localAlertDirector) {
        this.nodeService = nodeService;
        this.localBackdoorService = localBackdoorService;
        this.localCacheDirector = localCacheDirector;
        this.localAlertDirector = localAlertDirector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Node> getClusterInfo(final AsyncContext context) {

        final Message message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                "BackdoorService.ping",
                null,
                context
        );

        // Broadcasting ping message should result in refreshing of the cluster view
        nodeService.broadcast(message);

        return nodeService.getCluster();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void warmUp(final AsyncContext context) {

        final Message message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                "WarmUpService.warmUp",
                null,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Pair<Long, Boolean>> getProductReindexingState(final AsyncContext context) {

        final Map<String, Boolean> indexFinished = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE);
        if (indexFinished == null) {
            throw new IllegalArgumentException("Must have [" + JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE + "] attribute [Map<String, Boolean>] in async context");
        }

        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            final Boolean finished = indexFinished.get(node.getId()) != null && indexFinished.get(node.getId());
            if (!finished) {
                targets.add(node.getId());
            }
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.getProductReindexingState",
                null,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Pair<Long, Boolean>> indexStatus = new HashMap<>();
        for (final Message response : message.getResponses()) {

            final Object[] rsp = (Object[]) response.getPayload();
            indexStatus.put(
                    response.getSource(),
                    new Pair<>(Long.valueOf(ObjectUtils.defaultIfNull(rsp[1], "0").toString()), "DONE".equals(rsp[0])));

        }
        return indexStatus;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Pair<Long, Boolean>> getProductSkuReindexingState(final AsyncContext context) {

        final Map<String, Boolean> indexFinished = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE);
        final Long shopId = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_SHOP);
        if (indexFinished == null) {
            throw new IllegalArgumentException("Must have [" + JobContextKeys.NODE_FULL_PRODUCT_INDEX_STATE + "] attribute [Map<String, Boolean>] in async context");
        }

        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            final Boolean finished = indexFinished.get(node.getId()) != null && indexFinished.get(node.getId());
            if (!finished) {
                targets.add(node.getId());
            }
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.getProductSkuReindexingState",
                shopId,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Pair<Long, Boolean>> indexStatus = new HashMap<>();
        for (final Message response : message.getResponses()) {

            final Object[] rsp = (Object[]) response.getPayload();
            indexStatus.put(response.getSource(),
                    new Pair<>(Long.valueOf(ObjectUtils.defaultIfNull(rsp[1], "0").toString()), "DONE".equals(rsp[0])));

        }
        return indexStatus;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexAllProducts(final AsyncContext context) {

        final Long shopId = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_SHOP);

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                (shopId != null && shopId > 0L) ?
                        "BackdoorService.reindexShopProducts" : "BackdoorService.reindexAllProducts",
                shopId,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexAllProductsSku(final AsyncContext context) {

        final Long shopId = context.getAttribute(JobContextKeys.NODE_FULL_PRODUCT_INDEX_SHOP);

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                (shopId != null && shopId > 0L) ?
                        "BackdoorService.reindexShopProductsSku" : "BackdoorService.reindexAllProductsSku",
                shopId,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProduct(final AsyncContext context, final long productPk) {

        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            targets.add(node.getId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.reindexProduct",
                productPk,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductSku(final AsyncContext context, final long productPk) {

        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            targets.add(node.getId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.reindexProductSku",
                productPk,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductSkuCode(final AsyncContext context, final String productSkuCode) {

        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            targets.add(node.getId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.reindexProductSkuCode",
                productSkuCode,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProducts(final AsyncContext context, final long[] productPks) {

        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            targets.add(node.getId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.reindexProducts",
                productPks,
                context
        );

        nodeService.broadcast(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> sqlQuery(final AsyncContext context, final String query, final String node) {

        if (nodeService.getCurrentNodeId().equals(node)) {
            try {
                return localBackdoorService.sqlQuery(query);
            } catch (Exception e) {
                final String msg = "Cant parse SQL query : " + query + " Error : " + e.getMessage();
                LOG.warn(msg);
                return new ArrayList<>(Collections.singletonList(new Object[]{e.getMessage()}));
            }
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                Collections.singletonList(node),
                "BackdoorService.sqlQuery",
                query,
                context
        );

        nodeService.broadcast(message);

        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            return (List<Object[]>) message.getResponses().get(0).getPayload();

        }

        return Collections.emptyList();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> hsqlQuery(final AsyncContext context, final String query, final String node) {

        if (nodeService.getCurrentNodeId().equals(node)) {
            try {
                return localBackdoorService.hsqlQuery(query);
            } catch (Exception e) {
                final String msg = "Cant parse HQL query : " + query + " Error : " + e.getMessage();
                LOG.warn(msg);
                return new ArrayList<>(Collections.singletonList(new Object[]{e.getMessage()}));
            }
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                Collections.singletonList(node),
                "BackdoorService.hsqlQuery",
                query,
                context
        );

        nodeService.broadcast(message);

        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            return (List<Object[]>) message.getResponses().get(0).getPayload();

        }

        return Collections.emptyList();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> luceneQuery(final AsyncContext context, final String query, final String node) {

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                Collections.singletonList(node),
                "BackdoorService.luceneQuery",
                query,
                context
        );

        nodeService.broadcast(message);

        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            return (List<Object[]>) message.getResponses().get(0).getPayload();

        }

        return Collections.emptyList();

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void reloadConfigurations(final AsyncContext context) {

        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            targets.add(node.getId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "BackdoorService.reloadConfigurations",
                null,
                context
        );

        nodeService.broadcast(message);

        localBackdoorService.reloadConfigurations();

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<CacheInfoDTOImpl>> getCacheInfo(final AsyncContext context)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            targets.add(node.getId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "CacheDirector.getCacheInfo",
                null,
                context
        );

        nodeService.broadcast(message);

        final Map<String, List<CacheInfoDTOImpl>> info = new HashMap<>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                if (response.getPayload() instanceof List) {
                    info.put(response.getSource(), (List<CacheInfoDTOImpl>) response.getPayload());
                }

            }

        }

        final String admin = nodeService.getCurrentNodeId();
        List<CacheInfoDTOImpl> adminRez = localCacheDirector.getCacheInfo();
        for (final CacheInfoDTOImpl cacheInfoDTO : adminRez) {
            cacheInfoDTO.setNodeId(admin);
        }
        info.put(admin, adminRez);

        return info;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Boolean> evictAllCache(final AsyncContext context) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Boolean force = context.getAttribute("force");
        final boolean doForcefully = force != null && force;

        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            targets.add(node.getId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "CacheDirector.evictAllCache",
                doForcefully,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Boolean> evicts = new HashMap<>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                evicts.put(response.getSource(), Boolean.TRUE);

            }

        }

        localCacheDirector.evictAllCache(doForcefully);
        evicts.put(nodeService.getCurrentNodeId(), Boolean.TRUE);

        return evicts;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Boolean> evictCache(final AsyncContext context, final String name) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            targets.add(node.getId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "CacheDirector.evictCache",
                name,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Boolean> evicts = new HashMap<>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                evicts.put(response.getSource(), Boolean.TRUE);

            }

        }

        localCacheDirector.evictCache(name);
        evicts.put(nodeService.getCurrentNodeId(), Boolean.TRUE);

        return evicts;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Boolean> enableStats(final AsyncContext context, final String name) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            targets.add(node.getId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "CacheDirector.enableStats",
                name,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Boolean> stats = new HashMap<>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                stats.put(response.getSource(), Boolean.TRUE);

            }

        }

        localCacheDirector.enableStats(name);
        stats.put(nodeService.getCurrentNodeId(), Boolean.TRUE);

        return stats;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Boolean> disableStats(final AsyncContext context, final String name) throws UnmappedInterfaceException, UnableToCreateInstanceException {


        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            targets.add(node.getId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "CacheDirector.disableStats",
                name,
                context
        );

        nodeService.broadcast(message);

        final Map<String, Boolean> stats = new HashMap<>();
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                stats.put(response.getSource(), Boolean.TRUE);

            }

        }

        localCacheDirector.disableStats(name);
        stats.put(nodeService.getCurrentNodeId(), Boolean.TRUE);

        return stats;

    }

    @Override
    public List<Pair<String, String>> getAlerts(final AsyncContext context) throws UnmappedInterfaceException, UnableToCreateInstanceException {


        final List<Node> cluster = nodeService.getSfNodes();
        final List<String> targets = new ArrayList<>();
        for (final Node node : cluster) {
            targets.add(node.getId());
        }

        final RspMessage message = new ContextRspMessageImpl(
                nodeService.getCurrentNodeId(),
                targets,
                "AlertDirector.getAlerts",
                null,
                context
        );

        nodeService.broadcast(message);

        final List<Pair<String, String>> alerts = new ArrayList<>(150);
        alerts.addAll(localAlertDirector.getAlerts());
        if (CollectionUtils.isNotEmpty(message.getResponses())) {

            for (final Message response : message.getResponses()) {

                alerts.addAll((List) response.getPayload());

            }

        }

        return alerts;

    }
}
