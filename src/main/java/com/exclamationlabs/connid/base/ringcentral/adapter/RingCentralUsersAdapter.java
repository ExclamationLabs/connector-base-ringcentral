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

import com.exclamationlabs.connid.base.connector.adapter.AdapterValueTypeConverter;
import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.ringcentral.configuration.RingCentralConfiguration;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUser;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUserAddress;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUserEmail;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUserPhone;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.*;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.*;
import static com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralUserAttribute.*;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.*;

import java.util.HashSet;
import java.util.Set;

public class RingCentralUsersAdapter extends BaseAdapter<RingCentralUser, RingCentralConfiguration> {


    @Override
    public ObjectClass getType() {
        return ObjectClass.ACCOUNT;
    }

    @Override
    public Class<RingCentralUser> getIdentityModelClass() {
        return RingCentralUser.class;
    }

    @Override
    public Set<ConnectorAttribute> getConnectorAttributes() {
        Set<ConnectorAttribute> result = new HashSet<>();
        result.add(new ConnectorAttribute(USER_ID.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(USER_NAME.name(), STRING));
        result.add(new ConnectorAttribute(EMAIL.name(), STRING));
        result.add(new ConnectorAttribute(EMAIL_TYPE.name(), STRING));
        result.add(new ConnectorAttribute(CREATED.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(LAST_MODIFIED.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(LOCATION_URL.name(), STRING, NOT_UPDATEABLE));
        result.add(new ConnectorAttribute(FORMATTED_NAME.name(), STRING));
        result.add(new ConnectorAttribute(FAMILY_NAME.name(), STRING));
        result.add(new ConnectorAttribute(GIVEN_NAME.name(), STRING));
        result.add(new ConnectorAttribute(ACTIVE.name(), BOOLEAN));
        result.add(new ConnectorAttribute(PHONE_NUMBER.name(), STRING));
        result.add(new ConnectorAttribute(PHONE_NUMBER_TYPE.name(), STRING));
        result.add(new ConnectorAttribute(STREET_ADDRESS.name(), STRING));
        result.add(new ConnectorAttribute(LOCALITY.name(), STRING));
        result.add(new ConnectorAttribute(REGION.name(), STRING));
        result.add(new ConnectorAttribute(POSTAL_CODE.name(), STRING));
        result.add(new ConnectorAttribute(COUNTRY.name(), STRING));
        result.add(new ConnectorAttribute(ADDRESS_TYPE.name(), STRING));

        return result;

    }

    @Override
    protected Set<Attribute> constructAttributes(RingCentralUser user) {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(AttributeBuilder.build(USER_ID.name(), user.getId()));
        attributes.add(AttributeBuilder.build(USER_NAME.name(), user.getUserName()));
        attributes.add(AttributeBuilder.build(ACTIVE.name(), user.getActive()));
        attributes.add(AttributeBuilder.build(CREATED.name(), user.getMeta().getCreated()));
        attributes.add(AttributeBuilder.build(LAST_MODIFIED.name(), user.getMeta().getLastModified()));
        attributes.add(AttributeBuilder.build(LOCATION_URL.name(), user.getMeta().getLocation()));
        attributes.add(AttributeBuilder.build(FAMILY_NAME.name(), user.getName().getFamilyName()));
        attributes.add(AttributeBuilder.build(GIVEN_NAME.name(), user.getName().getGivenName()));
        attributes.add(AttributeBuilder.build(FORMATTED_NAME.name(), user.getName().getFormatted()));


        if (user.getEmails() != null && user.getEmails().size() > 0) {
            attributes.add(AttributeBuilder.build(EMAIL.name(), user.getEmails().get(0).getValue()));
            attributes.add(AttributeBuilder.build(EMAIL_TYPE.name(), user.getEmails().get(0).getType()));
        }

        if (user.getPhoneNumbers() != null && user.getPhoneNumbers().size() > 0) {
            attributes.add(AttributeBuilder.build(PHONE_NUMBER.name(), user.getPhoneNumbers().get(0).getValue()));
            attributes.add(AttributeBuilder.build(PHONE_NUMBER_TYPE.name(), user.getPhoneNumbers().get(0).getType()));
        }

        if (user.getAddresses() != null && user.getAddresses().size() > 0) {
            attributes.add(AttributeBuilder.build(STREET_ADDRESS.name(), user.getAddresses().get(0).getStreetAddress()));
            attributes.add(AttributeBuilder.build(LOCALITY.name(), user.getAddresses().get(0).getLocality()));
            attributes.add(AttributeBuilder.build(REGION.name(), user.getAddresses().get(0).getRegion()));
            attributes.add(AttributeBuilder.build(POSTAL_CODE.name(), user.getAddresses().get(0).getPostalCode()));
            attributes.add(AttributeBuilder.build(COUNTRY.name(), user.getAddresses().get(0).getCountry()));
            attributes.add(AttributeBuilder.build(ADDRESS_TYPE.name(), user.getAddresses().get(0).getType()));
         }

        return attributes;
    }

    @Override
    protected RingCentralUser constructModel(Set<Attribute> attributes,
                                             Set<Attribute> multiValueAdd,
                                             Set<Attribute> multiValueRemove,
                                             boolean isCreate) {
        RingCentralUser user = new RingCentralUser(!isCreate);

        user.setId(AdapterValueTypeConverter.getIdentityIdAttributeValue(attributes));

        if (user.getId()==null) {
            user.setId(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, USER_ID));
        }

        user.setUserName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, USER_NAME));
        user.setActive(AdapterValueTypeConverter.getSingleAttributeValue(Boolean.class, attributes, ACTIVE));

        user.getName().setFamilyName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, FAMILY_NAME));
        user.getName().setGivenName(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, GIVEN_NAME));
        user.getName().setFormatted(AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, FORMATTED_NAME));

        String email = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, EMAIL);
        String emailType = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, EMAIL_TYPE);
        if (StringUtils.isNotBlank(email)) {
            user.getEmails().add(new RingCentralUserEmail(email, emailType == null ? "work" : emailType));
        }

        String phone = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, PHONE_NUMBER);
        String phoneType = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, PHONE_NUMBER_TYPE);
        if (StringUtils.isNotBlank(phone)) {
            user.getPhoneNumbers().add(new RingCentralUserPhone(phone, phoneType));
        }

        String addressLine = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, STREET_ADDRESS);
        String locality = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, LOCALITY);
        String region = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, REGION);
        String postalCode = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, POSTAL_CODE);
        String country = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, COUNTRY);
        String addressType = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, ADDRESS_TYPE);
        if (StringUtils.isNotBlank(addressLine) || StringUtils.isNotBlank(locality) || StringUtils.isNotBlank(region) ||
                StringUtils.isNotBlank(postalCode) || StringUtils.isNotBlank(country)) {
            user.getAddresses().add(new RingCentralUserAddress(addressLine, locality, region, postalCode, country, addressType));
        }

        return user;
    }
}
