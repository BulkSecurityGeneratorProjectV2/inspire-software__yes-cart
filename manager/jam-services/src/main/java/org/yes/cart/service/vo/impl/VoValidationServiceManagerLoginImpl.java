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

package org.yes.cart.service.vo.impl;

import org.yes.cart.domain.entity.Manager;
import org.yes.cart.service.domain.ManagerService;
import org.yes.cart.service.vo.VoValidationService;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * User: inspiresoftware
 * Date: 29/08/2016
 * Time: 15:31
 */
public class VoValidationServiceManagerLoginImpl extends AbstractVoValidationServiceSubjectCodeFieldImpl implements VoValidationService {

    private final ManagerService managerService;

    public VoValidationServiceManagerLoginImpl(final ManagerService managerService) {
        super(Pattern.compile("^[a-z0-9]+([\\._a-z0-9\\-@]{7,254})$", Pattern.CASE_INSENSITIVE));
        this.managerService = managerService;
    }

    @Override
    protected Long getDuplicateId(final long currentId, final String valueToCheck, final Map<String, String> context) {
        final Manager manager = this.managerService.findByLogin(valueToCheck);
        return manager != null && manager.getManagerId() != currentId ? manager.getManagerId() : null;
    }
}
