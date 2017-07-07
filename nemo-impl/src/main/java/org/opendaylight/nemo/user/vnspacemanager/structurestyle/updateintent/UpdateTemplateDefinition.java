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
import org.opendaylight.nemo.user.vnspacemanager.structurestyle.updateintent.UpdateAbstractNodeInstance;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.definitions.TemplateDefinition;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.ConnectionPoint;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.instances.TemplateInstance;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.connection.definitions.ConnectionDefinition;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.connection.point.definitions.ConnectionPointDefinition; //not used but it is imported for the future used
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.connection.instance.EndNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.flow.instance.MatchItem;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.match.item.definitions.MatchItemDefinition; 
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.node.definitions.NodeDefinition;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.node.instance.Property;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.node.instance.SubNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.property.definitions.PropertyDefinition;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.property.instance.property.values.StringValue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.operation.rev151010.action.definitions.ActionDefinition;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.operation.rev151010.condition.instance.ConditionSegment;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.operation.rev151010.condition.parameter.definitions.ConditionParameterDefinition;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.operation.rev151010.operation.instance.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping.AbstractIntents;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping.TemplateParameter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping._abstract.intents._abstract.objects.AbstractConnection;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping._abstract.intents._abstract.objects.AbstractFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping._abstract.intents._abstract.objects.AbstractNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping._abstract.intents._abstract.objects.AbstractConnectionPoint;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping._abstract.intents._abstract.operations.AbstractOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z00293636 on 2015/12/2.
 */
public class UpdateTemplateDefinition {
     private static final Logger LOG = LoggerFactory.getLogger(UpdateTemplateDefinition.class);
    private TenantManage tenantManage;
    private GetDefinitions getDefinitions;
    private UpdateAbstractNodeInstance updateAbstractNodeInstance;

    public UpdateTemplateDefinition(DataBroker dataBroker, TenantManage tenantManage){
        this.tenantManage = tenantManage;
        getDefinitions = new GetDefinitions(dataBroker);
        updateAbstractNodeInstance = new UpdateAbstractNodeInstance(dataBroker,tenantManage);
    }

    public String checkTemplateDefinition(UserId userId, TemplateDefinition templateDefinition){
        String errorInfo = null;
        Boolean templateDefined = false;
        TemplateDefinition templateDefinitionStored = null;

        if (tenantManage.getTempalteDefinition(userId)!=null){
            if (tenantManage.getTempalteDefinition(userId).containsKey(templateDefinition.getTemplateName())){
                templateDefinitionStored = tenantManage.getTempalteDefinition(userId).get(templateDefinition.getTemplateName());
                templateDefined = true;
                
            }
        }

        if (tenantManage.getDefinitionDataStore(userId) !=null){
            if (tenantManage.getDefinitionDataStore(userId).containsKey(templateDefinition.getTemplateName())){
                templateDefinitionStored= tenantManage.getDefinitionDataStore(userId).get(templateDefinition.getTemplateName());
                templateDefined = true;
                
            }
        }
        if (templateDefined){
            errorInfo = checkTemplateDefinitionChanges(templateDefinition, templateDefinitionStored);
            if(errorInfo != null){
                return "Error|A different template with the same name " + templateDefinition.getTemplateName().getValue() + " has already been defined. Reason: "+errorInfo;
            }else{
                return "Warning|The same template " + templateDefinition.getTemplateName().getValue() + " has already been defined.";
            }
            
        }
        else {
            List<TemplateParameter> list = templateDefinition.getTemplateParameter();
            HashMap<ParameterName, TemplateParameter.ParameterValueType> map = new HashMap<ParameterName, TemplateParameter.ParameterValueType>();
            for (TemplateParameter templateParameter : list){
                map.put(templateParameter.getParameterName(), templateParameter.getParameterValueType());
            }
            AbstractIntents abstractIntents = templateDefinition.getAbstractIntents();
            if (abstractIntents.getAbstractObjects()!=null){
                if (abstractIntents.getAbstractObjects().getAbstractNode()!=null){
                    List<AbstractNode> nodeList = abstractIntents.getAbstractObjects().getAbstractNode();
                    Map<NodeId,AbstractNode> nodeMap = new HashMap<NodeId, AbstractNode>();
                    for (AbstractNode node : nodeList){
                        nodeMap.put(node.getNodeId(),node);
                    }
                    for (AbstractNode node : nodeMap.values()){
                        if (node.getSubNode()!=null){
                            for (SubNode subNode : node.getSubNode()){
                                if (!nodeMap.containsKey(subNode.getNodeId())){
                                    return "The sub node is not defined.";
                                }
                            }
                        }
                        errorInfo = checkNodeTemplate(userId, node,map);
                        if (errorInfo!=null){
                            return errorInfo;
                        }
                    }
                }
                if (abstractIntents.getAbstractObjects().getAbstractConnection()!=null){
                    List<AbstractConnection> connectionList = abstractIntents.getAbstractObjects().getAbstractConnection();
                    for (AbstractConnection connection : connectionList){
                        List<EndNode> endNodeList = connection.getEndNode();
                        Map<NodeId,AbstractNode> nodeMap = new HashMap<NodeId, AbstractNode>();
                        Map<ConnectionPointId,AbstractConnectionPoint> connectionPointMap = new HashMap<ConnectionPointId, AbstractConnectionPoint>();
                        Boolean abstNodesExist = false;
                        Boolean abstConnPointExist = false;
                        Boolean abstConnPointInstanceExist = false;
                        if (abstractIntents.getAbstractObjects().getAbstractNode()!=null){
                            abstNodesExist = true;
                            List<AbstractNode> nodeList = abstractIntents.getAbstractObjects().getAbstractNode(); 
                            for (AbstractNode node : nodeList){
                                nodeMap.put(node.getNodeId(),node);
                            }
                        } 
                        if (abstractIntents.getAbstractObjects().getAbstractConnectionPoint() !=null){
                            abstConnPointExist = true;
                            List<AbstractConnectionPoint> connectionPointList = abstractIntents.getAbstractObjects().getAbstractConnectionPoint();
                            for (AbstractConnectionPoint connectionPoint : connectionPointList){
                                connectionPointMap.put(connectionPoint.getConnectionPointId(),connectionPoint);
                            }
                        }
                        if (checkConnPointsInstance(userId) == null){
                            abstConnPointInstanceExist = true;

                        }
                        if (!abstNodesExist && !abstConnPointExist && !abstConnPointInstanceExist){
                            return "There are not end nodes.";
                        }
                        else {
                            for (EndNode endNode : endNodeList){
                                
                                if (!nodeMap.containsKey( new NodeId (endNode.getNodeId().getValue())) && !connectionPointMap.containsKey(new ConnectionPointId (endNode.getNodeId().getValue()))  && (checkEndNodeInstance(userId, endNode) != null)) {
                                    return "The end node doesn't exist.";
                                }
                            }
                        }

                        errorInfo = checkConnectionTemplate(connection, map);
                        if (errorInfo!=null){
                            return errorInfo;
                        }
                    }
                }
                if (abstractIntents.getAbstractObjects().getAbstractFlow()!=null){
                    List<AbstractFlow> flowList = abstractIntents.getAbstractObjects().getAbstractFlow();
                    for (AbstractFlow flow : flowList){
                        errorInfo = checkFlowTemplate(flow,map);
                        if (errorInfo!=null){
                            return errorInfo;
                        }
                    }
                }
                /* There is no need to check the definition of the abstractConnectionPoint so far.However, it is defined for the future use*/
                if (abstractIntents.getAbstractObjects().getAbstractConnectionPoint()!=null){
                    List<AbstractConnectionPoint> connectionPointList = abstractIntents.getAbstractObjects().getAbstractConnectionPoint();
                    for (AbstractConnectionPoint connectionPoint : connectionPointList){
                        errorInfo = checkConnectionPointTemplate(connectionPoint);
                        if (errorInfo!=null){ //is always null
                            return errorInfo;
                        }
                    }
                }
            }
            if (abstractIntents.getAbstractOperations()!=null){
                if (abstractIntents.getAbstractOperations().getAbstractOperation()!=null){
                    List<AbstractOperation> operationList = abstractIntents.getAbstractOperations().getAbstractOperation();
                    for (AbstractOperation operation : operationList){
                        ObjectId objectId = operation.getTargetObject();
                        Boolean targetExist = false;
                        if (abstractIntents.getAbstractObjects()!=null){
                            if (abstractIntents.getAbstractObjects().getAbstractNode()!=null) {
                                List<AbstractNode> nodeList = abstractIntents.getAbstractObjects().getAbstractNode();
                                Map<NodeId, AbstractNode> nodeMap = new HashMap<NodeId, AbstractNode>();
                                for (AbstractNode node : nodeList) {
                                    nodeMap.put(node.getNodeId(), node);
                                }
                                if (nodeMap.containsKey(new NodeId(objectId))){
                                    targetExist = true;
                                }
                            }
                            if (abstractIntents.getAbstractObjects().getAbstractConnection()!=null){
                                List<AbstractConnection> connectionList = abstractIntents.getAbstractObjects().getAbstractConnection();
                                Map<ConnectionId, AbstractConnection> connectionMap = new HashMap<ConnectionId, AbstractConnection>();
                                for (AbstractConnection connection : connectionList){
                                    connectionMap.put(connection.getConnectionId(),connection);
                                }
                                if (connectionMap.containsKey(new ConnectionId(objectId))){
                                    targetExist = true;
                                }
                            }
                            if (abstractIntents.getAbstractObjects().getAbstractFlow()!=null) {
                                List<AbstractFlow> flowList = abstractIntents.getAbstractObjects().getAbstractFlow();
                                Map<FlowId, AbstractFlow> flowMap = new HashMap<FlowId, AbstractFlow>();
                                for (AbstractFlow flow : flowList){
                                    flowMap.put(flow.getFlowId(),flow);
                                }
                                if (flowMap.containsKey(new FlowId(objectId))){
                                    targetExist = true;
                                }
                            }
                        }
                        else {
                            return "The target does not exist.";
                        }

                        if (!targetExist){
                            return "The target does not exist.";
                        }
                        errorInfo = checkOperationTemplate(operation, map);
                        if (errorInfo!=null){
                            return errorInfo;
                        }
                    }
                }
            }
            if (errorInfo == null){
                tenantManage.setTemplateDefinition(userId,templateDefinition.getTemplateName(),templateDefinition);
            }

        }
        return errorInfo;
    }

    private String checkNodeTemplate(UserId userId, AbstractNode node, Map<ParameterName, TemplateParameter.ParameterValueType> parameterValueTypeMap){
        String errorInfo = null;
        Map<NodeType, NodeDefinition> nodeDefinitions = getDefinitions.getNodeDefinition();
        Boolean instanceExist = false;
        String objectId=null;
        String objectId1=null;
        Map<TemplateInstanceId, TemplateInstance> templateInstanceMap = new HashMap<TemplateInstanceId, TemplateInstance>();
        if (nodeDefinitions.containsKey(node.getNodeType())){
            NodeDefinition nodeDefinition = nodeDefinitions.get(node.getNodeType());
            List<PropertyDefinition> propertyDefinitions = nodeDefinition.getPropertyDefinition();
            Map<PropertyName, PropertyDefinition> nodePropertyDefinition = new HashMap<PropertyName, PropertyDefinition>();
            if (propertyDefinitions!=null){
                for (PropertyDefinition propertyDefinition : propertyDefinitions){
                    nodePropertyDefinition.put(propertyDefinition.getPropertyName(),propertyDefinition);
                }
            }

            if (node.getProperty()!=null){
                for (Property property : node.getProperty()){
                    if (nodePropertyDefinition.containsKey(property.getPropertyName())){
                        PropertyDefinition.PropertyValueType type = nodePropertyDefinition.get(property.getPropertyName()).getPropertyValueType();
                       if (type.getIntValue()==0){
                           List<StringValue> stringValues = property.getPropertyValues().getStringValue();
                           for (StringValue stringValue : stringValues){
                                TemplateParameter.ParameterValueType valueType = parameterValueTypeMap.get(new ParameterName(stringValue.getValue()));
                               LOG.info("valueType: "+valueType);
                               if (valueType!=null){
                                   if (type.getIntValue()!= valueType.getIntValue()){
                                       return  "The property " + property.getPropertyName().getValue() + " type is not right.";
                                   }
                               }
                            }
                       }
                    }
                    else {
                        errorInfo = "The property name " + property.getPropertyName().getValue() + " is not defined.";
                        return errorInfo;
                    }
                }
            }
        }
        else {

            if (tenantManage.getTemplateInstance(userId)!=null){
                templateInstanceMap= tenantManage.getTemplateInstance(userId);
                objectId = tenantManage.getObjectId(userId,node.getNodeName().getValue());
                if (objectId != null){
                    if (templateInstanceMap.containsKey(new TemplateInstanceId(objectId))){
                        instanceExist = true;
                    }
                }
            }

            if (tenantManage.getInstanceDataStore(userId) != null){
                templateInstanceMap = tenantManage.getInstanceDataStore(userId);
                for (TemplateInstance i: templateInstanceMap.values()){
                    if (i.getTemplateInstanceName().getValue().equals(node.getNodeName().getValue())){
                        instanceExist = true;
                    }
                }
            }
            
            if(tenantManage.getInstanceNameDataStore(userId) != null){
                if(tenantManage.getInstanceNameDataStore(userId).containsKey(new TemplateInstanceName(node.getNodeName().getValue()))){
                    instanceExist = true;
                }
            }           

            if (!instanceExist){ 

                errorInfo = updateAbstractNodeInstance.abstractNodeHandling(userId, node);
            }
            /*else {
                return "The instance " + node.getNodeName().getValue() + " exists.";
            }*/
        }
        return errorInfo;
        //return instanceExist+" "+templateInstanceMap+" "+objectId+" "+objectId1;
    }

    private String checkConnectionTemplate(AbstractConnection connection,  Map<ParameterName, TemplateParameter.ParameterValueType> parameterValueTypeMap){
        Map<ConnectionType, ConnectionDefinition> connDefinitions = getDefinitions.getConnectionDefinition();
        if (connDefinitions.containsKey(connection.getConnectionType())){
            ConnectionDefinition connectionDefinition = connDefinitions.get(connection.getConnectionType());
            List<PropertyDefinition> propertyDefinitions = connectionDefinition.getPropertyDefinition();
            Map<PropertyName, PropertyDefinition> connPropertyDefinition = new HashMap<PropertyName, PropertyDefinition>();
            if (propertyDefinitions != null){
                for (PropertyDefinition propertyDefinition : propertyDefinitions){
                    connPropertyDefinition.put(propertyDefinition.getPropertyName(),propertyDefinition);
                }
            }

            if (connection.getProperty()!=null){
                for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.connection.instance.Property property : connection.getProperty()){
                    if (connPropertyDefinition.containsKey(property.getPropertyName())){
                        PropertyDefinition.PropertyValueType type = connPropertyDefinition.get(property.getPropertyName()).getPropertyValueType();
                        if (type.getIntValue()==0){
                            List<StringValue> stringValues = property.getPropertyValues().getStringValue();
                            for (StringValue stringValue : stringValues){
                                TemplateParameter.ParameterValueType valueType = parameterValueTypeMap.get(new ParameterName(stringValue.getValue()));
                                if (valueType!=null){
                                    if (type.getIntValue()!= valueType.getIntValue()){
                                        return "The property " + property.getPropertyName().getValue() + " type is not right.";
                                    }
                                }
                            }
                        }
                    }
                    else {
                        return "The property name " + property.getPropertyName().getValue() + " is not defined.";
                    }
                }
            }
        }
        else {
            return "The connection type " + connection.getConnectionType().getValue() + " is not defined.";
        }
        return null;
    }

    private String checkFlowTemplate(AbstractFlow flow, Map<ParameterName, TemplateParameter.ParameterValueType> parameterValueTypeMap){
        Map<MatchItemName, MatchItemDefinition> matchItemDefinitionMap = getDefinitions.getMatchItemDefinition();
        List<MatchItem> matchItemList = flow.getMatchItem();
        for (MatchItem matchItem : matchItemList){
            if (matchItemDefinitionMap.containsKey(matchItem.getMatchItemName())){
                MatchItemDefinition matchItemDefinition = matchItemDefinitionMap.get(matchItem.getMatchItemName());
                MatchItemDefinition.MatchItemValueType type = matchItemDefinition.getMatchItemValueType();

                if (type.getIntValue()==0){
                   String stringValues = matchItem.getMatchItemValue().getStringValue();
                    TemplateParameter.ParameterValueType valueType = parameterValueTypeMap.get(new ParameterName(stringValues));
                    if (valueType!=null){
                        if (type.getIntValue() != valueType.getIntValue()){
                            return "The match item " + stringValues+" type is not right.";
                        }
                    }
                }
            }
            else {
                return "The match item " + matchItem.getMatchItemName().getValue() +" is not defined.";
            }
        }
        return null;
    }

    /* TBD if ConnectionPoint has types, parameters*/
    private String checkConnectionPointTemplate(AbstractConnectionPoint connectionPoint)  {
        return null;     
    }

    private String checkOperationTemplate(AbstractOperation operation, Map<ParameterName, TemplateParameter.ParameterValueType> parameterValueTypeMap){
        Map<ParameterName, ConditionParameterDefinition> conditionParameterDefinitionMap = getDefinitions.getConditionParameterDefinition();
        Map<ActionName, ActionDefinition> actionDefinitionMap = getDefinitions.getActionDefinition();
        List<ConditionSegment> conditionSegmentList = operation.getConditionSegment();
        List<Action> actionList = operation.getAction();
        if (conditionSegmentList!=null){
            for (ConditionSegment conditionSegment : conditionSegmentList){
                if (conditionParameterDefinitionMap.containsKey(conditionSegment.getConditionParameterName())){
                    if (conditionSegment.getConditionParameterTargetValue()!=null){
                        ConditionParameterDefinition definition = conditionParameterDefinitionMap.get(conditionSegment.getConditionParameterName());
                        ConditionParameterDefinition.ParameterValueType type = definition.getParameterValueType();

                        if (type.getIntValue()==0){
                            String valuePrameter = conditionSegment.getConditionParameterTargetValue().getStringValue();
                            TemplateParameter.ParameterValueType valueType = parameterValueTypeMap.get(new ParameterName(valuePrameter));
                            if (valueType!=null){
                                if (type.getIntValue()!=valueType.getIntValue()){
                                    return "The condition " + conditionSegment.getConditionParameterName().getValue() +" type is not right.";
                                }
                            }
                        }
                    }
                }
                else {
                    return "The Condition " + conditionSegment.getConditionParameterName().getValue() + " is not defined.";
                }
            }
        }
        if (actionList!=null){
            for (Action action : actionList){
                if (actionDefinitionMap.containsKey(action.getActionName())){
                    if (action.getParameterValues()!=null){
                        ActionDefinition actionDefinition = actionDefinitionMap.get(action.getActionName());
                        ActionDefinition.ParameterValueType type = actionDefinition.getParameterValueType();

                        if (type.getIntValue()==0){
                            List<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.operation.rev151010.action.instance.parameter.values.StringValue> stringValues = action.getParameterValues().getStringValue();
                            for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.operation.rev151010.action.instance.parameter.values.StringValue stringValue : stringValues){
                                TemplateParameter.ParameterValueType valueType = parameterValueTypeMap.get(new ParameterName(stringValue.getValue()));
                                if (valueType!=null){
                                    if (type.getIntValue()!=valueType.getIntValue()){
                                        return "The action " + action.getActionName().getValue() +" type is not right.";
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    return "The action " + action.getActionName().getValue() + " is not defined.";
                }
            }
        }
        return null;
    }

    private String checkEndNodeInstance(UserId userId, EndNode endNode){
        Boolean endNode_connPointExist = false;
        Boolean endNode_connPointExist1 = false;
        Map<ConnectionPointId, ConnectionPoint> connectionPointMap = new HashMap<ConnectionPointId, ConnectionPoint>();
        if (tenantManage.getConnectionPoint(userId)!=null){
            connectionPointMap= tenantManage.getConnectionPoint(userId);
            if (tenantManage.getConnectionPoint(userId).containsKey(new ConnectionPointId (endNode.getNodeId().getValue()))){
                endNode_connPointExist = true;     
            }
        }

        if (tenantManage.getConnectionPointDataStore(userId)!=null){
            connectionPointMap= tenantManage.getConnectionPointDataStore(userId);
            if (tenantManage.getConnectionPointDataStore(userId).containsKey(new ConnectionPointId(endNode.getNodeId().getValue()))){
                endNode_connPointExist1 = true;  
            }
        }

        if (!endNode_connPointExist && !endNode_connPointExist1){
            return "The endnode "+endNode+" does not exist (UpdateTemplateDefinition- checkConnectionInstance);";      
        }
    
        return null;

    }

    private String checkConnPointsInstance(UserId userId){
       Boolean connPoint = false;
       Boolean connPointDS = false;
       if (tenantManage.getConnectionPoint(userId) != null){
        connPoint=true;
       }
       if (tenantManage.getConnectionPointDataStore(userId) != null){
        connPointDS=true;
       }
       if (!connPoint && !connPointDS){
        return "There are not ConnectionPoints";
       }

       return null;
        
    } 

    private String checkTemplateDefinitionChanges ( TemplateDefinition templateDefinition, TemplateDefinition templateDefinitionStored){
         AbstractIntents abstractIntents = templateDefinition.getAbstractIntents();
         AbstractIntents abstractIntentStored = templateDefinitionStored.getAbstractIntents();
            if (abstractIntents.getAbstractObjects()!=null && abstractIntentStored.getAbstractObjects()!=null){
                if(abstractIntents.getAbstractObjects().getAbstractNode() == null ^ abstractIntentStored.getAbstractObjects().getAbstractNode() ==null){
                    return  "Nodes are not defined";
                }else if (abstractIntents.getAbstractObjects().getAbstractNode()!=null && abstractIntentStored.getAbstractObjects().getAbstractNode()!=null){
                    List<AbstractNode> nodeList = abstractIntents.getAbstractObjects().getAbstractNode();
                    List<AbstractNode> nodeListStored = abstractIntentStored.getAbstractObjects().getAbstractNode();
                    if(nodeList.size() != nodeListStored.size()){
                        return "The number of abstractNodes defined does not match";
                    } else{
                        int nodeCounter = 0;
                        for (int i = 0; i <= nodeList.size()-1; i++ ) {
                            Boolean  nodeExists = false;
                            for (int j = 0; j<= nodeListStored.size()-1; j++ ) {
                                if (nodeList.get(i).getNodeName().getValue().equals(nodeListStored.get(j).getNodeName().getValue()) && nodeList.get(i).getNodeType().getValue().equals(nodeListStored.get(j).getNodeType().getValue())){
                                    nodeExists=true;
                                }
                            }

                            if(nodeExists){
                                nodeCounter++;
                            }
                        }

                        if(nodeCounter != nodeListStored.size()){
                            return "There are one or more Nodes that does not match with the Nodes defined";
                        }
                    }
                }

                if(abstractIntents.getAbstractObjects().getAbstractConnectionPoint() == null ^ abstractIntentStored.getAbstractObjects().getAbstractConnectionPoint() == null){
                    return  "ConnectionPoints are not defined";
                }else if (abstractIntents.getAbstractObjects().getAbstractConnectionPoint()!=null && abstractIntentStored.getAbstractObjects().getAbstractConnectionPoint() !=null){
                    List<AbstractConnectionPoint> connectionPointList = abstractIntents.getAbstractObjects().getAbstractConnectionPoint();
                    List<AbstractConnectionPoint> connectionPointListStored = abstractIntentStored.getAbstractObjects().getAbstractConnectionPoint();
                    if(connectionPointList.size() != connectionPointListStored.size()){
                        return "The number of ConnectionPoints defined does not match";
                    } else{
                        int connPointCounter = 0;
                        for (int i = 0; i <= connectionPointList.size()-1; i++ ) {
                            Boolean  connPointExists = false;
                            for (int j = 0; j<= connectionPointListStored.size()-1; j++ ) {
                                if (connectionPointList.get(i).getConnectionPointName().getValue().equals(connectionPointListStored.get(j).getConnectionPointName().getValue()) ){
                                    
                                    if(connectionPointList.get(i).getVnfdInterfaceName() != null && connectionPointListStored.get(j).getVnfdInterfaceName() != null){
                                        if(connectionPointList.get(i).getConnectionPointName().getValue().equals(connectionPointListStored.get(j).getConnectionPointName().getValue())){
                                            connPointExists=true;
                                        }
                                    }
                                    if(connectionPointList.get(i).getVnfdInterfaceName() == null && connectionPointListStored.get(j).getVnfdInterfaceName() == null){
                                    connPointExists=true;
                                    }
                                }
                            }

                            if(connPointExists){
                                connPointCounter++;
                            }
                        }

                        if(connPointCounter != connectionPointListStored.size()){
                            return "There are one or more ConnectionPoints that does not match with the ConnectionPoints defined";
                        }
                    }
                }


                if(abstractIntents.getAbstractObjects().getAbstractConnection() == null ^ abstractIntentStored.getAbstractObjects().getAbstractConnection() == null){
                    return  "Connections are not defined";
                }else if (abstractIntents.getAbstractObjects().getAbstractConnection()!=null && abstractIntentStored.getAbstractObjects().getAbstractConnection() !=null){
                    List<AbstractConnection> connectionList = abstractIntents.getAbstractObjects().getAbstractConnection();
                    List<AbstractConnection> connectionListStored = abstractIntentStored.getAbstractObjects().getAbstractConnection();
                    if(connectionList.size() != connectionListStored.size()){
                        return "The number of Connections defined does not match";
                    } else{
                
                        int connectionCounter = 0;
                        for (int i = 0; i <= connectionList.size()-1; i++ ) {
                            Boolean  connExists = false;
                            for (int j = 0; j<= connectionListStored.size()-1; j++ ) {
                                if (connectionList.get(i).getConnectionName().getValue().equals(connectionListStored.get(j).getConnectionName().getValue()) && connectionList.get(i).getConnectionType().getValue().equals(connectionListStored.get(j).getConnectionType().getValue())){
                                    connExists = true;
                                }
                            }

                            if(connExists){
                                connectionCounter++;
                            }
                        }

                        if(connectionCounter != connectionListStored.size()){
                            return "There are one or more Connections that does not match with the Connections defined";
                        }
                    }
                }
            }
    return null;

    }

}