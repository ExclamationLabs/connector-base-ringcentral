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

package com.exclamationlabs.connid.base.ringcentral.driver.rest;

import com.exclamationlabs.connid.base.connector.driver.DriverInvocator;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralCallQueueAttribute;
import com.exclamationlabs.connid.base.ringcentral.configuration.RingCentralConfiguration;
import com.exclamationlabs.connid.base.ringcentral.model.RingCentralCallQueue;
import com.exclamationlabs.connid.base.ringcentral.model.request.CallQueueBulkAssign;
import com.exclamationlabs.connid.base.ringcentral.model.response.ListCallQueuesResponse;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.util.*;
import java.util.stream.Collectors;

public class RingCentralCallQueueInvocator implements DriverInvocator<RingCentralDriver, RingCentralCallQueue> {

    @Override
    public String create(RingCentralDriver driver, RingCentralCallQueue model) throws ConnectorException {
        throw new ConnectorException("Create of call queue not supported by API");
    }

    @Override
    public void update(RingCentralDriver driver, String queueId, RingCentralCallQueue queue) throws ConnectorException {
        // Update Call Queue members only (not the call queue itself)
        List<String> currentMembers = getCurrentCallQueueMembers(driver, queueId);
        List<String> updatedMembers = queue.getUserMembers();

        List<String> membersToRemove = new ArrayList<>(currentMembers);
        membersToRemove.removeAll(updatedMembers);

        List<String> membersToAdd = new ArrayList<>(updatedMembers);
        membersToAdd.removeAll(currentMembers);

        CallQueueBulkAssign bulkAssign = new CallQueueBulkAssign();
        bulkAssign.setAddedExtensionIds(membersToAdd);
        bulkAssign.setRemovedExtensionIds(membersToRemove);

        try {
            driver.rateLimitCheck("heavy");

            driver.executePostRequest(
                    RingCentralDriver.ACCOUNT_API_PATH + "call-queues/" + queueId +
                            "/bulk-assign", CallQueueBulkAssign.class, bulkAssign).getResponseObject();
        } catch (IllegalArgumentException ill) {
            // Post returns HTTP 204 and no response, which is not supported well by base framework
            // disregard exception
        }

    }

    @Override
    public void delete(RingCentralDriver driver, String userId) throws ConnectorException {
        throw new ConnectorException("Delete of call queue not supported by API");
    }

    @Override
    public Set<RingCentralCallQueue> getAll(RingCentralDriver driver, ResultsFilter filter,
                                             ResultsPaginator paginator, Integer max) throws ConnectorException {
        driver.rateLimitCheck("medium");

        if (StringUtils.equalsIgnoreCase(filter.getAttribute(), RingCentralCallQueueAttribute.USER_MEMBERS.name())) {
            RingCentralConfiguration ringCentralConfiguration = driver.getConfiguration();

            String[] ids = new String[0];
            if (StringUtils.isNotBlank(ringCentralConfiguration.getPreferredCallQueueIds())) {
                ids = StringUtils.split(ringCentralConfiguration.getPreferredCallQueueIds(), ',');
            }

            ListCallQueuesResponse response = driver.executeGetRequest(RingCentralDriver.ACCOUNT_API_PATH +
                            "call-queues?perPage=1000&memberExtensionId="+filter.getValue(), ListCallQueuesResponse.class,
                    Collections.emptyMap()).getResponseObject();

            List<String> idList = Arrays.asList(ids);
            return response.getRecords().stream()
                    .filter(queue -> idList.contains(queue.getIdentityIdValue())).collect(Collectors.toSet());
        } else {
            ListCallQueuesResponse response = driver.executeGetRequest(RingCentralDriver.ACCOUNT_API_PATH +
                            "call-queues?perPage=1000", ListCallQueuesResponse.class,
                    Collections.emptyMap()).getResponseObject();
            return new HashSet<>(response.getRecords());
        }
    }

    @Override
    public RingCentralCallQueue getOne(RingCentralDriver driver, String queueId, Map<String, Object> map) throws ConnectorException {
        driver.rateLimitCheck("light");

        RingCentralCallQueue result = driver.executeGetRequest(
                RingCentralDriver.ACCOUNT_API_PATH + "call-queues/" + queueId,
                RingCentralCallQueue.class, Collections.emptyMap()).getResponseObject();

        // get list of current call queue members
        List<String> members = getCurrentCallQueueMembers(driver, queueId);
        result.setUserMembers(members);

        return result;
    }

    private List<String> getCurrentCallQueueMembers(RingCentralDriver driver, String queueId) {
        List<String> members = new ArrayList<>();

        driver.rateLimitCheck("light");

        ListCallQueuesResponse membersResult = driver.executeGetRequest(
                RingCentralDriver.ACCOUNT_API_PATH + "call-queues/" + queueId + "/members",
                ListCallQueuesResponse.class, Collections.emptyMap()).getResponseObject();
        for (RingCentralCallQueue current : membersResult.getRecords()) {
            members.add(current.getId());
        }

        return members;
    }

}
