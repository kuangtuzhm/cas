/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.cas.authentication.principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * The {@link AbstractServiceFactory} is the parent class providing
 * convenience methods for creating service objects.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
public abstract class AbstractServiceFactory<T extends Service> implements ServiceFactory<T> {

    /** Logger instance. */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public <T1 extends Service> T1 createService(final String id, final Class<? extends Service> clazz) {
        final Service service = createService(id);

        if (!clazz.isAssignableFrom(service.getClass())) {
            throw new ClassCastException("Service [" + service.getId()
                    + " is of type " + service.getClass()
                    + " when we were expecting " + clazz);
        }
        return (T1) service;
    }

    @Override
    public <T1 extends Service> T1 createService(final HttpServletRequest request, final Class<? extends Service> clazz) {
        final Service service = createService(request);

        if (!clazz.isAssignableFrom(service.getClass())) {
            throw new ClassCastException("Service [" + service.getId()
                    + " is of type " + service.getClass()
                    + " when we were expecting " + clazz);
        }
        return (T1) service;
    }

    /**
     * Cleanup the url. Removes jsession ids and query strings.
     *
     * @param url the url
     * @return sanitized url.
     */
    protected static String cleanupUrl(final String url) {
        if (url == null) {
            return null;
        }

        final int jsessionPosition = url.indexOf(";jsession");

        if (jsessionPosition == -1) {
            return url;
        }

        final int questionMarkPosition = url.indexOf('?');

        if (questionMarkPosition < jsessionPosition) {
            return url.substring(0, url.indexOf(";jsession"));
        }

        return url.substring(0, jsessionPosition)
                + url.substring(questionMarkPosition);
    }

}

