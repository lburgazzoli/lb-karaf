/*
 * Copyright 2013 lb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lburgazzoli.karaf.cluster.hazelcast;

import com.github.lburgazzoli.karaf.cluster.common.ILifecycle;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 *
 */
public class HazelcastSession implements ILifecycle, IHazelcastSession {

    private HazelcastInstance m_instance;
    private final HazelcastConfig m_config;
    private final String m_name;

    /**
     * c-tor
     *
     * @param name
     * @param config
     */
    public HazelcastSession(String name,HazelcastConfig config) {
        m_name = name;
        m_instance = null;
        m_config = config;
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void init() {
        if(m_instance == null && m_config != null) {
            m_instance = Hazelcast.newHazelcastInstance(m_config);
        }
    }

    @Override
    public void destroy() {
        if(m_instance != null) {
            m_instance.getLifecycleService().shutdown();
            m_instance = null;
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public Config getConfiguration() {
        return m_config;
    }

    @Override
    public HazelcastInstance getInstance() {
        return m_instance;
    }
}
