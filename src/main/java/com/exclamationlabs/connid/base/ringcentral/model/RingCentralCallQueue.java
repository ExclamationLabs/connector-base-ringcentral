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

package com.exclamationlabs.connid.base.ringcentral.model;

import com.exclamationlabs.connid.base.connector.model.IdentityModel;

import java.util.List;

public class RingCentralCallQueue implements IdentityModel {

    private String uri;
    private String id;
    private String extensionNumber;
    private String name;

    private List<String> userMembers;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExtensionNumber() {
        return extensionNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUserMembers() {
        return userMembers;
    }

    public void setUserMembers(List<String> userMembers) {
        this.userMembers = userMembers;
    }

    @Override
    public String getIdentityIdValue() {
        return getId();
    }

    @Override
    public String getIdentityNameValue() {
        return getName();
    }

    @Override
    public boolean equals(Object input) {
        return identityEquals(RingCentralCallQueue.class, this, input);
    }

    @Override
    public int hashCode() {
        return identityHashCode();
    }

}
