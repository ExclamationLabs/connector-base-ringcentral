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
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeMapBuilder;
import com.exclamationlabs.connid.base.connector.authenticator.OAuth2TokenPasswordAuthenticator;
import com.exclamationlabs.connid.base.ringcentral.adapter.RingCentralGroupsAdapter;
import com.exclamationlabs.connid.base.ringcentral.adapter.RingCentralUsersAdapter;
import com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralGroupAttribute;
import com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralUserAttribute;
import com.exclamationlabs.connid.base.ringcentral.configuration.RingCentralConfiguration;
import com.exclamationlabs.connid.base.ringcentral.driver.rest.RingCentralDriver;
import com.exclamationlabs.connid.base.ringcentral.model.RingCentralGroup;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUser;
import org.identityconnectors.framework.spi.ConnectorClass;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.*;
import static com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralGroupAttribute.*;
import static com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralUserAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.*;

@ConnectorClass(displayNameKey = "ringcentral.connector.display", configurationClass = RingCentralConfiguration.class)

public class RingCentralConnector extends BaseConnector<RingCentralUser, RingCentralGroup> {

    public RingCentralConnector() {
        setAuthenticator(new OAuth2TokenPasswordAuthenticator());


        setDriver(new RingCentralDriver());
        setUsersAdapter(new RingCentralUsersAdapter());
        setGroupsAdapter(new RingCentralGroupsAdapter());
        setUserAttributes( new ConnectorAttributeMapBuilder<>(RingCentralUserAttribute.class)
                .add(USER_ID, STRING, NOT_UPDATEABLE)
                .add(USER_NAME, STRING)
                .add(EMAIL, STRING)
                .add(EMAIL_TYPE, STRING)
                .add(CREATED, STRING, NOT_UPDATEABLE)
                .add(LAST_MODIFIED, STRING, NOT_UPDATEABLE)
                .add(LOCATION_URL, STRING, NOT_UPDATEABLE)
                .add(FORMATTED_NAME, STRING)
                .add(FAMILY_NAME, STRING)
                .add(GIVEN_NAME, STRING)
                .add(ACTIVE, BOOLEAN)
                .add(PHONE_NUMBER, STRING)
                .add(PHONE_NUMBER_TYPE, STRING)
                .add(STREET_ADDRESS, STRING)
                .add(LOCALITY, STRING)
                .add(REGION, STRING)
                .add(POSTAL_CODE, STRING)
                .add(COUNTRY, STRING)
                .add(ADDRESS_TYPE, STRING)
                .build());

        setGroupAttributes(new ConnectorAttributeMapBuilder<>(RingCentralGroupAttribute.class)
                .add(GROUP_ID, STRING, NOT_UPDATEABLE)
                .add(GROUP_NAME, STRING, REQUIRED)
                .build());
    }

}
