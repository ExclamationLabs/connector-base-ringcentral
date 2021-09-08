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
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        configuration.setTestConfiguration();
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
        prepareMockResponse(responseData, responseData2);

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
        prepareMockResponse(responseData, responseData2);

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(RingCentralUserAttribute.GIVEN_NAME.name()).addValue("Johnny").build());

        Uid newId = connector.update(ObjectClass.ACCOUNT, new Uid("1234"), attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
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
        prepareMockResponse(responseData);

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
        prepareMockResponse(responseData);

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
        Set<Attribute> attributes = new HashSet<>();
        connector.update(ObjectClass.GROUP, new Uid("1234"), attributes, new OperationOptionsBuilder().build());
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

    @Test
    public void test390UserDelete() {
        prepareMockResponse();
        connector.delete(ObjectClass.ACCOUNT, new Uid("1234"), new OperationOptionsBuilder().build());
    }


 }