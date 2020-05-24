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

package org.yes.cart.shoppingcart;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 17/11/2017
 * Time: 08:34
 */
public interface CartValidityModelMessage {

    enum MessageType { INFO, SUCCESS, WARNING, ERROR }

    /**
     * Flag if checkout should be blocked
     *
     * @return block flag
     */
    boolean isCheckoutBlocking();

    /**
     * Type of this message. Used for UI styles.
     *
     * @return message type
     */
    MessageType getMessageType();

    /**
     * Validation notification message key
     *
     * @return message key
     */
    String getMessageKey();

    /**
     * Parameters to message (optional).
     *
     * @return message parameter objects
     */
    Map<String, String> getMessageArgs();

}
