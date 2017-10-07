/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.runtime.common;

import com.adeptj.runtime.exception.InitializationException;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/**
 * Utilities for SSL/TLS.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class SslUtil {

    private static final String PROTOCOL_TLS = "TLS";

    public static SSLContext getSslContext(KeyStore keyStore, char[] keyPwd) {
        try {
            SSLContext sslContext = SSLContext.getInstance(PROTOCOL_TLS);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, keyPwd);
            sslContext.init(kmf.getKeyManagers(), null, null);
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException | KeyStoreException ex) {
            LoggerFactory.getLogger(SslUtil.class).error("Exception while initializing SSLContext!!", ex);
            throw new InitializationException("Exception while initializing SSLContext!!", ex);
        }
    }
}
