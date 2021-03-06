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

package io.adeptj.runtime.common;

/**
 * ShutdownHook for resource cleanups.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class ShutdownHook extends Thread {

    private Lifecycle lifecycle;

    public ShutdownHook(Lifecycle lifecycle, String threadName) {
        super(threadName);
        this.lifecycle = lifecycle;
    }

    /**
     * Calls the {@link Lifecycle#stop()} in run method.
     */
    @Override
    public void run() {
        this.lifecycle.stop();
    }
}
