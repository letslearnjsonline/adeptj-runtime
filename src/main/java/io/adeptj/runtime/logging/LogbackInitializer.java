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

package io.adeptj.runtime.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import com.adeptj.runtime.tools.logging.LogbackConfig;
import com.adeptj.runtime.tools.logging.LogbackManager;
import com.typesafe.config.Config;
import io.adeptj.runtime.config.Configs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.qos.logback.classic.Level.toLevel;
import static com.adeptj.runtime.tools.logging.LogbackManager.APPENDER_CONSOLE;
import static com.adeptj.runtime.tools.logging.LogbackManager.APPENDER_FILE;
import static io.adeptj.runtime.common.Times.elapsedMillis;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

/**
 * This Class initializes the Logback logging framework.
 * <p>
 * Usually Logback is initialized via logback.xml logFile on CLASSPATH.
 * But using that approach Logback takes longer to initialize(5+ seconds) which is reduced drastically
 * to ~ 200 milliseconds using programmatic approach.
 * <p>
 * This is huge improvement on total startup time.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class LogbackInitializer {

    private static final String KEY_ROOT_LOG_LEVEL = "root-log-level";

    private static final String KEY_SERVER_LOG_FILE = "server-log-file";

    private static final String KEY_ROLLOVER_SERVER_LOG_FILE = "rollover-server-log-file";

    private static final String KEY_LOG_PATTERN_FILE = "log-pattern-file";

    private static final String KEY_LOG_PATTERN_CONSOLE = "log-pattern-console";

    private static final String KEY_LOG_MAX_HISTORY = "log-max-history";

    private static final String KEY_LOG_MAX_SIZE = "log-max-size";

    private static final String KEY_LOGGERS = "loggers";

    private static final String KEY_LOG_NAME = "name";

    private static final String KEY_LOG_LEVEL = "level";

    private static final String KEY_LOG_ADDITIVITY = "additivity";

    private static final String APPENDER_ASYNC = "ASYNC";

    private static final String INIT_MSG = "Logback initialized in [{}] ms!!";

    private static final String SYS_PROP_LOG_ASYNC = "log.async";

    private static final String KEY_ASYNC_LOG_QUEUE_SIZE = "async-log-queue-size";

    private static final String KEY_ASYNC_LOG_DISCARD_THRESHOLD = "async-log-discardingThreshold";

    private static final String KEY_IMMEDIATE_FLUSH = "file-appender-immediate-flush";

    // Utility methods only.
    private LogbackInitializer() {
    }

    public static void init() {
        long startTime = System.nanoTime();
        Config loggingCfg = Configs.of().logging();
        LogbackConfig logbackConfig = getLogbackConfig(loggingCfg);
        LogbackManager logbackMgr = LogbackManager.getInstance();
        RollingFileAppender<ILoggingEvent> fileAppender = logbackMgr.createRollingFileAppender(logbackConfig);
        ConsoleAppender<ILoggingEvent> consoleAppender = logbackMgr
                .createConsoleAppender(APPENDER_CONSOLE, loggingCfg.getString(KEY_LOG_PATTERN_CONSOLE));
        List<Appender<ILoggingEvent>> appenderList = new ArrayList<>();
        appenderList.add(consoleAppender);
        appenderList.add(fileAppender);
        logbackMgr.getAppenders().addAll(appenderList);
        LoggerContext context = logbackMgr.getLoggerContext();
        initRootLogger(context, consoleAppender, loggingCfg);
        addLoggers(loggingCfg, appenderList);
        addAsyncAppender(loggingCfg, fileAppender);
        context.start();
        context.getLogger(LogbackInitializer.class).info(INIT_MSG, elapsedMillis(startTime));
    }

    private static LogbackConfig getLogbackConfig(Config loggingCfg) {
        return LogbackConfig.builder()
                .appenderName(APPENDER_FILE)
                .logFile(loggingCfg.getString(KEY_SERVER_LOG_FILE))
                .pattern(loggingCfg.getString(KEY_LOG_PATTERN_FILE))
                .immediateFlush(loggingCfg.getBoolean(KEY_IMMEDIATE_FLUSH))
                .logMaxSize(loggingCfg.getString(KEY_LOG_MAX_SIZE))
                .rolloverFile(loggingCfg.getString(KEY_ROLLOVER_SERVER_LOG_FILE))
                .logMaxHistory(loggingCfg.getInt(KEY_LOG_MAX_HISTORY))
                .build();
    }

    private static void initRootLogger(LoggerContext context, Appender<ILoggingEvent> appender, Config loggingCfg) {
        Logger root = context.getLogger(ROOT_LOGGER_NAME);
        root.setLevel(toLevel(loggingCfg.getString(KEY_ROOT_LOG_LEVEL)));
        root.addAppender(appender);
    }

    @SuppressWarnings("unchecked")
    private static void addLoggers(Config loggingCfg, List<Appender<ILoggingEvent>> appenderList) {
        loggingCfg.getObject(KEY_LOGGERS)
                .unwrapped()
                .forEach((logCfgName, logCfgMap) -> {
                    Map<String, Object> configs = (Map<String, Object>) logCfgMap;
                    appenderList.forEach(appender ->
                            LogbackManager.getInstance().addLogger(LogbackConfig.builder()
                                    .logger((String) configs.get(KEY_LOG_NAME))
                                    .level((String) configs.get(KEY_LOG_LEVEL))
                                    .additivity((Boolean) configs.get(KEY_LOG_ADDITIVITY))
                                    .appender(appender)
                                    .build()));
                });
    }

    private static void addAsyncAppender(Config config, RollingFileAppender<ILoggingEvent> fileAppender) {
        if (Boolean.getBoolean(SYS_PROP_LOG_ASYNC)) {
            LogbackManager.getInstance().createAsyncAppender(LogbackConfig.builder()
                    .asyncAppenderName(APPENDER_ASYNC)
                    .asyncLogQueueSize(config.getInt(KEY_ASYNC_LOG_QUEUE_SIZE))
                    .asyncLogDiscardingThreshold(config.getInt(KEY_ASYNC_LOG_DISCARD_THRESHOLD))
                    .asyncAppender(fileAppender)
                    .build());
        }
    }
}