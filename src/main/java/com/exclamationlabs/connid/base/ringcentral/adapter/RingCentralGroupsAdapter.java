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

package com.exclamationlabs.connid.base.ringcentral.adapter;

import com.exclamationlabs.connid.base.connector.adapter.BaseGroupsAdapter;
import com.exclamationlabs.connid.base.ringcentral.model.RingCentralGroup;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUser;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ConnectorObject;

import java.util.Set;

public class RingCentralGroupsAdapter extends BaseGroupsAdapter<RingCentralUser, RingCentralGroup> {


    @Override
    protected RingCentralGroup constructGroup(Set<Attribute> attributes, boolean creation) {
        return new RingCentralGroup();
    }

    @Override
    protected ConnectorObject constructConnectorObject(RingCentralGroup group) {
        return getConnectorObjectBuilder(group)
                 .build();
    }
}
