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

import com.exclamationlabs.connid.base.connector.configuration.ConnectorProperty;
import com.exclamationlabs.connid.base.connector.driver.rest.BaseRestDriver;
import com.exclamationlabs.connid.base.connector.driver.rest.RestFaultProcessor;
import com.exclamationlabs.connid.base.ringcentral.model.RingCentralGroup;
import com.exclamationlabs.connid.base.ringcentral.model.response.ListUsersResponse;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUser;
import com.exclamationlabs.connid.base.ringcentral.model.user.extension.RingCentralUserExtension;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.*;

public class RingCentralDriver extends BaseRestDriver<RingCentralUser, RingCentralGroup> {

    private static final String API_PATH = "scim/v2/";
    private static final String EXTENSION_API_PATH = "restapi/v1.0/account/~/extension";

    @Override
    protected RestFaultProcessor getFaultProcessor() {
        return RingCentralFaultProcessor.getInstance();
    }

    @Override
    protected String getBaseServiceUrl() {
        return "https://platform.devtest.ringcentral.com/";
    }

    @Override
    protected boolean usesBearerAuthorization() {
        return true;
    }

    @Override
    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return null;
    }

    @Override
    public void test() throws ConnectorException {
        try {
            String response = executeGetRequest("scim/health", String.class);
            if (!StringUtils.equalsIgnoreCase("OK", response)) {
                throw new ConnectorException("Health check for RingCentral returned invalid response.");
            }
        } catch (Exception e) {
            throw new ConnectorException("Health check for RingCentral failed.", e);
        }
    }

    @Override
    public void close() {
        configuration = null;
        authenticator = null;
    }

    @Override
    public String createUser(RingCentralUser userModel) throws ConnectorException {
        // Create user extension
        RingCentralUserExtension userExtension = new RingCentralUserExtension();
        userExtension.getContact().setEmail(userModel.getEmails().get(0).getValue());
        userExtension.getContact().setFirstName(userModel.getName().getGivenName());
        userExtension.getContact().setLastName(userModel.getName().getFamilyName());
        userExtension.setType("User");
        executePostRequest(EXTENSION_API_PATH, RingCentralUserExtension.class, userExtension);

        RingCentralUser responseUser = executePostRequest(API_PATH + "Users", RingCentralUser.class, userModel);

        if (responseUser == null || responseUser.getId() == null) {
            throw new ConnectorException("Response from user creation was invalid");
        }
        return responseUser.getId();
    }

    @Override
    public String createGroup(RingCentralGroup groupModel) throws ConnectorException {
        throw new ConnectorException("Groups not supported by RingCentral connector");
    }

    @Override
    public void updateUser(String userId, RingCentralUser modifiedUser) throws ConnectorException {
        RingCentralUser currentUser = getUser(userId);
        updateCurrentUser(currentUser, modifiedUser);
        RingCentralUser responseUser = executePutRequest(API_PATH + "Users/" + userId, RingCentralUser.class, modifiedUser);

        if (responseUser == null || responseUser.getId() == null) {
            throw new ConnectorException("Response from user update was invalid");
        }
    }

    @Override
    public void updateGroup(String groupId, RingCentralGroup groupModel) throws ConnectorException {
        throw new ConnectorException("Groups not supported by RingCentral connector");
    }

    @Override
    public void deleteUser(String userId) throws ConnectorException {
       executeDeleteRequest(API_PATH + "Users/" + userId, null);
    }

    @Override
    public void deleteGroup(String groupId) throws ConnectorException {
        throw new ConnectorException("Groups not supported by RingCentral connector");
    }

    @Override
    public List<RingCentralUser> getUsers() throws ConnectorException {
        ListUsersResponse response = executeGetRequest(API_PATH + "Users", ListUsersResponse.class);
        return response.getUsers();
    }

    @Override
    public List<RingCentralGroup> getGroups() throws ConnectorException {
        throw new ConnectorException("Groups not supported by RingCentral connector");
    }

    @Override
    public RingCentralUser getUser(String userId) throws ConnectorException {
        return executeGetRequest(API_PATH + "Users/" + userId, RingCentralUser.class);
    }

    @Override
    public RingCentralGroup getGroup(String groupId) throws ConnectorException {
        throw new ConnectorException("Groups not supported by RingCentral connector");
    }

    @Override
    public void addGroupToUser(String groupId, String userId) throws ConnectorException {
        throw new ConnectorException("Groups not supported by RingCentral connector");
    }

    @Override
    public void removeGroupFromUser(String groupId, String userId) throws ConnectorException {
        throw new ConnectorException("Groups not supported by RingCentral connector");
    }

    private void updateCurrentUser(final RingCentralUser currentUser, RingCentralUser modifiedUser) {
        if (modifiedUser.getUserName() == null) {
            modifiedUser.setUserName(currentUser.getUserName());
        }

        if (modifiedUser.getName() == null) {
            modifiedUser.setName(currentUser.getName());
        } else {
            if (modifiedUser.getName().getFamilyName() == null) {
                modifiedUser.getName().setFamilyName(currentUser.getName().getFamilyName());
            }
            if (modifiedUser.getName().getGivenName() == null) {
                modifiedUser.getName().setGivenName(currentUser.getName().getGivenName());
            }
        }

        if (modifiedUser.getEmails() == null || modifiedUser.getEmails().isEmpty()) {
            modifiedUser.setEmails(currentUser.getEmails());
        }

        if (modifiedUser.getAddresses() == null || modifiedUser.getAddresses().isEmpty()) {
            modifiedUser.setAddresses(currentUser.getAddresses());
        }

        if (modifiedUser.getPhoneNumbers() == null || modifiedUser.getPhoneNumbers().isEmpty()) {
            modifiedUser.setPhoneNumbers(currentUser.getPhoneNumbers());
        }

        if (modifiedUser.getActive() == null) {
            modifiedUser.setActive(currentUser.getActive());
        }
    }
}