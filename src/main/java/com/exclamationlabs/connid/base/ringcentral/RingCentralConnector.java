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

package com.exclamationlabs.connid.base.ringcentral;

import com.exclamationlabs.connid.base.connector.BaseConnector;
import com.exclamationlabs.connid.base.connector.BaseFullAccessConnector;
import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenPasswordAuthenticator;
import com.exclamationlabs.connid.base.ringcentral.adapter.RingCentralCallQueueAdapter;
import com.exclamationlabs.connid.base.ringcentral.adapter.RingCentralUsersAdapter;
import com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralCallQueueAttribute;
import com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralUserAttribute;
import com.exclamationlabs.connid.base.ringcentral.configuration.RingCentralConfiguration;
import com.exclamationlabs.connid.base.ringcentral.driver.rest.RingCentralDriver;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.*;
import org.identityconnectors.framework.spi.ConnectorClass;

import java.util.HashSet;
import java.util.Set;

@ConnectorClass(displayNameKey = "ringcentral.connector.display", configurationClass = RingCentralConfiguration.class)

public class RingCentralConnector extends BaseFullAccessConnector {

    public RingCentralConnector() {
        setAuthenticator(new OAuth2TokenPasswordAuthenticator());

        setDriver(new RingCentralDriver());
        setAdapters(new RingCentralUsersAdapter(), new RingCentralCallQueueAdapter());
        setEnhancedFiltering(true);
        Set<String> filterSet = new HashSet<>();
        filterSet.add(RingCentralUserAttribute.USER_NAME.name());
        filterSet.add(RingCentralCallQueueAttribute.USER_MEMBERS.name());
        setFilterAttributes(filterSet);
    }

    @Override
    protected FilterTranslator<String> getConnectorFilterTranslator(ObjectClass objectClass) {
        BaseAdapter<?> matchedAdapter = adapterMap.get(objectClass);
        if (matchedAdapter == null) {
            throw new ConnectorException("Unsupported object class for filter translator: " + objectClass);
        } else {
            return new CustomFilterTranslator(getFilterAttributes());
        }
    }

    public static class CustomFilterTranslator extends AbstractFilterTranslator<String> {

        private final Set<String> acceptableAttributeNames;

        public CustomFilterTranslator(Set<String> attributes) {
            acceptableAttributeNames = attributes;
        }

        @Override
        // Not normally invoked by Midpoint
        protected String createEqualsExpression(EqualsFilter filter, boolean not) {
            if (not || filter == null) {
                return null;
            }

            Attribute attr = filter.getAttribute();
            if (!attr.is(Name.NAME) && !attr.is(Uid.NAME)) {
                return null;
            }
            String value = AttributeUtil.getAsStringValue(attr);

            return (checkSearchValue(value) == null) ? null : value;
        }

        @Override
        // Normally invoked by Midpoint for filtering
        protected String createContainsExpression(ContainsFilter filter, boolean not) {
            if (StringUtils.equals(filter.getAttribute().getName(), Uid.NAME) || acceptableAttributeNames.contains(filter.getAttribute().getName())) {
                return filter.getAttribute().getName() + BaseConnector.FILTER_SEPARATOR + filter.getValue();
            } else {
                throw new InvalidAttributeValueException("Filter on attribute " + filter.getAttribute().getName() + " not supported.");
            }
        }

        @Override
        protected String createContainsAllValuesExpression(ContainsAllValuesFilter filter, boolean not) {
            if (StringUtils.equals(filter.getAttribute().getName(), Uid.NAME) || acceptableAttributeNames.contains(filter.getAttribute().getName())) {
                return filter.getAttribute().getName() + BaseConnector.FILTER_SEPARATOR + filter.getAttribute().getValue().get(0);
            } else {
                throw new InvalidAttributeValueException("Filter on attribute " + filter.getAttribute().getName() + " not supported.");
            }
        }

        private String checkSearchValue(String value) {
            if (StringUtil.isEmpty(value)) {
                return null;
            }
            if (value.contains("*") || value.contains("&") || value.contains("|")) {
                throw new IllegalArgumentException(
                        "Value of search attribute contains illegal character(s).");
            }
            return value;
        }
    }


}
