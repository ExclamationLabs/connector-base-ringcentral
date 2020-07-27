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

import com.exclamationlabs.connid.base.connector.driver.DriverDataNotFoundException;
import com.exclamationlabs.connid.base.connector.driver.rest.RestFaultProcessor;
import com.exclamationlabs.connid.base.ringcentral.model.response.fault.ErrorResponse;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.*;

import java.io.IOException;

public class RingCentralFaultProcessor implements RestFaultProcessor {

    private static final Log LOG = Log.getLog(RingCentralFaultProcessor.class);

    private static final RingCentralFaultProcessor instance = new RingCentralFaultProcessor();

    public static RingCentralFaultProcessor getInstance() {
        return instance;
    }

    public void process(HttpResponse httpResponse, GsonBuilder gsonBuilder) {
        String rawResponse;
        try {
            rawResponse = EntityUtils.toString(httpResponse.getEntity(), Charsets.UTF_8);
            LOG.info("Raw Fault response {0}", rawResponse);

            Header responseType = httpResponse.getFirstHeader("Content-Type");
            String responseTypeValue = responseType.getValue();
            if (!StringUtils.contains(responseTypeValue, ContentType.APPLICATION_JSON.getMimeType())) {
                // received non-JSON error response from RingCentral unable to process
                String errorMessage = "Unable to parse RingCentral response, not valid JSON: ";
                LOG.info("{0} {1}", errorMessage, rawResponse);
                throw new ConnectorException(errorMessage + rawResponse);
            }

            handleFaultResponse(rawResponse, gsonBuilder);

        } catch (IOException e) {
            throw new ConnectorException("Unable to read fault response from RingCentral response. " +
                    "Status: " + httpResponse.getStatusLine().getStatusCode() + ", " +
                    httpResponse.getStatusLine().getReasonPhrase(), e);
        }
    }


    private void handleFaultResponse(String rawResponse, GsonBuilder gsonBuilder) {
        ErrorResponse fault = gsonBuilder.create().fromJson(rawResponse, ErrorResponse.class);
        if (fault != null) {
            try {
                matchFaultText(fault);
            } catch (DriverDataNotFoundException notFound) {
                LOG.info(fault.getDetail());
            }
        } else {
            throw new ConnectorException("Unable to read RingCentral fault information: " + rawResponse);
        }
    }

    private void matchFaultText(ErrorResponse fault) {
        String faultMessage = fault.getDetail() == null ? fault.getMessage() : fault.getDetail();

        if (StringUtils.containsIgnoreCase(faultMessage, "is not found")) {
            throw new DriverDataNotFoundException("not found");
        } else if (StringUtils.containsIgnoreCase(faultMessage, "Extension e-mail already exists on account")) {
            throw new AlreadyExistsException("Cannot create user: duplicate email in RingCentral.  Details: " +
                    fault);
        } else {
            throw new ConnectorException("Unknown fault occurred with RingCentral call: " +
                    fault);
        }
    }
}
