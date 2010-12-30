/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.cas.authentication.principal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jasig.cas.server.authentication.AttributePrincipal;
import org.jasig.cas.server.authentication.Service;
import org.jasig.cas.server.util.UniqueTicketIdGenerator;
import org.jasig.cas.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of a WebApplicationService.
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.3 $ $Date: 2007/04/19 20:13:01 $
 * @since 3.1
 *
 */
public abstract class AbstractWebApplicationService implements WebApplicationService {

    protected static final Logger LOG = LoggerFactory.getLogger(SamlService.class);
    
    private static final Map<String, Object> EMPTY_MAP = Collections.unmodifiableMap(new HashMap<String, Object>());

    // TODO disabled
    private static final UniqueTicketIdGenerator GENERATOR = null;
    
    /** The id of the service. */
    private final String id;
    
    /** The original url provided, used to reconstruct the redirect url. */
    private final String originalUrl;

    private final String artifactId;
    
    private AttributePrincipal principal;
    
    private boolean loggedOutAlready = false;
    
    private final HttpClient httpClient;
    
    protected AbstractWebApplicationService(final String id, final String originalUrl, final String artifactId, final HttpClient httpClient) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.artifactId = artifactId;
        this.httpClient = httpClient;
    }
    
    public final String toString() {
        return this.id;
    }
    
    public final String getId() {
        return this.id;
    }
    
    public final String getArtifactId() {
        return this.artifactId;
    }

    public final Map<String, Object> getAttributes() {
        return EMPTY_MAP;
    }

    protected static String cleanupUrl(final String url) {
        if (url == null) {
            return null;
        }

        final int jsessionPosition = url.indexOf(";jsession");

        if (jsessionPosition == -1) {
            return url;
        }

        final int questionMarkPosition = url.indexOf("?");

        if (questionMarkPosition < jsessionPosition) {
            return url.substring(0, url.indexOf(";jsession"));
        }

        return url.substring(0, jsessionPosition)
            + url.substring(questionMarkPosition);
    }
    
    protected final String getOriginalUrl() {
        return this.originalUrl;
    }

    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof Service) {
            final Service service = (Service) object;

            return getId().equals(service.getId());
        }

        return false;
    }
    
    public int hashCode() {
        final int prime = 41;
        int result = 1;
        result = prime * result
            + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }
    
    protected AttributePrincipal getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(final AttributePrincipal principal) {
        this.principal = principal;
    }
    
    public boolean matches(final Service service) {
        return this.id.equals(service.getId());
    }
    
    public synchronized boolean logOutOfService(final String sessionIdentifier) {
        if (this.loggedOutAlready) {
            return true;
        }

        LOG.debug("Sending logout request for: " + getId());

        final String logoutRequest = "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" ID=\""
            + GENERATOR.getNewTicketId("LR")
            + "\" Version=\"2.0\" IssueInstant=\"" + getCurrentDateAndTime()
            + "\"><saml:NameID xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">@NOT_USED@</saml:NameID><samlp:SessionIndex>"
            + sessionIdentifier + "</samlp:SessionIndex></samlp:LogoutRequest>";
        
        this.loggedOutAlready = true;
        
        if (this.httpClient != null) {
            return this.httpClient.sendMessageToEndPoint(getOriginalUrl(), logoutRequest, true);
        }
        
        return false;
    }

    protected String getCurrentDateAndTime() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return dateFormat.format(new Date());
    }
}