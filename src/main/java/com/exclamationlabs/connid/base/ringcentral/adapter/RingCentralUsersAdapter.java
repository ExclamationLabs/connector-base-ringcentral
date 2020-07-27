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
import com.exclamationlabs.connid.base.connector.adapter.BaseUsersAdapter;
import com.exclamationlabs.connid.base.ringcentral.model.RingCentralGroup;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUser;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUserAddress;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUserEmail;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUserPhone;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;

import static com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralUserAttribute.*;

import java.util.Set;

public class RingCentralUsersAdapter extends BaseUsersAdapter<RingCentralUser, RingCentralGroup> {
    @Override
    protected RingCentralUser constructUser(Set<Attribute> attributes, boolean creation) {
        RingCentralUser user = new RingCentralUser(!creation);

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
            user.getEmails().add(new RingCentralUserEmail(email, emailType));
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

    @Override
    protected ConnectorObject constructConnectorObject(RingCentralUser user) {
        ConnectorObjectBuilder builder = getConnectorObjectBuilder(user)
                .addAttribute(AttributeBuilder.build(USER_ID.name(), user.getId()))
                .addAttribute(AttributeBuilder.build(USER_NAME.name(), user.getUserName()))
                .addAttribute(AttributeBuilder.build(ACTIVE.name(), user.getActive()))
                .addAttribute(AttributeBuilder.build(CREATED.name(), user.getMeta().getCreated()))
                .addAttribute(AttributeBuilder.build(LAST_MODIFIED.name(), user.getMeta().getLastModified()))
                .addAttribute(AttributeBuilder.build(LOCATION_URL.name(), user.getMeta().getLocation()))
                .addAttribute(AttributeBuilder.build(FAMILY_NAME.name(), user.getName().getFamilyName()))
                .addAttribute(AttributeBuilder.build(GIVEN_NAME.name(), user.getName().getGivenName()))
                .addAttribute(AttributeBuilder.build(FORMATTED_NAME.name(), user.getName().getFormatted()));

        if (user.getEmails() != null && user.getEmails().size() > 0) {
            builder.addAttribute(AttributeBuilder.build(EMAIL.name(),user.getEmails().get(0).getValue()))
                .addAttribute(AttributeBuilder.build(EMAIL_TYPE.name(), user.getEmails().get(0).getType()));
        }

        if (user.getPhoneNumbers() != null && user.getPhoneNumbers().size() > 0) {
            builder.addAttribute(AttributeBuilder.build(PHONE_NUMBER.name(),user.getPhoneNumbers().get(0).getValue()))
                    .addAttribute(AttributeBuilder.build(PHONE_NUMBER_TYPE.name(), user.getPhoneNumbers().get(0).getType()));
        }

        if (user.getAddresses() != null && user.getAddresses().size() > 0) {
            builder.addAttribute(AttributeBuilder.build(STREET_ADDRESS.name(),user.getAddresses().get(0).getStreetAddress()))
                    .addAttribute(AttributeBuilder.build(LOCALITY.name(), user.getAddresses().get(0).getLocality()))
                    .addAttribute(AttributeBuilder.build(REGION.name(), user.getAddresses().get(0).getRegion()))
                    .addAttribute(AttributeBuilder.build(POSTAL_CODE.name(), user.getAddresses().get(0).getPostalCode()))
                    .addAttribute(AttributeBuilder.build(COUNTRY.name(), user.getAddresses().get(0).getCountry()))
                    .addAttribute(AttributeBuilder.build(ADDRESS_TYPE.name(), user.getAddresses().get(0).getType()));
        }

        return builder.build();
    }
}
