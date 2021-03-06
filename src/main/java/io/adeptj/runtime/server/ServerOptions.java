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

package io.adeptj.runtime.server;

import io.adeptj.runtime.common.Times;
import com.typesafe.config.Config;
import io.undertow.Undertow.Builder;
import io.undertow.UndertowOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.Option;

import java.util.Map;

/**
 * UNDERTOW Server Options.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class ServerOptions {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerOptions.class);

    private static final String SERVER_OPTIONS = "server-options";

    private static final String OPTIONS_TYPE_STRING = "options-type-string";

    private static final String OPTIONS_TYPE_INTEGER = "options-type-integer";

    private static final String OPTIONS_TYPE_LONG = "options-type-long";

    private static final String OPTIONS_TYPE_BOOLEAN = "options-type-boolean";

    /**
     * Utility methods only
     */
    private ServerOptions() {
    }

    /**
     * Configures the server options dynamically.
     *
     * @param builder        Undertow.Builder
     * @param undertowConfig Undertow Typesafe Config
     */
    public static Builder build(Builder builder, Config undertowConfig) {
        long startTime = System.nanoTime();
        Config serverOptionsCfg = undertowConfig.getConfig(SERVER_OPTIONS);
        stringOptions(builder, serverOptionsCfg.getObject(OPTIONS_TYPE_STRING).unwrapped());
        integerOptions(builder, serverOptionsCfg.getObject(OPTIONS_TYPE_INTEGER).unwrapped());
        longOptions(builder, serverOptionsCfg.getObject(OPTIONS_TYPE_LONG).unwrapped());
        booleanOptions(builder, serverOptionsCfg.getObject(OPTIONS_TYPE_BOOLEAN).unwrapped());
        LOGGER.info("Undertow ServerOptions set in [{}] ms!!", Times.elapsedMillis(startTime));
        return builder;
    }

    private static void buildServerOptions(Builder builder, Map<String, ?> options) {
        options.forEach((optKey, optVal) -> builder.setServerOption(toOption(optKey), optVal));
    }

    private static void stringOptions(Builder builder, Map<String, ?> options) {
        buildServerOptions(builder, options);
    }

    private static void integerOptions(Builder builder, Map<String, ?> options) {
        buildServerOptions(builder, options);
    }

    private static void booleanOptions(Builder builder, Map<String, ?> options) {
        buildServerOptions(builder, options);
    }

    private static void longOptions(Builder builder, Map<String, ?> options) {
        options.forEach((optKey, optVal) -> builder.setServerOption(toOption(optKey), Long.valueOf((String) optVal)));
    }

    @SuppressWarnings("unchecked")
    private static <T> Option<T> toOption(String name) {
        Option<T> option = null;
        try {
            option = (Option) UndertowOptions.class.getField(name).get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.error("Exception while accessing field: [{}]", name, ex);
        }
        return option;
    }
}
