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
package com.adeptj.modularweb.micro.osgi;

/**
 * EventDispatcherTrackerSupport.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
public enum EventDispatcherTrackerSupport {

	INSTANCE;

	private volatile EventDispatcherTracker eventDispatcherTracker;
	
	protected EventDispatcherTracker getEventDispatcherTracker() {
		return this.eventDispatcherTracker;
	}
	
	protected void setEventDispatcherTracker(EventDispatcherTracker eventDispatcherTracker) {
		this.eventDispatcherTracker = eventDispatcherTracker;
	}

	protected void closeEventDispatcherTracker() {
		if (this.eventDispatcherTracker != null) {
			this.eventDispatcherTracker.close();
			this.eventDispatcherTracker = null;
		}
	}
}