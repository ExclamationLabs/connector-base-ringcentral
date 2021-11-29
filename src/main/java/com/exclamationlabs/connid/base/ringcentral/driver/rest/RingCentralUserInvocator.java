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

import com.exclamationlabs.connid.base.connector.driver.DriverInvocator;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.ringcentral.model.response.ListUsersResponse;
import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUser;
import com.exclamationlabs.connid.base.ringcentral.model.user.extension.RingCentralUserExtension;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RingCentralUserInvocator implements DriverInvocator<RingCentralDriver, RingCentralUser> {

    @Override
    public String create(RingCentralDriver driver, RingCentralUser userModel) throws ConnectorException {
        // Create user extension
        RingCentralUserExtension userExtension = new RingCentralUserExtension();
        userExtension.getContact().setEmail(userModel.getEmails().get(0).getValue());
        userExtension.getContact().setFirstName(userModel.getName().getGivenName());
        userExtension.getContact().setLastName(userModel.getName().getFamilyName());
        userExtension.setType("User");
        driver.executePostRequest(RingCentralDriver.EXTENSION_API_PATH,
                RingCentralUserExtension.class, userExtension);

        driver.rateLimitCheck("heavy");
        RingCentralUser responseUser = driver.executePostRequest(RingCentralDriver.API_PATH + "Users",
                RingCentralUser.class, userModel).getResponseObject();

        if (responseUser == null || responseUser.getId() == null) {
            throw new ConnectorException("Response from user creation was invalid");
        }
        return responseUser.getId();
    }

    @Override
    public void update(RingCentralDriver driver, String userId, RingCentralUser modifiedUser) throws ConnectorException {
        driver.rateLimitCheck("light");
        RingCentralUser currentUser = getOne(driver, userId, Collections.emptyMap());
        updateCurrentUser(currentUser, modifiedUser);

        driver.rateLimitCheck("heavy");

        RingCentralUser responseUser = driver.executePutRequest(RingCentralDriver.API_PATH +
                "Users/" + userId, RingCentralUser.class, modifiedUser).getResponseObject();

        if (responseUser == null || responseUser.getId() == null) {
            throw new ConnectorException("Response from user update was invalid");
        }
    }

    @Override
    public void delete(RingCentralDriver driver, String userId) throws ConnectorException {
        driver.rateLimitCheck("heavy");
        driver.executeDeleteRequest(RingCentralDriver.API_PATH + "Users/" + userId, null);
    }

    @Override
    public Set<RingCentralUser> getAll(RingCentralDriver driver, ResultsFilter filter,
                                       ResultsPaginator paginator, Integer max) throws ConnectorException {
        driver.rateLimitCheck("light");
        if (filter.hasFilter()) {
            final String FILTER_PART = "?count=1000&filter=userName%20eq%20";
            final String FILTER_PART2 = "\"" + filter.getValue() + "\"";
            ListUsersResponse response;
            try {
                response = driver.executeGetRequest(RingCentralDriver.API_PATH +
                                "Users" + FILTER_PART + URLEncoder.encode(FILTER_PART2, StandardCharsets.UTF_8.name()), ListUsersResponse.class,
                        Collections.emptyMap()).getResponseObject();
            } catch (UnsupportedEncodingException e) {
                throw new ConnectorException(e);
            }
            return new HashSet<>(response.getUsers());
        } else {

            ListUsersResponse response = driver.executeGetRequest(RingCentralDriver.API_PATH +
                            "Users?count=1000", ListUsersResponse.class,
                    Collections.emptyMap()).getResponseObject();
            return new HashSet<>(response.getUsers());
        }

    }

    @Override
    public RingCentralUser getOne(RingCentralDriver driver, String userId, Map<String, Object> map) throws ConnectorException {
        driver.rateLimitCheck("light");
        return driver.executeGetRequest(
                RingCentralDriver.API_PATH + "Users/" + userId, RingCentralUser.class, Collections.emptyMap()).getResponseObject();
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
