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

insert into users(username, name, unique_identifier, passwd, enabled)
values('admin@localhost', 'Admin User', uuid(), '{bcrypt}$2a$10$z5Mxvmr82Oaodfgz2EWZ9uNsX/Xtvo7GrrP8LI6Ra1LezlbA5g02K', 1);
select last_insert_id() into @uid;

insert into user_authority(user_id, authority_id)
select @uid, id
from authority;
