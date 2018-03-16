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

package org.yes.cart.domain.entity.impl;


import org.yes.cart.domain.entity.Customer;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class AttrValueEntityCustomer implements org.yes.cart.domain.entity.AttrValueCustomer, java.io.Serializable {

    private long attrvalueId;
    private long version;

    private Customer customer;
    private String val;
    private String indexedVal;
    private String displayVal;
    private String attributeCode;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public AttrValueEntityCustomer() {
    }


    @Override
    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String getVal() {
        return this.val;
    }

    @Override
    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public String getIndexedVal() {
        return indexedVal;
    }

    @Override
    public void setIndexedVal(final String indexedVal) {
        this.indexedVal = indexedVal;
    }

    @Override
    public String getDisplayVal() {
        return this.displayVal;
    }

    @Override
    public void setDisplayVal(String displayVal) {
        this.displayVal = displayVal;
    }

    @Override
    public String getAttributeCode() {
        return attributeCode;
    }

    @Override
    public void setAttributeCode(final String attributeCode) {
        this.attributeCode = attributeCode;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public long getAttrvalueId() {
        return this.attrvalueId;
    }

    @Override
    public long getId() {
        return this.attrvalueId;
    }


    @Override
    public void setAttrvalueId(long attrvalueId) {
        this.attrvalueId = attrvalueId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


