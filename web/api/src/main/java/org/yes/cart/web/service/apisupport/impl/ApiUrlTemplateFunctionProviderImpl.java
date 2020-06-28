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

package org.yes.cart.web.service.apisupport.impl;

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.theme.templates.TemplateProcessor;
import org.yes.cart.web.application.ApplicationDirector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 25/08/2014
 * Time: 00:54
 */
public class ApiUrlTemplateFunctionProviderImpl implements TemplateProcessor.FunctionProvider {

    private final String contextPath;
    private final String[] paramName;

    public ApiUrlTemplateFunctionProviderImpl() {
        this.contextPath = "";
        this.paramName = null;
    }

    public ApiUrlTemplateFunctionProviderImpl(final String contextPath, final String ... paramName) {
        if (contextPath.endsWith("/")) {
            this.contextPath = contextPath.substring(0, contextPath.length() - 1);
        } else {
            this.contextPath = contextPath;
        }
        this.paramName = paramName;
    }

    @Override
    public Object doAction(final Object... params) {

        final Shop shop = ApplicationDirector.getCurrentShop();
        final String defaultUrl = shop.getDefaultShopSecureUrl();
        final StringBuilder url = new StringBuilder(defaultUrl);
        if (defaultUrl.endsWith("/")) {
            url.append(contextPath.substring(1));
        } else {
            url.append(contextPath);
        }
        if (params != null && this.paramName != null && params.length >= 1) {

            final List sections;
            if (params[0] instanceof Collection) {
                sections = new ArrayList((Collection) params[0]);
            } else {
                sections = Collections.singletonList(String.valueOf(params[0]));
            }
            if (sections.size() >= this.paramName.length) {
                for (int i = 0; i < this.paramName.length; i++) {
                    url.append('/').append(this.paramName[i]).append('/').append(sections.get(i));
                }
            }

        }
        return url.toString();
    }



}
