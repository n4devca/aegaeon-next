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


CREATE OR REPLACE FUNCTION model_updatedat() RETURNS TRIGGER AS '
  BEGIN
    NEW.updatedat = NOW();
    RETURN NEW;
  END;
' LANGUAGE 'plpgsql';


CREATE TABLE user_info_type (
  id SERIAL,
  code varchar(40) NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  PRIMARY KEY (id)
) 
;

CREATE UNIQUE INDEX user_info_type_code_uq on user_info_type (code);
CREATE TRIGGER user_info_type_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE grant_type (
  id SERIAL,
  code varchar(45) NOT NULL,
  implementation varchar(20) NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  PRIMARY KEY (id)
) 
;

CREATE UNIQUE INDEX grant_type_code_uq on grant_type (code);
CREATE TRIGGER grant_type_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE authority (
  id SERIAL,
  code varchar(50) NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  PRIMARY KEY (id)
) 
;

CREATE UNIQUE INDEX authority_code_uq on authority (code);
CREATE TRIGGER authority_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE scope (
  id SERIAL,
  name varchar(40) NOT NULL,
  description varchar(500),
  issystem boolean NOT NULL DEFAULT FALSE,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  defaultvalue boolean NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id)
)
;

CREATE UNIQUE INDEX scope_name_uq on scope (name);
CREATE TRIGGER scope_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE client (
  id SERIAL,
  public_id varchar(50) NOT NULL,
  secret varchar(1000) NOT NULL,
  name varchar(40) NOT NULL,
  description varchar(500),
  logourl varchar(300),
  provider_name varchar(40) NOT NULL,
  id_token_seconds int NOT NULL DEFAULT 600,
  access_token_seconds int NOT NULL DEFAULT 3600,
  refresh_token_seconds int NOT NULL DEFAULT 604800,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  application_type varchar(45) NOT NULL DEFAULT 'web',
  allow_introspect boolean not null default FALSE,
  client_uri varchar(250),
  policy_uri varchar(250),
  tos_uri varchar(250),
  jwks_uri varchar(250),
  jwks varchar(4000),
  sector_identifier_uri varchar(250),
  subject_type varchar(45),
  id_token_signed_response_alg varchar(45) NOT NULL DEFAULT 'RS256',
  id_token_encrypted_response_alg varchar(45),
  id_token_encrypted_response_enc varchar(45) NOT NULL DEFAULT 'A128CBC-HS256',
  userinfo_signed_response_alg varchar(45),
  userinfo_encrypted_response_alg varchar(45),
  userinfo_encrypted_response_enc varchar(45) NOT NULL DEFAULT 'A128CBC-HS256',
  request_object_signing_alg varchar(45),
  request_object_encryption_alg varchar(45),
  request_object_encryption_enc varchar(45) NOT NULL DEFAULT 'A128CBC-HS256',
  token_endpoint_auth_method varchar(45) NOT NULL DEFAULT 'client_secret_basic',
  token_endpoint_auth_signing_alg varchar(45),
  default_max_age int ,
  require_auth_time boolean NOT NULL DEFAULT FALSE,
  initiate_login_uri varchar(250),
  PRIMARY KEY (id)
)  
;

CREATE UNIQUE INDEX client_publicid_uq on client (public_id);
CREATE TRIGGER client_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE users (
  id SERIAL,
  username varchar(100) NOT NULL,
  name varchar(100),
  uniqueIdentifier varchar(128) NOT NULL,
  passwd varchar(250) NOT NULL,
  enabled boolean NOT NULL DEFAULT FALSE,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  picture_url varchar(500),
  PRIMARY KEY (id)
)  
;

CREATE UNIQUE INDEX users_username_uq on users (username);
CREATE TRIGGER users_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE user_info (
  id SERIAL,
  user_id int NOT NULL,
  user_info_type_id int NOT NULL,
  description varchar(250),
  value varchar(1000) NOT NULL,
  note varchar(1000),
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  PRIMARY KEY (id),
  CONSTRAINT usr_if_userid_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT usr_if_userinftypeid_fk FOREIGN KEY (user_info_type_id) REFERENCES user_info_type (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE INDEX usr_if_userid_idx on user_info (user_id);
CREATE INDEX usr_if_userinftypeid_idx on user_info (user_info_type_id);
CREATE TRIGGER usr_if_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE user_authorization (
  id SERIAL,
  user_id int NOT NULL,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  scopes varchar(200) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uath_clientid_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT uath_userid_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE UNIQUE INDEX uath_uid_cid_uq on user_authorization (user_id, client_id);
CREATE INDEX uath_clientid_idx on user_authorization (client_id);
CREATE INDEX uath_userid_idx on user_authorization (user_id);
CREATE TRIGGER uath_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE user_authority (
  user_id int NOT NULL,
  authority_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  PRIMARY KEY (user_id,authority_id),
  CONSTRAINT ua_auth_id_auth_fk FOREIGN KEY (authority_id) REFERENCES authority (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT ua_user_id_user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE INDEX ua_auth_id_auth_idx on user_authority (authority_id);
CREATE INDEX ua_user_id_user_idx on user_authority (user_id);
CREATE TRIGGER ua_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE authorization_code (
  id SERIAL,
  code varchar(100) NOT NULL,
  validuntil timestamp with time zone NOT NULL,
  user_id int NOT NULL,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  scopes varchar(200),
  redirecturl varchar(1000) NOT NULL,
  nonce varchar(40),
  response_type varchar(50) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT athc_client_id_client_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT athc_user_id_user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE UNIQUE INDEX athc_code_uq on authorization_code (code);
CREATE INDEX athc_client_id_client_idx on authorization_code (client_id);
CREATE INDEX athc_user_id_user_idx on authorization_code (user_id);
CREATE TRIGGER athc_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE client_auth_flow (
  id SERIAL,
  client_id int NOT NULL,
  selected boolean NOT NULL DEFAULT FALSE,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  flow varchar(45),
  PRIMARY KEY (id),
  CONSTRAINT cgt_client_client_id_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
)  
;

CREATE UNIQUE INDEX client_auth_flow_uq on client_auth_flow (client_id, flow);
CREATE INDEX cgt_client_client_id_idx on client_auth_flow (client_id);
CREATE TRIGGER cgt_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE client_contact (
  id SERIAL,
  email varchar(100) NOT NULL,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  PRIMARY KEY (id),
  CONSTRAINT cc_client_id_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
)  
;

CREATE UNIQUE INDEX cc_client_email_uq on client_contact (email, client_id);
CREATE INDEX cc_client_id_idx on client_contact (client_id);
CREATE TRIGGER cc_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE client_redirection (
  id SERIAL,
  url varchar(1000),
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  PRIMARY KEY (id),
  CONSTRAINT clr_client_id_client_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE INDEX clr_client_id_client_idx on client_redirection (client_id);
CREATE TRIGGER clr__updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE client_request_uris (
  id SERIAL,
  uri varchar(1000) NOT NULL,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  PRIMARY KEY (id),
  CONSTRAINT crqu_client_id_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
)  
;

CREATE UNIQUE INDEX crqu_clientiduri_uq on client_request_uris (uri, client_id);
CREATE INDEX crqu_client_id_idx on client_request_uris (client_id);
CREATE TRIGGER crqu_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE client_scope (
  id SERIAL,
  client_id int NOT NULL,
  scope_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  PRIMARY KEY (id),
  CONSTRAINT csc_client_id_client_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT csc_sc_id_sc_fk FOREIGN KEY (scope_id) REFERENCES scope (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE INDEX csc_client_id_client_idx on client_scope (client_id);
CREATE INDEX csc_sc_id_sc_idx on client_scope (scope_id);
CREATE TRIGGER client_scope_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE access_token (
  id SERIAL,
  token varchar(4000) NOT NULL,
  validuntil timestamp with time zone NOT NULL,
  user_id int,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  scopes varchar(200),
  PRIMARY KEY (id),
  CONSTRAINT actk_client_id_client_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT actk_user_id_user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE INDEX actk_user_id_user_idx on access_token (user_id);
CREATE INDEX actk_client_id_client_idx on access_token (client_id);
CREATE TRIGGER access_token_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE id_token (
  id SERIAL,
  token varchar(4000) NOT NULL,
  validuntil timestamp with time zone NOT NULL,
  user_id int ,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  scopes varchar(200),
  PRIMARY KEY (id),
  CONSTRAINT idtk_client_id_client_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT idtk_user_id_user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE INDEX idtk_user_id_user_idx on id_token (user_id);
CREATE INDEX idtk_client_id_client_idx on id_token (client_id);
CREATE TRIGGER id_token_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();

CREATE TABLE refresh_token (
  id SERIAL,
  token varchar(255) NOT NULL,
  validuntil timestamp with time zone NOT NULL,
  user_id int NOT NULL,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  createdat timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedat timestamp with time zone,
  scopes varchar(200),
  PRIMARY KEY (id),
  CONSTRAINT rftk_client_id_client_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT rftk_user_id_user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE UNIQUE INDEX refresh_token_token_uq on refresh_token (token);
CREATE INDEX rftk_user_id_user_idx on refresh_token (user_id);
CREATE INDEX rftk_client_id_client_idx on refresh_token (client_id);
CREATE TRIGGER refresh_token_updatedat_trg BEFORE UPDATE ON user_info_type FOR EACH ROW EXECUTE PROCEDURE model_updatedat();
