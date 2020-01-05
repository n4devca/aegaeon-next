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


CREATE OR REPLACE FUNCTION model_updated_at() RETURNS TRIGGER AS '
  BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
  END;
' LANGUAGE 'plpgsql';


CREATE TABLE authority (
  id SERIAL,
  code varchar(50) NOT NULL,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  PRIMARY KEY (id)
) 
;

CREATE UNIQUE INDEX authority_code_uq on authority (code);
CREATE TRIGGER authority_updated_at_trg BEFORE UPDATE ON authority FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE scope (
  id SERIAL,
  code varchar(40) NOT NULL,
  description varchar(500),
  is_system boolean NOT NULL DEFAULT FALSE,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  PRIMARY KEY (id)
)
;

CREATE UNIQUE INDEX scope_code_uq on scope (code);
CREATE TRIGGER scope_updated_at_trg BEFORE UPDATE ON scope FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE client (
  id SERIAL,
  public_id varchar(50) NOT NULL,
  secret varchar(1000) NOT NULL,
  name varchar(40) NOT NULL,
  description varchar(500),
  logo_url varchar(300),
  id_token_seconds int NOT NULL DEFAULT 600,
  access_token_seconds int NOT NULL DEFAULT 3600,
  refresh_token_seconds int NOT NULL DEFAULT 604800,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
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
CREATE TRIGGER client_updated_at_trg BEFORE UPDATE ON client FOR EACH ROW EXECUTE PROCEDURE model_updated_at();


CREATE TABLE claim (
    id SERIAL,
    code varchar(100) NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX claim_code_uq on claim (code);

CREATE TABLE scope_claim (
    scope_id int not null,
    claim_id int not null,
    PRIMARY KEY (scope_id, claim_id),
    CONSTRAINT scope_claim_scope_id_fk FOREIGN KEY (scope_id) REFERENCES scope (id) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT scope_claim_claim_id_fk FOREIGN KEY (claim_id) REFERENCES claim (id) ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE INDEX scope_claim_scope_id_idx on scope_claim (scope_id);
CREATE INDEX scope_claim_claim_id_idx on scope_claim (claim_id);


CREATE TABLE users (
  id SERIAL,
  username varchar(100) NOT NULL,
  name varchar(100),
  unique_identifier varchar(128) NOT NULL,
  passwd varchar(250) NOT NULL,
  enabled boolean NOT NULL DEFAULT FALSE,
  picture_url varchar(500),
  last_login_date timestamp with time zone,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  version int NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
)  
;

CREATE UNIQUE INDEX users_username_uq on users (username);
CREATE TRIGGER users_updated_at_trg BEFORE UPDATE ON users FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE user_info (
  id SERIAL,
  user_id int NOT NULL,
  claim_id int,
  custom_name varchar(100),
  value varchar(4000) NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  version int NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT usr_if_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT usr_if_claim_id_fk FOREIGN KEY (claim_id) REFERENCES claim (id) ON DELETE SET NULL ON UPDATE NO ACTION
)  
;

CREATE INDEX usr_if_user_id_idx on user_info (user_id);
CREATE INDEX usr_if_claim_id_idx on user_info (claim_id);
CREATE TRIGGER usr_if_updated_at_trg BEFORE UPDATE ON user_info FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE user_authorization (
  id SERIAL,
  user_id int NOT NULL,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  scopes varchar(200) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uath_clientid_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT uath_userid_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE UNIQUE INDEX uath_uid_cid_uq on user_authorization (user_id, client_id);
CREATE INDEX uath_clientid_idx on user_authorization (client_id);
CREATE INDEX uath_userid_idx on user_authorization (user_id);
CREATE TRIGGER uath_updated_at_trg BEFORE UPDATE ON user_authorization FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE user_authority (
  user_id int NOT NULL,
  authority_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  PRIMARY KEY (user_id,authority_id),
  CONSTRAINT ua_auth_id_auth_fk FOREIGN KEY (authority_id) REFERENCES authority (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT ua_user_id_user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE INDEX ua_auth_id_auth_idx on user_authority (authority_id);
CREATE INDEX ua_user_id_user_idx on user_authority (user_id);
CREATE TRIGGER ua_updated_at_trg BEFORE UPDATE ON user_authority FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE authorization_code (
  id SERIAL,
  code varchar(100) NOT NULL,
  valid_until timestamp with time zone NOT NULL,
  user_id int NOT NULL,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
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
CREATE TRIGGER athc_updated_at_trg BEFORE UPDATE ON authorization_code FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE client_flow (
  id SERIAL,
  client_id int NOT NULL,
  flow varchar(45) NOT NULL,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  PRIMARY KEY (id),
  CONSTRAINT cgt_client_client_id_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
)  
;

CREATE UNIQUE INDEX client_auth_flow_uq on client_auth_flow (client_id, flow);
CREATE INDEX cgt_client_client_id_idx on client_auth_flow (client_id);
CREATE TRIGGER cgt_updated_at_trg BEFORE UPDATE ON client_flow FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE client_contact (
  id SERIAL,
  email varchar(100) NOT NULL,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  PRIMARY KEY (id),
  CONSTRAINT cc_client_id_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
)  
;

CREATE UNIQUE INDEX cc_client_email_uq on client_contact (email, client_id);
CREATE INDEX cc_client_id_idx on client_contact (client_id);
CREATE TRIGGER cc_updated_at_trg BEFORE UPDATE ON client_contact FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE client_redirection (
  id SERIAL,
  url varchar(1000),
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  PRIMARY KEY (id),
  CONSTRAINT clr_client_id_client_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE INDEX clr_client_id_client_idx on client_redirection (client_id);
CREATE TRIGGER clr__updated_at_trg BEFORE UPDATE ON client_redirection FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE client_request_uris (
  id SERIAL,
  uri varchar(1000) NOT NULL,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  PRIMARY KEY (id),
  CONSTRAINT crqu_client_id_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
)  
;

CREATE UNIQUE INDEX crqu_clientiduri_uq on client_request_uris (uri, client_id);
CREATE INDEX crqu_client_id_idx on client_request_uris (client_id);
CREATE TRIGGER crqu_updated_at_trg BEFORE UPDATE ON client_request_uris FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE client_scope (
  id SERIAL,
  client_id int NOT NULL,
  scope_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  PRIMARY KEY (id),
  CONSTRAINT csc_client_id_client_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT csc_sc_id_sc_fk FOREIGN KEY (scope_id) REFERENCES scope (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE INDEX csc_client_id_client_idx on client_scope (client_id);
CREATE INDEX csc_sc_id_sc_idx on client_scope (scope_id);
CREATE TRIGGER client_scope_updated_at_trg BEFORE UPDATE ON client_scope FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE access_token (
  id SERIAL,
  token varchar(4000) NOT NULL,
  valid_until timestamp with time zone NOT NULL,
  user_id int,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  scopes varchar(200),
  PRIMARY KEY (id),
  CONSTRAINT actk_client_id_client_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT actk_user_id_user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE INDEX actk_user_id_user_idx on access_token (user_id);
CREATE INDEX actk_client_id_client_idx on access_token (client_id);
CREATE TRIGGER access_token_updated_at_trg BEFORE UPDATE ON access_token FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE id_token (
  id SERIAL,
  token varchar(4000) NOT NULL,
  valid_until timestamp with time zone NOT NULL,
  user_id int ,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  scopes varchar(200),
  PRIMARY KEY (id),
  CONSTRAINT idtk_client_id_client_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT idtk_user_id_user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE INDEX idtk_user_id_user_idx on id_token (user_id);
CREATE INDEX idtk_client_id_client_idx on id_token (client_id);
CREATE TRIGGER id_token_updated_at_trg BEFORE UPDATE ON id_token FOR EACH ROW EXECUTE PROCEDURE model_updated_at();

CREATE TABLE refresh_token (
  id SERIAL,
  token varchar(255) NOT NULL,
  valid_until timestamp with time zone NOT NULL,
  user_id int NOT NULL,
  client_id int NOT NULL,
  version int NOT NULL DEFAULT 0,
  created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp with time zone,
  scopes varchar(200),
  PRIMARY KEY (id),
  CONSTRAINT rftk_client_id_client_fk FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT rftk_user_id_user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE NO ACTION
)  
;

CREATE UNIQUE INDEX refresh_token_token_uq on refresh_token (token);
CREATE INDEX rftk_user_id_user_idx on refresh_token (user_id);
CREATE INDEX rftk_client_id_client_idx on refresh_token (client_id);
CREATE TRIGGER refresh_token_updated_at_trg BEFORE UPDATE ON refresh_token FOR EACH ROW EXECUTE PROCEDURE model_updated_at();
