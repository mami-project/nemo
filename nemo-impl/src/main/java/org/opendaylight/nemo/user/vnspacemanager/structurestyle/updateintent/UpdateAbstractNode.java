/*
 * Copyright (c) 2015 Huawei, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.nemo.user.vnspacemanager.structurestyle.updateintent;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.nemo.user.tenantmanager.TenantManage;
import org.opendaylight.nemo.user.vnspacemanager.languagestyle.NEMOConstants;
import org.opendaylight.nemo.user.vnspacemanager.languagestyle.updateintentlang.UpdateTemplateInstanceLang;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.instances.TemplateInstanceBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.instances.TemplateInstanceKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.users.User;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.node.definitions.NodeDefinition;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.node.instance.Property;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.node.instance.SubNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.property.definitions.PropertyDefinition;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.property.instance.PropertyValues;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.property.instance.property.values.StringValue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.instance.grouping.TemplateParameter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.instance.grouping.TemplateParameterBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.instance.grouping.TemplateParameterKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.instance.grouping.template.parameter.ParameterValuesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.instance.grouping.template.parameter.parameter.values.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping._abstract.intents._abstract.objects.AbstractNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by e on 2017/03/17.
 */
public class UpdateAbstractNode {
    private TenantManage tenantManage;
    private GetDefinitions getDefinitions;
    private UpdateTemplateInstance updateTemplateInstance;


    public UpdateAbstractNode(DataBroker dataBroker,TenantManage tenantManage){
        this.tenantManage = tenantManage;
        getDefinitions = new GetDefinitions(dataBroker);
        updateTemplateInstance = new UpdateTemplateInstance(dataBroker, tenantManage);
    }


    public String abstractNodeHandling(UserId userId, AbstractNode abstractNode){
        String errorInfo = null;
        boolean nodeModel = false;
        if (tenantManage.getTempalteDefinition(userId)!=null){
            if (tenantManage.getTempalteDefinition(userId).containsKey(new TemplateName(abstractNode.getNodeType().getValue()))){
                nodeModel = true;
            }
        }
        else if (tenantManage.getDefinitionDataStore(userId)!=null){
            if (tenantManage.getDefinitionDataStore(userId).containsKey(new TemplateName(abstractNode.getNodeType().getValue()))){
                nodeModel = true;
            }
        }
        else if (!nodeModel){
            Map<UserId, User> usersMap = tenantManage.getUsers();
            for (User user : usersMap.values()) {
                if (user.getUserRole().getValue().equals(NEMOConstants.admin)) {
                    if (tenantManage.getDefinitionDataStore(user.getUserId()) != null) {
                        if (tenantManage.getDefinitionDataStore(user.getUserId()).containsKey(new TemplateName(abstractNode.getNodeType().getValue()))) {
                            nodeModel = true;
                        }
                    }
                }
            }
        }

        if (nodeModel){
            if (abstractNode.getSubNode()!=null){
                return "Subnodes should not be included in template instance.";
            }
            else {
                TemplateInstanceBuilder builder = new TemplateInstanceBuilder();
                builder.setKey(new TemplateInstanceKey(new TemplateInstanceId(abstractNode.getNodeId().getValue())))
                        .setTemplateInstanceId(new TemplateInstanceId(abstractNode.getNodeId().getValue()))
                        .setTemplateInstanceName(new TemplateInstanceName(abstractNode.getNodeName().getValue()))
                        .setTemplateName(new TemplateName(abstractNode.getNodeType().getValue()));
                if (abstractNode.getProperty()!=null){
                    List<Property> nodeProeprty = abstractNode.getProperty();
                    List<TemplateParameter> parameters = new LinkedList<TemplateParameter>();
                    for (Property property : nodeProeprty){
                        TemplateParameterBuilder parameterBuilder = new TemplateParameterBuilder();
                        parameterBuilder.setKey(new TemplateParameterKey(new ParameterName(property.getPropertyName().getValue())))
                                        .setParameterName(new ParameterName(property.getPropertyName().getValue()));
                        ParameterValuesBuilder valuesBuilder = new ParameterValuesBuilder();
                        List<IntValue> intValueList = new LinkedList<IntValue>();
                        List<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.instance.grouping.template.parameter.parameter.values.StringValue> stringValueList
                                = new LinkedList<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.instance.grouping.template.parameter.parameter.values.StringValue>();
                        RangeValue rangeValue = null;
                        if (property.getPropertyValues().getIntValue()!=null){
                            for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.property.instance.property.values.IntValue intValue : property.getPropertyValues().getIntValue()){
                                IntValueBuilder intValueBuilder = new IntValueBuilder();
                                intValueBuilder.setKey(new IntValueKey(intValue.getOrder(),intValue.getValue()))
                                                .setOrder(intValue.getOrder())
                                                .setValue(intValue.getValue());
                                intValueList.add(intValueBuilder.build());
                            }
                        }
                        if (property.getPropertyValues().getStringValue()!=null){
                            for (StringValue stringValue : property.getPropertyValues().getStringValue()){
                                StringValueBuilder stringValueBuilder = new StringValueBuilder();
                                stringValueBuilder.setKey(new StringValueKey(stringValue.getOrder(),stringValue.getValue()))
                                                  .setOrder(stringValue.getOrder())
                                                  .setValue(stringValue.getValue());
                                stringValueList.add(stringValueBuilder.build());
                            }
                        }
                        if (property.getPropertyValues().getRangeValue()!=null){
                            RangeValueBuilder rangeValueBuilder = new RangeValueBuilder();
                            rangeValueBuilder.setMin(property.getPropertyValues().getRangeValue().getMin())
                                             .setMax(property.getPropertyValues().getRangeValue().getMax());
                            rangeValue = rangeValueBuilder.build();
                        }
                        valuesBuilder.setIntValue(intValueList).setStringValue(stringValueList).setRangeValue(rangeValue);
                        parameterBuilder.setParameterValues(valuesBuilder.build());
                        parameters.add(parameterBuilder.build());
                    }
                    builder.setTemplateParameter(parameters);
                }

                errorInfo = updateTemplateInstance.checkTemplateInstance(userId,builder.build());
            }
        }
        else {
            errorInfo = "The abstractNode does not have Template Definition:"+abstractNode.getNodeType().getValue();
        }
        return errorInfo;
    }

}
