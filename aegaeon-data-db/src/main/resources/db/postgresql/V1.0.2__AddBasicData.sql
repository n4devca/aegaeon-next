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

-- Scopes
insert into scope(code, description, is_system) values('openid', 'To request an id_token.', 1);
insert into scope(code, description, is_system) values('profile', 'To request user''s information.', 0);
insert into scope(code, description, is_system) values('offline_access', 'To request a refresh token', 1);
insert into scope(code, description, is_system) values('email', 'To request user''s email information.', 0);
insert into scope(code, description, is_system) values('address', 'To request user''s address information.', 0);
insert into scope(code, description, is_system) values('phone', 'To request user''s phone information.', 0);
insert into scope(code, description, is_system) values('social_media', 'To request user''s social media information.', 0);

-- Claims
insert into claim(code) values('name');
insert into claim(code) values('nickname');
insert into claim(code) values('profile');
insert into claim(code) values('picture');
insert into claim(code) values('website');
insert into claim(code) values('gender');
insert into claim(code) values('birthdate');
insert into claim(code) values('zoneinfo');
insert into claim(code) values('locale');
insert into claim(code) values('email');
insert into claim(code) values('email_verified');
insert into claim(code) values('phone_number');
insert into claim(code) values('phone_number_verified');
insert into claim(code) values('address');
insert into claim(code) values('facebook');
insert into claim(code) values('instagram');
insert into claim(code) values('linkedin');
insert into claim(code) values('youtube');
insert into claim(code) values('twitter');
insert into claim(code) values('whatsapp');
insert into claim(code) values('qq');
insert into claim(code) values('wechat');
insert into claim(code) values('telegram');
insert into claim(code) values('mastodon');

-- Default Scope - Claim linking
insert into scope_claim(scope_id, claim_id)
select s.id, c.id
from scope s, claim c
where s.code = 'profile'
  and c.code in ('name', 'nickname', 'profile', 'picture', 'website', 'gender', 'birthdate', 'zoneinfo', 'locale');

insert into scope_claim(scope_id, claim_id)
select s.id, c.id
from scope s, claim c
where s.code = 'email'
  and c.code in ('email', 'email_verified');

insert into scope_claim(scope_id, claim_id)
select s.id, c.id
from scope s, claim c
where s.code = 'phone'
  and c.code in ('phone_number', 'phone_number_verified');

insert into scope_claim(scope_id, claim_id)
select s.id, c.id
from scope s, claim c
where s.code = 'address'
  and c.code in ('address');

insert into scope_claim(scope_id, claim_id)
select s.id, c.id
from scope s, claim c
where s.code = 'social_media'
  and c.code in ('facebook', 'instagram', 'linkedin', 'youtube', 'twitter', 'whatsapp', 'qq', 'wechat', 'telegram', 'mastodon');




