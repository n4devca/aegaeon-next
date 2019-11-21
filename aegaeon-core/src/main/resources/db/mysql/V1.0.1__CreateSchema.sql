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

CREATE TABLE `authority` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `authority_code_uq` (`code`)
) ENGINE=InnoDB
;

CREATE TABLE `scope` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_system` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `scope_name_uq` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `client` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `public_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `secret` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `logo_url` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `provider_name` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_token_seconds` int(11) NOT NULL DEFAULT 600,
  `access_token_seconds` int(11) NOT NULL DEFAULT 3600,
  `refresh_token_seconds` int(11) NOT NULL DEFAULT 604800,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  `application_type` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL DEFAULT 'web',
  `allow_introspect` tinyint(1) not null default 0,
  `client_uri` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `policy_uri` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tos_uri` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jwks_uri` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `jwks` varchar(4000) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `sector_identifier_uri` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `subject_type` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_token_signed_response_alg` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL DEFAULT 'RS256',
  `id_token_encrypted_response_alg` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `id_token_encrypted_response_enc` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL DEFAULT 'A128CBC-HS256',
  `userinfo_signed_response_alg` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `userinfo_encrypted_response_alg` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `userinfo_encrypted_response_enc` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL DEFAULT 'A128CBC-HS256',
  `request_object_signing_alg` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `request_object_encryption_alg` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `request_object_encryption_enc` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL DEFAULT 'A128CBC-HS256',
  `token_endpoint_auth_method` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL DEFAULT 'client_secret_basic',
  `token_endpoint_auth_signing_alg` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `default_max_age` int(11) DEFAULT NULL,
  `require_auth_time` tinyint(1) NOT NULL DEFAULT 0,
  `initiate_login_uri` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cl_publicid_uq` (`public_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unique_identifier` varchar(128) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `passwd` varchar(250) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT 0,
  `picture_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_login_date` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_username_uq` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;


CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `scope_id` int(11),
  `name` varchar(100) NOT NULL,
  `value` varchar(4000) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `usr_if_userid_idx` (`user_id`),
  KEY `usr_if_scopeid_idx` (`scope_id`),
  CONSTRAINT `usr_if_userid_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `usr_if_scopeid_fk` FOREIGN KEY (`scope_id`) REFERENCES `scope` (`id`) ON DELETE SET NULL ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `user_authorization` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  `scopes` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uath_uid_cid_uq` (`user_id`,`client_id`),
  CONSTRAINT `uath_clientid_fk` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `uath_userid_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `user_authority` (
  `user_id` int(11) NOT NULL,
  `authority_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`,`authority_id`),
  KEY `ua_auth_id_auth_fk` (`authority_id`),
  CONSTRAINT `ua_auth_id_auth_fk` FOREIGN KEY (`authority_id`) REFERENCES `authority` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `ua_user_id_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `authorization_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(100) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `valid_until` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,
  `scopes` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `redirect_url` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nonce` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `response_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `athc_code_uq` (`code`),
  KEY `athc_user_id_user_fk` (`user_id`),
  KEY `athc_client_id_client_fk` (`client_id`),
  CONSTRAINT `athc_client_id_client_fk` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `athc_user_id_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `client_flow` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `client_id` int(11) NOT NULL,
  `flow` varchar(45) CHARACTER SET latin1 COLLATE latin1_bin DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `client_auth_flow_uq` (`client_id`,`flow`),
  CONSTRAINT `cgt_client_client_id_fk` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `client_contact` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `client_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cc_client_email_uq_idx` (`email`,`client_id`),
  KEY `cc_client_id_fk` (`client_id`),
  CONSTRAINT `cc_client_id_fk` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `client_redirection` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `client_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `clr_client_id_client_fk` (`client_id`),
  CONSTRAINT `clr_client_id_client_fk` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `client_request_uris` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uri` varchar(1000) NOT NULL,
  `resource_type` varchar(40) NOT NULL,
  `client_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `crqu_client_uri_uq_idx` (`uri`(200),`client_id`),
  KEY `crqu_client_id_fk` (`client_id`),
  CONSTRAINT `crqu_client_id_fk` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `client_scope` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `client_id` int(11) NOT NULL,
  `scope_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `csc_client_id_client_fk` (`client_id`),
  KEY `csc_sc_id_sc_fk` (`scope_id`),
  CONSTRAINT `csc_client_id_client_fk` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `csc_sc_id_sc_fk` FOREIGN KEY (`scope_id`) REFERENCES `scope` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;


CREATE TABLE `access_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `token` varchar(4000) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `valid_until` datetime NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `client_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  `scopes` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `actk_user_id_user_fk` (`user_id`),
  KEY `actk_client_id_client_fk` (`client_id`),
  CONSTRAINT `actk_client_id_client_fk` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `actk_user_id_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `id_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `token` varchar(4000) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `valid_until` datetime NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `client_id` int(11) NOT NULL,
  `scopes` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idtk_user_id_user_fk` (`user_id`),
  KEY `idtk_client_id_client_fk` (`client_id`),
  CONSTRAINT `idtk_client_id_client_fk` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `idtk_user_id_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `refresh_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `token` varchar(255) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
  `valid_until` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,
  `scopes` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `createdat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedat` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `rftk_token_uq_idx` (`token`),
  KEY `rftk_user_id_user_fk` (`user_id`),
  KEY `rftk_client_id_client_fk` (`client_id`),
  CONSTRAINT `rftk_client_id_client_fk` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `rftk_user_id_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

