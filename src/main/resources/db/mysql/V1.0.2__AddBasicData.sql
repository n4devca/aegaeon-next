/**
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

-- Authority
insert into authority(code) values('ROLE_USER');
insert into authority(code) values('ROLE_ADMIN');

-- Grant Type
-- insert into grant_type(code, implementation) values('authorization_code', 'openid');
-- insert into grant_type(code, implementation) values('implicit', 'openid');
-- insert into grant_type(code, implementation) values('refresh_token', 'openid');
-- insert into grant_type(code, implementation) values('client_credentials', 'oauth');

-- Scopes
insert into scope(name, description, issystem) values('openid', 'To request an id_token.', 1);
insert into scope(name, description, issystem) values('profile', 'To request user''s information.', 1);
insert into scope(name, description, issystem) values('offline_access', 'To request a refresh token', 1);
insert into scope(name, description, issystem) values('email', 'To request user''s email information.', 1);
insert into scope(name, description, issystem) values('address', 'To request user''s address information.', 1);
insert into scope(name, description, issystem) values('phone', 'To request user''s phone information.', 1);
insert into scope(name, description, issystem) values('socialmedia', 'To request user''s social media information.', 1);



