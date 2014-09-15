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
package com.github.lburgazzoli.karaf.cluster.common;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 *
 */
public class CombinedClassLoaderManager implements ILifecycle, BundleListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombinedClassLoaderManager.class);

    private final Set<String> m_bundleIds;
    private final BundleContext m_bundleContext;
    private final CombinedClassLoader m_classLoader;

    /**
     * c-tor
     *
     * @param bundleContext
     * @param classLoader
     */
    public CombinedClassLoaderManager(BundleContext bundleContext,CombinedClassLoader classLoader) {
        m_bundleContext = bundleContext;
        m_classLoader = classLoader;
        m_bundleIds = Sets.newHashSet();
    }

    /**
     * @param bundleIds
     */
    public void setEligibleBundleIds(List<String> bundleIds) {
        m_bundleIds.clear();
        m_bundleIds.addAll(bundleIds);
    }

    /**
     * @param bundleIds
     */
    public void setEligibleBundleIds(String bundleIds) {
        m_bundleIds.clear();
        for(String bundleId : StringUtils.split(bundleIds,",")) {
            m_bundleIds.add(bundleId);
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     */
    @Override
    public void init() {
        m_bundleContext.addBundleListener(this);

        Bundle[] bundles = m_bundleContext.getBundles();
        for(Bundle bundle : bundles) {
            if(isBundleEligible(bundle)) {
                m_classLoader.addBundle(bundle);
            }
        }
    }

    /**
     *
     */
    @Override
    public void destroy() {
        m_bundleContext.removeBundleListener(this);
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param event
     */
    @Override
    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
            case BundleEvent.STARTED:
                if(isBundleEligible(event.getBundle())) {
                    m_classLoader.addBundle(event.getBundle());
                }
                break;
            case BundleEvent.STOPPED:
                if(isBundleEligible(event.getBundle())) {
                    m_classLoader.removeBundle(event.getBundle());
                }
                break;
        }
    }

    /**
     *
     * @param bundle
     * @return
     */
    public boolean isBundleEligible(Bundle bundle) {
        for(String bundleId : m_bundleIds) {
            if(bundle.getSymbolicName().matches(bundleId)) {
                return true;
            }
        }

        return m_bundleIds.isEmpty() ? true : false;
    }
}
