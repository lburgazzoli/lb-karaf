/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lburgazzoli.karaf.common.cmd;

import com.google.common.collect.Lists;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public abstract class AbstractCommand extends OsgiCommandSupport {
    private final List<ServiceReference<?>> m_serviceReferences;

    /**
     * c-tor
     */
    public AbstractCommand() {
        m_serviceReferences = Lists.newLinkedList();
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    protected Object doExecute() throws Exception {
        try {
            execute();
        } catch (Exception e) {
            throw e;
        } finally {
            for(ServiceReference<?> sr : m_serviceReferences) {
                getBundleContext().ungetService(sr);
            }
        }

        return null;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @throws Exception
     */
    protected abstract void execute() throws Exception;

    /**
     *
     * @param type
     * @return
     * @throws Exception
     */
    protected Collection<ServiceReference<?>> getServiceReferences(Class<?> type) throws Exception {
        return getServiceReferences(type,null);
    }

    /**
     *
     * @param type
     * @return
     * @throws Exception
     */
    protected Collection<ServiceReference<?>> getServiceReferences(Class<?> type,String filter) throws Exception {
        List<ServiceReference<?>> sref = Lists.newLinkedList();
        BundleContext             bctx = getBundleContext();
        ServiceReference<?>[]     refs = bctx.getServiceReferences(type.getName(), filter);

        if(refs != null) {
            for(ServiceReference<?> sr : refs) {
                sref.add(sr);
                m_serviceReferences.add(sr);
            }

            return sref;
        }

        return sref;
    }
}
