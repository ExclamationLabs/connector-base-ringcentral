/*
    Copyright 2021 Exclamation Labs
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

import com.exclamationlabs.connid.base.connector.adapter.AdapterValueTypeConverter;
import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.ringcentral.model.RingCentralCallQueue;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.ASSIGNMENT_IDENTIFIER;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralCallQueueAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.MULTIVALUED;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.NOT_UPDATEABLE;

public class RingCentralCallQueueAdapter extends BaseAdapter<RingCentralCallQueue> {


    @Override
    public ObjectClass getType() {
        return new ObjectClass("CallQueue");
    }

    @Override
    public Class<RingCentralCallQueue> getIdentityModelClass() {
        return RingCentralCallQueue.class;
    }

    @Override
    public List<ConnectorAttribute> getConnectorAttributes() {
        List<ConnectorAttribute> result = new ArrayList<>();
        result.add(new ConnectorAttribute(CALL_QUEUE_ID.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(CALL_QUEUE_NAME.name(), STRING));
        result.add(new ConnectorAttribute(URI.name(), STRING));
        result.add(new ConnectorAttribute(EXTENSION_NUMBER.name(), STRING));
        result.add(new ConnectorAttribute(USER_MEMBERS.name(), ASSIGNMENT_IDENTIFIER, MULTIVALUED));

        return result;

    }

    @Override
    protected List<Attribute> constructAttributes(RingCentralCallQueue queue) {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(AttributeBuilder.build(CALL_QUEUE_ID.name(), queue.getId()));
        attributes.add(AttributeBuilder.build(CALL_QUEUE_NAME.name(), queue.getName()));
        attributes.add(AttributeBuilder.build(URI.name(), queue.getUri()));
        attributes.add(AttributeBuilder.build(EXTENSION_NUMBER.name(), queue.getExtensionNumber()));
        attributes.add(AttributeBuilder.build(USER_MEMBERS.name(), queue.getUserMembers()));

        return attributes;
    }

    @Override
    protected RingCentralCallQueue constructModel(Set<Attribute> attributes, boolean isCreate) {
        RingCentralCallQueue queue = new RingCentralCallQueue();

        queue.setId(AdapterValueTypeConverter.getIdentityIdAttributeValue(attributes));

        queue.setName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, CALL_QUEUE_NAME));
        queue.setUri(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, URI));
        queue.setExtensionNumber(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, EXTENSION_NUMBER));

        queue.setUserMembers(AdapterValueTypeConverter.getMultipleAttributeValue(List.class, attributes, USER_MEMBERS));

        return queue;
    }
}
