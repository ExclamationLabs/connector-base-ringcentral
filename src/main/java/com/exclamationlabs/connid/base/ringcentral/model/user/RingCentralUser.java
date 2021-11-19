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

package com.exclamationlabs.connid.base.ringcentral.model.user;

import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.ringcentral.model.MetaInformation;
import com.exclamationlabs.connid.base.ringcentral.model.RingCentralCallQueue;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RingCentralUser implements IdentityModel {

    private String id;
    private String userName;
    private Boolean active;

    private MetaInformation meta;
    private RingCentralUserName name;
    private List<RingCentralUserEmail> emails;
    private List<RingCentralUserPhone> phoneNumbers;
    private List<RingCentralUserAddress> addresses;

    @SerializedName("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User")
    private RingCentralUserExtensionLink extensionLink;

    private List<String> schemas;

    public RingCentralUser() {
        this(false);
    }

    public RingCentralUser(boolean update) {
        if (update) {
            name = new RingCentralUserName();
            emails = new ArrayList<>();
            phoneNumbers = new ArrayList<>();
            addresses = new ArrayList<>();
            schemas = new ArrayList<>();
            schemas.add("urn:ietf:params:scim:schemas:core:2.0:User");
        } else {
            name = new RingCentralUserName();
            emails = new ArrayList<>();
            phoneNumbers = new ArrayList<>();
            addresses = new ArrayList<>();
            schemas = new ArrayList<>();
            extensionLink = new RingCentralUserExtensionLink();
            extensionLink.setDepartment("Technology");
            schemas.add("urn:ietf:params:scim:schemas:core:2.0:User");
            schemas.add("urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");
        }

    }

    @Override
    public String getIdentityIdValue() {
        return getId();
    }

    @Override
    public String getIdentityNameValue() {
        return getUserName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public MetaInformation getMeta() {
        return meta;
    }

    public void setMeta(MetaInformation meta) {
        this.meta = meta;
    }

    public RingCentralUserName getName() {
        return name;
    }

    public void setName(RingCentralUserName name) {
        this.name = name;
    }

    public List<RingCentralUserEmail> getEmails() {
        return emails;
    }

    public void setEmails(List<RingCentralUserEmail> emails) {
        this.emails = emails;
    }

    public List<RingCentralUserPhone> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<RingCentralUserPhone> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<RingCentralUserAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<RingCentralUserAddress> addresses) {
        this.addresses = addresses;
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    @Override
    public boolean equals(Object input) {
        return identityEquals(RingCentralUser.class, this, input);
    }

    @Override
    public int hashCode() {
        return identityHashCode();
    }
}
