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
 *
 */

package ca.n4dev.aegaeonnext.token.key

import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File

@Component
class KeysProvider(@Value("\${aegaeon.jwks}") pKeyUri: String) {

    val jwkSet: JWKSet = JWKSet.load(File(pKeyUri))

    fun getKeyById(pId: String): JWK? {
        for (j in this.jwkSet.keys) {
            if (j?.keyID == pId) {
                return j
            }
        }
        return null
    }

    fun toPublicJson(): String {
        val jsonObj = this.jwkSet.toJSONObject(true)
        return jsonObj.toJSONString()
    }
}
