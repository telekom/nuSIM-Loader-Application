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

package de.scoopgmbh.nusimapp.if3;

import com.typesafe.config.Config;
import de.scoopgmbh.nusimapp.LoaderAppApplicationContext;
import de.scoopgmbh.nusimapp.lifecycle.Managed;

public class IF3AdapterFactory implements Managed {

    public static final String NAME = "if3AdapterFactory";

    private final LoaderAppApplicationContext context;
    private IF3Adapter adapter;

    public IF3AdapterFactory(LoaderAppApplicationContext context) {
        this.context = context;
    }

    public IF3Adapter getAdapter() {
        return adapter;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void start() throws Exception {
        Config if3Config = context.getConfig().getConfig("if3");
        String adapterClassName = if3Config.getString("adapterClass");
        Class<? extends IF3Adapter> adapterClazz = Class.forName(adapterClassName).asSubclass(IF3Adapter.class);
        adapter = adapterClazz.getConstructor(Config.class).newInstance(if3Config);
    }

    @Override
    @SuppressWarnings("RedundantThrows")
    public void stop() throws Exception {
    }
}
