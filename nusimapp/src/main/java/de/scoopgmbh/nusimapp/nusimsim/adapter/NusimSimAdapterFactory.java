/*
 * nusim-loader
 *
 * (c) 2020 Deutsche Telekom AG.
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package de.scoopgmbh.nusimapp.nusimsim.adapter;

import com.typesafe.config.Config;
import de.scoopgmbh.nusimapp.LoaderAppApplicationContext;
import de.scoopgmbh.nusimapp.lifecycle.Managed;

public class NusimSimAdapterFactory implements Managed {

    public static final String NAME = "nusimSimAdapterFactory";

    private final LoaderAppApplicationContext context;
    private NusimSimAdapter adapter;

    public NusimSimAdapterFactory(LoaderAppApplicationContext context) {
        this.context = context;
    }

    public NusimSimAdapter getAdapter() {
        return adapter;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void start() throws Exception {
        Config adapterConfig = context.getConfig().getConfig("nusimSimAdapter");
        String adapterClassName = adapterConfig.getString("adapterClass");
        Class<? extends NusimSimAdapter> adapterClazz = Class.forName(adapterClassName).asSubclass(NusimSimAdapter.class);
        adapter = adapterClazz.getConstructor(Config.class).newInstance(adapterConfig);
    }

    @Override
    public void stop() throws Exception {
        if (adapter != null) {
            adapter.stop();
        }
    }
}
