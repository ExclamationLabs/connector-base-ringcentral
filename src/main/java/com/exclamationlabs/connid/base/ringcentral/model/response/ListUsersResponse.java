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

package com.exclamationlabs.connid.base.ringcentral.model.response;

import com.exclamationlabs.connid.base.ringcentral.model.user.RingCentralUser;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListUsersResponse {

    private Integer totalResults;
    private Integer startIndex;
    private Integer itemsPerPage;

    @SerializedName("Resources")
    private List<RingCentralUser> users;

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public List<RingCentralUser> getUsers() {
        return users;
    }

    public void setUsers(List<RingCentralUser> users) {
        this.users = users;
    }
}
