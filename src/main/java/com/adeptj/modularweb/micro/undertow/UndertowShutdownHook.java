/** 
###############################################################################
#                                                                             # 
#    Copyright 2016, Rakesh Kumar, AdeptJ (http://adeptj.com)                 #
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
package com.adeptj.modularweb.micro.undertow;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adeptj.modularweb.micro.common.Constants;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentManager;

/**
 * ShutdownHook for graceful server shutdown, this first cleans up the deployment and then stops UNDERTOW server.
 * 
 * Rakesh.Kumar, AdeptJ
 */
public class UndertowShutdownHook extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(UndertowShutdownHook.class);

	private Undertow server;

	private DeploymentManager manager;

	public UndertowShutdownHook(Undertow server, DeploymentManager manager) {
		super(Constants.SHUTDOWN_HOOK);
		this.server = server;
		this.manager = manager;
	}

	/**
	 * Handles Graceful server shutdown and resource cleanup.
	 */
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		LOGGER.info("Stopping AdeptJ ModularWeb Micro!!");
		try {
			this.manager.stop();
			this.manager.undeploy();
			this.server.stop();
			LOGGER.info("AdeptJ ModularWeb Micro stopped in [{}] ms!!", (System.currentTimeMillis() - startTime));
		} catch (ServletException ex) {
			LOGGER.error("Exception while stopping Undertow, shutting down JVM!!", ex);
			System.exit(-1);
		}
	}

}