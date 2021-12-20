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

package com.exclamationlabs.connid.base.ringcentral;

import com.exclamationlabs.connid.base.connector.test.util.ConnectorMockRestTest;
import com.exclamationlabs.connid.base.connector.test.util.ConnectorTestUtils;
import com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralUserAttribute;
import com.exclamationlabs.connid.base.ringcentral.configuration.RingCentralConfiguration;
import com.exclamationlabs.connid.base.ringcentral.driver.rest.RingCentralDriver;
import com.exclamationlabs.connid.base.ringcentral.driver.rest.RingCentralRateLimit;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralCallQueueAttribute.USER_MEMBERS;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class RingCentralConnectorTest extends ConnectorMockRestTest {

    private RingCentralConnector connector;

    @Before
    public void setup() {
        connector = new RingCentralConnector() {
            @Override
            public void init(Configuration configuration) {
                setAuthenticator(null);
                setDriver(new RingCentralDriver() {
                    @Override
                    protected HttpClient createClient() {
                        return stubClient;
                    }
                });
                super.init(configuration);
            }
        };
        RingCentralConfiguration configuration = new RingCentralConfiguration();
        configuration.setServiceUrl("test");
        configuration.setOauth2Username("test");
        configuration.setOauth2Password("test");
        configuration.setTokenUrl("test");
        configuration.setEncodedSecret("test");

        connector.init(configuration);
    }


    @Test
    public void test110UserCreate() {
        final String responseData = "{ \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/281176004/extension/281950004\",\n" +
                "  \"id\" : 281950004,\n" +
                "  \"extensionNumber\" : \"104\",\n" +
                "  \"contact\" : {\n" +
                "    \"firstName\" : \"Jane\",\n" +
                "    \"lastName\" : \"Dough\",\n" +
                "    \"email\" : \"jane@dough.com\",\n" +
                "    \"emailAsLoginName\" : true,\n" +
                "    \"pronouncedName\" : {\n" +
                "      \"type\" : \"Default\",\n" +
                "      \"text\" : \"Jane Dough\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"name\" : \"Jane Dough\",\n" +
                "  \"type\" : \"User\",\n" +
                "  \"status\" : \"NotActivated\",\n" +
                "  \"regionalSettings\" : {\n" +
                "    \"timezone\" : {\n" +
                "      \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/dictionary/timezone/58\",\n" +
                "      \"id\" : \"58\",\n" +
                "      \"name\" : \"US/Pacific\",\n" +
                "      \"description\" : \"Pacific Time (US & Canada)\",\n" +
                "      \"bias\" : \"-480\"\n" +
                "    },\n" +
                "    \"homeCountry\" : {\n" +
                "      \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/dictionary/country/1\",\n" +
                "      \"id\" : \"1\",\n" +
                "      \"name\" : \"United States\",\n" +
                "      \"isoCode\" : \"US\",\n" +
                "      \"callingCode\" : \"1\"\n" +
                "    },\n" +
                "    \"language\" : {\n" +
                "      \"id\" : \"1033\",\n" +
                "      \"name\" : \"English (United States)\",\n" +
                "      \"localeCode\" : \"en-US\"\n" +
                "    },\n" +
                "    \"greetingLanguage\" : {\n" +
                "      \"id\" : \"1033\",\n" +
                "      \"name\" : \"English (United States)\",\n" +
                "      \"localeCode\" : \"en-US\"\n" +
                "    },\n" +
                "    \"formattingLocale\" : {\n" +
                "      \"id\" : \"1033\",\n" +
                "      \"name\" : \"English (United States)\",\n" +
                "      \"localeCode\" : \"en-US\"\n" +
                "    },\n" +
                "    \"timeFormat\" : \"24h\"\n" +
                "  },\n" +
                "  \"setupWizardState\" : \"NotStarted\",\n" +
                "  \"permissions\" : {\n" +
                "    \"admin\" : {\n" +
                "      \"enabled\" : false\n" +
                "    },\n" +
                "    \"internationalCalling\" : {\n" +
                "      \"enabled\" : true\n" +
                "    }\n" +
                "  },\n" +
                "  \"profileImage\" : {\n" +
                "    \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/281176004/extension/281950004/profile-image\"\n" +
                "  },\n" +
                "  \"hidden\" : false\n" +
                "}";

        final String responseData2 = "{\"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:User\", \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" ],\n" +
                "  \"id\" : \"281950004\",\n" +
                "  \"meta\" : {\n" +
                "    \"resourceType\" : \"User\",\n" +
                "    \"created\" : \"2020-07-27T22:06:55.038Z\",\n" +
                "    \"lastModified\" : \"2020-07-27T22:06:55.038Z\",\n" +
                "    \"location\" : \"https://platform.devtest.ringcentral.com/scim/v2/Users/281950004\"\n" +
                "  },\n" +
                "  \"userName\" : \"jane@dough.com\",\n" +
                "  \"name\" : {\n" +
                "    \"formatted\" : \"Jane Dough\",\n" +
                "    \"familyName\" : \"Dough\",\n" +
                "    \"givenName\" : \"Jane\"\n" +
                "  },\n" +
                "  \"active\" : false,\n" +
                "  \"emails\" : [ {\n" +
                "    \"value\" : \"jane@dough.com\",\n" +
                "    \"type\" : \"work\"\n" +
                "  } ],\n" +
                "  \"phoneNumbers\" : [ {\n" +
                "    \"value\" : \"104\",\n" +
                "    \"type\" : \"other\"\n" +
                "  } ],\n" +
                "  \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" : {\n" +
                "    \"department\" : \"Technology\"\n" +
                "  }\n" +
                "}";
        prepareMockResponse(Collections.emptyMap(), responseData, responseData2);

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(RingCentralUserAttribute.USER_NAME.name()).addValue("test@tester.com").build());
        attributes.add(new AttributeBuilder().setName(RingCentralUserAttribute.EMAIL.name()).addValue("test@tester.com").build());

        attributes.add(new AttributeBuilder().setName(RingCentralUserAttribute.GIVEN_NAME.name()).addValue("John").build());
        attributes.add(new AttributeBuilder().setName(RingCentralUserAttribute.FAMILY_NAME.name()).addValue("Doe").build());

        Uid newId = connector.create(ObjectClass.ACCOUNT, attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test
    public void test120UserModify() {
        // modify requires a get prior to execution
        String responseData = "{  \"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:User\", \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" ],\n" +
                "  \"id\" : \"281950004\",\n" +
                "  \"meta\" : {\n" +
                "    \"resourceType\" : \"User\",\n" +
                "    \"created\" : \"2020-07-27T22:06:56.619Z\",\n" +
                "    \"lastModified\" : \"2020-07-27T22:06:56.619Z\",\n" +
                "    \"location\" : \"https://platform.devtest.ringcentral.com/scim/v2/Users/281950004\"\n" +
                "  },\n" +
                "  \"userName\" : \"jane@dough.com\",\n" +
                "  \"name\" : {\n" +
                "    \"formatted\" : \"Jane Dough\",\n" +
                "    \"familyName\" : \"Dough\",\n" +
                "    \"givenName\" : \"Jane\"\n" +
                "  },\n" +
                "  \"active\" : false,\n" +
                "  \"emails\" : [ {\n" +
                "    \"value\" : \"jane@dough.com\",\n" +
                "    \"type\" : \"work\"\n" +
                "  } ],\n" +
                "  \"phoneNumbers\" : [ {\n" +
                "    \"value\" : \"104\",\n" +
                "    \"type\" : \"other\"\n" +
                "  } ],\n" +
                "  \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" : {\n" +
                "    \"department\" : \"Technology\"\n" +
                "  }\n" +
                "}";

        String responseData2 = " { \"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:User\", \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" ],\n" +
                "  \"id\" : \"281953004\",\n" +
                "  \"meta\" : {\n" +
                "    \"resourceType\" : \"User\",\n" +
                "    \"created\" : \"2020-07-27T22:30:23.406Z\",\n" +
                "    \"lastModified\" : \"2020-07-27T22:30:23.406Z\",\n" +
                "    \"location\" : \"https://platform.devtest.ringcentral.com/scim/v2/Users/281953004\"\n" +
                "  },\n" +
                "  \"userName\" : \"jane@dough.com\",\n" +
                "  \"name\" : {\n" +
                "    \"formatted\" : \"Johnny Dough\",\n" +
                "    \"familyName\" : \"Dough\",\n" +
                "    \"givenName\" : \"Johnny\"\n" +
                "  },\n" +
                "  \"active\" : false,\n" +
                "  \"emails\" : [ {\n" +
                "    \"value\" : \"jane@dough.com\",\n" +
                "    \"type\" : \"work\"\n" +
                "  } ],\n" +
                "  \"phoneNumbers\" : [ {\n" +
                "    \"value\" : \"104\",\n" +
                "    \"type\" : \"other\"\n" +
                "  } ],\n" +
                "  \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" : {\n" +
                "    \"department\" : \"Technology\"\n" +
                "  }\n" +
                "}";
        prepareMockResponse(Collections.emptyMap(), responseData, responseData2);

        Set<AttributeDelta> attributes = new HashSet<>();
        attributes.add(new AttributeDeltaBuilder().setName(RingCentralUserAttribute.GIVEN_NAME.name()).
                addValueToReplace("Johnny").build());

        Set<AttributeDelta> response = connector.updateDelta(ObjectClass.ACCOUNT, new Uid("1234"), attributes, new OperationOptionsBuilder().build());
        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    public void test130UsersGet() {
        String responseData = "{\"schemas\" : [ \"urn:ietf:params:scim:api:messages:2.0:ListResponse\" ],\n" +
                "  \"totalResults\" : 4,\n" +
                "  \"startIndex\" : 1,\n" +
                "  \"itemsPerPage\" : 100,\n" +
                "  \"Resources\" : [ {\n" +
                "    \"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:User\" ],\n" +
                "    \"id\" : \"281176004\",\n" +
                "    \"meta\" : {\n" +
                "      \"resourceType\" : \"User\",\n" +
                "      \"created\" : \"2020-07-27T22:06:55.841Z\",\n" +
                "      \"lastModified\" : \"2020-07-27T22:06:55.841Z\",\n" +
                "      \"location\" : \"https://platform.devtest.ringcentral.com/scim/v2/Users/281176004\"\n" +
                "    },\n" +
                "    \"userName\" : \"connectors@exclamationlabs.com\",\n" +
                "    \"name\" : {\n" +
                "      \"formatted\" : \"Mike Neugebauer\",\n" +
                "      \"familyName\" : \"Neugebauer\",\n" +
                "      \"givenName\" : \"Mike\"\n" +
                "    },\n" +
                "    \"active\" : true,\n" +
                "    \"emails\" : [ {\n" +
                "      \"value\" : \"connectors@exclamationlabs.com\",\n" +
                "      \"type\" : \"work\"\n" +
                "    } ]\n" +
                "  }, {\n" +
                "    \"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:User\", \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" ],\n" +
                "    \"id\" : \"281874004\",\n" +
                "    \"meta\" : {\n" +
                "      \"resourceType\" : \"User\",\n" +
                "      \"created\" : \"2020-07-27T22:06:55.842Z\",\n" +
                "      \"lastModified\" : \"2020-07-27T22:06:55.842Z\",\n" +
                "      \"location\" : \"https://platform.devtest.ringcentral.com/scim/v2/Users/281874004\"\n" +
                "    },\n" +
                "    \"userName\" : \"john2@doe.com\",\n" +
                "    \"name\" : {\n" +
                "      \"formatted\" : \"Johnny Doe\",\n" +
                "      \"familyName\" : \"Doe\",\n" +
                "      \"givenName\" : \"Johnny\"\n" +
                "    },\n" +
                "    \"active\" : false,\n" +
                "    \"emails\" : [ {\n" +
                "      \"value\" : \"john2@doe.com\",\n" +
                "      \"type\" : \"work\"\n" +
                "    } ],\n" +
                "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" : {\n" +
                "      \"department\" : \"Technology\"\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:User\", \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" ],\n" +
                "    \"id\" : \"281875004\",\n" +
                "    \"meta\" : {\n" +
                "      \"resourceType\" : \"User\",\n" +
                "      \"created\" : \"2020-07-27T22:06:55.842Z\",\n" +
                "      \"lastModified\" : \"2020-07-27T22:06:55.842Z\",\n" +
                "      \"location\" : \"https://platform.devtest.ringcentral.com/scim/v2/Users/281875004\"\n" +
                "    },\n" +
                "    \"userName\" : \"john@dough.com\",\n" +
                "    \"name\" : {\n" +
                "      \"formatted\" : \"Johnny Dough\",\n" +
                "      \"familyName\" : \"Dough\",\n" +
                "      \"givenName\" : \"Johnny\"\n" +
                "    },\n" +
                "    \"active\" : false,\n" +
                "    \"emails\" : [ {\n" +
                "      \"value\" : \"john@dough.com\",\n" +
                "      \"type\" : \"work\"\n" +
                "    } ],\n" +
                "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" : {\n" +
                "      \"department\" : \"Technology\"\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:User\", \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" ],\n" +
                "    \"id\" : \"281950004\",\n" +
                "    \"meta\" : {\n" +
                "      \"resourceType\" : \"User\",\n" +
                "      \"created\" : \"2020-07-27T22:06:55.842Z\",\n" +
                "      \"lastModified\" : \"2020-07-27T22:06:55.842Z\",\n" +
                "      \"location\" : \"https://platform.devtest.ringcentral.com/scim/v2/Users/281950004\"\n" +
                "    },\n" +
                "    \"userName\" : \"jane@dough.com\",\n" +
                "    \"name\" : {\n" +
                "      \"formatted\" : \"Jane Dough\",\n" +
                "      \"familyName\" : \"Dough\",\n" +
                "      \"givenName\" : \"Jane\"\n" +
                "    },\n" +
                "    \"active\" : false,\n" +
                "    \"emails\" : [ {\n" +
                "      \"value\" : \"jane@dough.com\",\n" +
                "      \"type\" : \"work\"\n" +
                "    } ],\n" +
                "    \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" : {\n" +
                "      \"department\" : \"Technology\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}";
        prepareMockResponse(Collections.emptyMap(), responseData);

        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.ACCOUNT, "", resultsHandler, new OperationOptionsBuilder().build());
        assertTrue(idValues.size() >= 1);
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    }

    @Test
    public void test140UserGet() {
        String responseData = "{  \"schemas\" : [ \"urn:ietf:params:scim:schemas:core:2.0:User\", \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" ],\n" +
                "  \"id\" : \"281950004\",\n" +
                "  \"meta\" : {\n" +
                "    \"resourceType\" : \"User\",\n" +
                "    \"created\" : \"2020-07-27T22:06:56.619Z\",\n" +
                "    \"lastModified\" : \"2020-07-27T22:06:56.619Z\",\n" +
                "    \"location\" : \"https://platform.devtest.ringcentral.com/scim/v2/Users/281950004\"\n" +
                "  },\n" +
                "  \"userName\" : \"jane@dough.com\",\n" +
                "  \"name\" : {\n" +
                "    \"formatted\" : \"Jane Dough\",\n" +
                "    \"familyName\" : \"Dough\",\n" +
                "    \"givenName\" : \"Jane\"\n" +
                "  },\n" +
                "  \"active\" : false,\n" +
                "  \"emails\" : [ {\n" +
                "    \"value\" : \"jane@dough.com\",\n" +
                "    \"type\" : \"work\"\n" +
                "  } ],\n" +
                "  \"phoneNumbers\" : [ {\n" +
                "    \"value\" : \"104\",\n" +
                "    \"type\" : \"other\"\n" +
                "  } ],\n" +
                "  \"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\" : {\n" +
                "    \"department\" : \"Technology\"\n" +
                "  }\n" +
                "}";
        prepareMockResponse(Collections.emptyMap(), responseData);

        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.ACCOUNT, "1234", resultsHandler, new OperationOptionsBuilder().build());
        assertEquals(1, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    }


    @Test(expected=ConnectorException.class)
    public void test210GroupCreate() {
        Set<Attribute> attributes = new HashSet<>();
        connector.create(ObjectClass.GROUP, attributes, new OperationOptionsBuilder().build());
    }

    @Test(expected=ConnectorException.class)
    public void test220GroupModify() {
        Set<AttributeDelta> attributes = new HashSet<>();
        connector.updateDelta(ObjectClass.GROUP, new Uid("1234"), attributes, new OperationOptionsBuilder().build());
    }

    @Test(expected=ConnectorException.class)
    public void test230GroupsGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.GROUP, "", resultsHandler, new OperationOptionsBuilder().build());
    }

    @Test(expected=ConnectorException.class)
    public void test240GroupGet() {
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.GROUP, "1234", resultsHandler, new OperationOptionsBuilder().build());
    }

    @Test(expected=ConnectorException.class)
    public void test290GroupDelete() {
        connector.delete(ObjectClass.GROUP, new Uid("1234"), new OperationOptionsBuilder().build());
    }

    @Test(expected=ConnectorException.class)
    public void test310CallQueueAdd() {
        Set<Attribute> attributes = new HashSet<>();
        connector.create(new ObjectClass("CallQueue"), attributes, new OperationOptionsBuilder().build());
    }


    @Test
    public void test320CallQueueModify() {
        String responseData = "{\n" +
                "  \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/call-queues/703190005/members?page=1&perPage=100\",\n" +
                "  \"records\" : [ {\n" +
                "    \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/extension/298259004\",\n" +
                "    \"id\" : 298259004,\n" +
                "    \"extensionNumber\" : \"102\"\n" +
                "  }, {\n" +
                "    \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/extension/298248004\",\n" +
                "    \"id\" : 298248004,\n" +
                "    \"extensionNumber\" : \"101\"\n" +
                "  } ],\n" +
                "  \"paging\" : {\n" +
                "    \"page\" : 1,\n" +
                "    \"totalPages\" : 1,\n" +
                "    \"perPage\" : 100,\n" +
                "    \"totalElements\" : 2,\n" +
                "    \"pageStart\" : 0,\n" +
                "    \"pageEnd\" : 1\n" +
                "  },\n" +
                "  \"navigation\" : {\n" +
                "    \"firstPage\" : {\n" +
                "      \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/call-queues/703190005/members?page=1&perPage=100\"\n" +
                "    },\n" +
                "    \"lastPage\" : {\n" +
                "      \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/call-queues/703190005/members?page=1&perPage=100\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        prepareMockResponse(Collections.emptyMap(), responseData);

        Set<AttributeDelta> attributes = new HashSet<>();
        attributes.add(new AttributeDeltaBuilder().setName(USER_MEMBERS.name()).
                addValueToReplace("303243004", "298248004").build());

        Set<AttributeDelta> response = connector.updateDelta(new ObjectClass("CallQueue"), new Uid("1234"), attributes, new OperationOptionsBuilder().build());
        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    public void test330CallQueuesGet() {
        String responseData = "{\n" +
                "  \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/call-queues?page=1&perPage=100\",\n" +
                "  \"records\" : [ {\n" +
                "    \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/extension/703190005\",\n" +
                "    \"id\" : \"703190005\",\n" +
                "    \"extensionNumber\" : \"944\",\n" +
                "    \"name\" : \"TestCallQueueGroup\"\n" +
                "  } ],\n" +
                "  \"paging\" : {\n" +
                "    \"page\" : 1,\n" +
                "    \"totalPages\" : 1,\n" +
                "    \"perPage\" : 100,\n" +
                "    \"totalElements\" : 1,\n" +
                "    \"pageStart\" : 0,\n" +
                "    \"pageEnd\" : 0\n" +
                "  },\n" +
                "  \"navigation\" : {\n" +
                "    \"firstPage\" : {\n" +
                "      \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/call-queues?page=1&perPage=100\"\n" +
                "    },\n" +
                "    \"lastPage\" : {\n" +
                "      \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/call-queues?page=1&perPage=100\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        prepareMockResponse(Collections.emptyMap(), responseData);

        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(new ObjectClass("CallQueue"), "", resultsHandler, new OperationOptionsBuilder().build());
        assertTrue(idValues.size() >= 1);
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
        assertTrue(StringUtils.isNotBlank(nameValues.get(0)));
    }

    @Test
    public void test340CallQueueGet() {
        String responseData = "{\n" +
                "  \"id\" : \"703190005\",\n" +
                "  \"name\" : \"TestCallQueueGroup\",\n" +
                "  \"extensionNumber\" : \"944\",\n" +
                "  \"status\" : \"Enabled\"\n" +
                "}";

        String membersData = "{\n" +
                "  \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/call-queues/703190005/members?page=1&perPage=100\",\n" +
                "  \"records\" : [ {\n" +
                "    \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/extension/303243004\",\n" +
                "    \"id\" : 303243004,\n" +
                "    \"extensionNumber\" : \"103\"\n" +
                "  }, {\n" +
                "    \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/extension/298248004\",\n" +
                "    \"id\" : 298248004,\n" +
                "    \"extensionNumber\" : \"101\"\n" +
                "  } ],\n" +
                "  \"paging\" : {\n" +
                "    \"page\" : 1,\n" +
                "    \"totalPages\" : 1,\n" +
                "    \"perPage\" : 100,\n" +
                "    \"totalElements\" : 2,\n" +
                "    \"pageStart\" : 0,\n" +
                "    \"pageEnd\" : 1\n" +
                "  },\n" +
                "  \"navigation\" : {\n" +
                "    \"firstPage\" : {\n" +
                "      \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/call-queues/703190005/members?page=1&perPage=100\"\n" +
                "    },\n" +
                "    \"lastPage\" : {\n" +
                "      \"uri\" : \"https://platform.devtest.ringcentral.com/restapi/v1.0/account/298248004/call-queues/703190005/members?page=1&perPage=100\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        prepareMockResponse(Collections.emptyMap(), responseData, membersData);

        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(new ObjectClass("CallQueue"), "1234", resultsHandler, new OperationOptionsBuilder().build());
        assertEquals(1, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    }

    @Test(expected=ConnectorException.class)
    public void test390CallQueueDelete() {
        connector.delete(new ObjectClass("CallQueue"), new Uid("1234"), new OperationOptionsBuilder().build());
    }

    @Test
    public void test590UserDelete() {
        prepareMockResponse(Collections.emptyMap(), "");
        connector.delete(ObjectClass.ACCOUNT, new Uid("1234"), new OperationOptionsBuilder().build());
    }

 }