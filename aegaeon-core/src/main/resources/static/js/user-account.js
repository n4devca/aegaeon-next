/*
 * *
 *  * Copyright 2017 Remi Guillemette - n4dev.ca
 *  *
 *  * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 *
 */

(function (window, document) {

    document.addEventListener('DOMContentLoaded', function () {

        // image display
        var inputPicture = document.getElementById('user-account-picture');
        inputPicture.addEventListener('blur', function () {
            var url = inputPicture.value;
            console.log(url);

            if (url) {
                document.getElementById('user-account-picture-display').src = url;
            }
        });

        // Validate name
        var inputName = document.getElementById('user-account-name');
        inputName.addEventListener('blur', function () {
            var name = inputName.value;
            if (!name) {
                document.getElementById('user-account-name-error').classList.add('show-error');
            } else {
                document.getElementById('user-account-name-error').classList.remove('show-error');
            }
        });
    });

})(window, document);