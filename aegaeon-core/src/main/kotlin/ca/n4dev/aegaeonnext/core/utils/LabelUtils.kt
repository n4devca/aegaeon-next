/*
 * Copyright 2019 Remi Guillemette - n4dev.ca
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package ca.n4dev.aegaeonnext.core.utils

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.*

/**
 *
 * LabelUtils.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 06 - 2019
 *
 */
@Component
class LabelUtils(val messageSource: MessageSource) {


    /**
     * Get a translated label from the proper bundle.
     *
     * @param pKey The label key
     * @param pLocale the locale
     * @return A String.
     */
    protected fun getLabel(pKey: String, pLocale: Locale): String? {
        return getLabel(pKey, null, pLocale)
    }

    /**
     * Get a translated label from the proper bundle.
     *
     * @param pKey The label key
     * @param pParameters the parameters
     * @param pLocale the locale
     * @return A String.
     */
    protected fun getLabel(pKey: String, pParameters: Array<Any>?, pLocale: Locale): String? {
        return if (pKey.isNotEmpty()) {
            messageSource.getMessage(pKey.toLowerCase(), pParameters, pLocale)
        } else null
    }
}