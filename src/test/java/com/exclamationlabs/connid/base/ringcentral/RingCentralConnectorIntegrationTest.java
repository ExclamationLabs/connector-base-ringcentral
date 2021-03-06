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

import com.exclamationlabs.connid.base.connector.configuration.ConfigurationNameBuilder;
import com.exclamationlabs.connid.base.connector.test.IntegrationTest;
import com.exclamationlabs.connid.base.connector.test.util.ConnectorTestUtils;
import static com.exclamationlabs.connid.base.ringcentral.attribute.RingCentralUserAttribute.*;
import com.exclamationlabs.connid.base.ringcentral.configuration.RingCentralConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.objects.*;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RingCentralConnectorIntegrationTest extends IntegrationTest {

    private RingCentralConnector connector;

    private static String generatedUserId;

    @Override
    public String getConfigurationName() {
        return new ConfigurationNameBuilder().withConnector(() -> "RINGCENTRAL").build();

    }

    @Before
    public void setup() {
        connector = new RingCentralConnector();
        setup(connector, new RingCentralConfiguration(getConfigurationName()));
    }

    @Test
    @Ignore // Ignore test to avoid topping Ring Central rate limits during testing
    public void test050TestMethod() {
        connector.test();
    }

    @Test
    public void test110UserCreate() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(USER_NAME.name()).addValue("jane@dough.com").build());
        attributes.add(new AttributeBuilder().setName(ACTIVE.name()).addValue(Boolean.TRUE).build());
        attributes.add(new AttributeBuilder().setName(FAMILY_NAME.name()).addValue("Dough").build());
        attributes.add(new AttributeBuilder().setName(GIVEN_NAME.name()).addValue("Jane").build());
        attributes.add(new AttributeBuilder().setName(FORMATTED_NAME.name()).addValue("Jane Dough Sr.").build());
        attributes.add(new AttributeBuilder().setName(EMAIL.name()).addValue("jane@dough.com").build());
        attributes.add(new AttributeBuilder().setName(EMAIL_TYPE.name()).addValue("work").build());

        Uid newId = connector.create(ObjectClass.ACCOUNT, attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
        generatedUserId = newId.getUidValue();
    }

    @Test(expected=AlreadyExistsException.class)
    @Ignore // Ignore test to avoid topping Ring Central rate limits during testing
    public void test112UserCreateAlreadyExists() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(USER_NAME.name()).addValue("jane@dough.com").build());
        attributes.add(new AttributeBuilder().setName(ACTIVE.name()).addValue(Boolean.TRUE).build());
        attributes.add(new AttributeBuilder().setName(FAMILY_NAME.name()).addValue("Dough").build());
        attributes.add(new AttributeBuilder().setName(GIVEN_NAME.name()).addValue("Jane").build());
        attributes.add(new AttributeBuilder().setName(FORMATTED_NAME.name()).addValue("Jane Dough Sr.").build());
        attributes.add(new AttributeBuilder().setName(EMAIL.name()).addValue("jane@dough.com").build());
        attributes.add(new AttributeBuilder().setName(EMAIL_TYPE.name()).addValue("work").build());

        connector.create(ObjectClass.ACCOUNT, attributes, new OperationOptionsBuilder().build());
    }

    @Test
    public void test120UserModify() {
        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new AttributeBuilder().setName(FAMILY_NAME.name()).addValue("Dough").build());
        attributes.add(new AttributeBuilder().setName(GIVEN_NAME.name()).addValue("Johnny").build());
        attributes.add(new AttributeBuilder().setName(FORMATTED_NAME.name()).addValue("Johnny Dough Sr.").build());

        Uid newId = connector.update(ObjectClass.ACCOUNT, new Uid(generatedUserId), attributes, new OperationOptionsBuilder().build());
        assertNotNull(newId);
        assertNotNull(newId.getUidValue());
    }

    @Test
    @Ignore // Ignore test to avoid topping Ring Central rate limits during testing
    public void test130UsersGet() {
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
        List<String> idValues = new ArrayList<>();
        List<String> nameValues = new ArrayList<>();
        ResultsHandler resultsHandler = ConnectorTestUtils.buildResultsHandler(idValues, nameValues);

        connector.executeQuery(ObjectClass.ACCOUNT, generatedUserId, resultsHandler, new OperationOptionsBuilder().build());
        assertEquals(1, idValues.size());
        assertTrue(StringUtils.isNotBlank(idValues.get(0)));
    }

    @Test
    public void test390UserDelete() {
        connector.delete(ObjectClass.ACCOUNT, new Uid(generatedUserId), new OperationOptionsBuilder().build());
    }
}
