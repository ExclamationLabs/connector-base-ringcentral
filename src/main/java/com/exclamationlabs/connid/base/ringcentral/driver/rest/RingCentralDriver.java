/*
    Copyright 2020 Exclamation Labs
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.exclamationlabs.connid.base.ringcentral.driver.rest;

import com.exclamationlabs.connid.base.connector.driver.rest.BaseRestDriver;
import com.exclamationlabs.connid.base.connector.driver.rest.RestFaultProcessor;
import com.exclamationlabs.connid.base.connector.driver.rest.RestResponseData;
import com.exclamationlabs.connid.base.ringcentral.model.RingCentralCallQueue;
import com.exclamationlabs.connid.base.ringcentral.configuration.RingCentralConfiguration;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.*;

public class RingCentralDriver extends BaseRestDriver<RingCentralConfiguration> {

    public static final String API_PATH = "scim/v2/";
    public static final String ACCOUNT_API_PATH = "restapi/v1.0/account/~/";
    public static final String EXTENSION_API_PATH = ACCOUNT_API_PATH + "extension";

    public static Map<String, RingCentralRateLimit> rateLimitMap = new HashMap<>();


    public RingCentralDriver() {
        super();
        addInvocator(RingCentralUser.class, new RingCentralUserInvocator());
        addInvocator(RingCentralCallQueue.class, new RingCentralCallQueueInvocator());
    }

    @Override
    protected RestFaultProcessor getFaultProcessor() {
        return RingCentralFaultProcessor.getInstance();
    }

    @Override
    protected String getBaseServiceUrl() {
        return configuration.getServiceUrl();
    }

    @Override
    protected boolean usesBearerAuthorization() {
        return true;
    }

    @Override
    public void test() throws ConnectorException {
        try {
            String response = executeGetRequest("scim/health", String.class, Collections.emptyMap()).getResponseObject();
            if (!StringUtils.equalsIgnoreCase("OK", response)) {
                throw new ConnectorException("Health check for RingCentral returned invalid response.");
            }
        } catch (Exception e) {
            throw new ConnectorException("Health check for RingCentral failed.", e);
        }
    }

    @Override
    public void close() {
        configuration = null;
        authenticator = null;
    }

    @Override
    public <T> RestResponseData<T> executeRequest(HttpRequestBase request, Class<T> returnType, boolean isRetry, int retryCount) {
        String remaining = null;
        String group = null;
        String expiration = null;

        RestResponseData<T> response = super.executeRequest(request, returnType, isRetry, retryCount);

        for(Header header : response.getResponseHeaders()){
            if(remaining != null && expiration != null && group != null){
                break;
            }
            switch (header.getName()) {
                case "X-Rate-Limit-Group":
                    group = header.getValue();
                    break;
                case "X-Rate-Limit-Remaining":
                    remaining = header.getValue();
                    break;
                case "X-Rate-Limit-Window":
                    expiration = header.getValue();
                    break;
            }
        }
        if(rateLimitMap.containsKey(group)){
            RingCentralRateLimit rateLimit = rateLimitMap.get(group);
            rateLimit.setRemaining(remaining);
            if(expiration == null){
                expiration = "60";
            }
            rateLimit.enqueue(Long.parseLong(expiration));
        }
        return response;
    }
    protected void rateLimitCheck(String group){
        RingCentralRateLimit rateLimit = rateLimitMap.get(group);
        if(rateLimit == null){
            rateLimit = new RingCentralRateLimit();
            rateLimitMap.put(group, rateLimit);
        }else{
            rateLimit.checkRemaining();
        }
    }
}