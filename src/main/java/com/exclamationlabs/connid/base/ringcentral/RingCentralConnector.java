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

import com.exclamationlabs.connid.base.connector.BaseFullAccessConnector;
import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenPasswordAuthenticator;
import com.exclamationlabs.connid.base.ringcentral.adapter.RingCentralCallQueueAdapter;
import com.exclamationlabs.connid.base.ringcentral.adapter.RingCentralUsersAdapter;
import com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralCallQueueAttribute;
import com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralUserAttribute;
import com.exclamationlabs.connid.base.ringcentral.configuration.RingCentralConfiguration;
import com.exclamationlabs.connid.base.ringcentral.driver.rest.RingCentralDriver;
import org.identityconnectors.framework.spi.ConnectorClass;

import java.util.HashSet;
import java.util.Set;

@ConnectorClass(displayNameKey = "ringcentral.connector.display", configurationClass = RingCentralConfiguration.class)

public class RingCentralConnector extends BaseFullAccessConnector<RingCentralConfiguration> {

    public RingCentralConnector() {
        super(RingCentralConfiguration.class);
        setAuthenticator((Authenticator) new OAuth2TokenPasswordAuthenticator());

        setDriver(new RingCentralDriver());
        setAdapters(new RingCentralUsersAdapter(), new RingCentralCallQueueAdapter());
        setEnhancedFiltering(true);
        Set<String> filterSet = new HashSet<>();
        filterSet.add(RingCentralUserAttribute.USER_NAME.name());
        filterSet.add(RingCentralCallQueueAttribute.USER_MEMBERS.name());
        setFilterAttributes(filterSet);
    }

}
