/*
 * Copyright (c) 2015 Huawei, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.nemo.user.vnspacemanager.structurestyle.deleteintent;




/**
 * Created by ebg on 2017/5/31.
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.nemo.user.tenantmanager.TenantManage;
import org.opendaylight.nemo.user.vnspacemanager.languagestyle.NEMOConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.ConnectionPointId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.NodeName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.TemplateInstanceId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.TemplateName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.UserId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.ConnectionPoint;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.definitions.TemplateDefinition;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.instances.TemplateInstance;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping.AbstractIntents;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping.TemplateParameter;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping._abstract.intents._abstract.objects.AbstractConnection;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping._abstract.intents._abstract.objects.AbstractFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping._abstract.intents._abstract.objects.AbstractNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping._abstract.intents._abstract.objects.AbstractConnectionPoint;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.template.rev151201.template.definition.grouping._abstract.intents._abstract.operations.AbstractOperation;

import org.opendaylight.nemo.user.vnspacemanager.structurestyle.deleteintent.*;

public class DeleteTemplateDefinition {

    	private TenantManage tenantManage;
	private DeleteTemplateInstance deleteTemplateInstance;

	public DeleteTemplateDefinition(DataBroker dataBroker, TenantManage tenantManage) {
		this.tenantManage = tenantManage;
		deleteTemplateInstance = new DeleteTemplateInstance(dataBroker, tenantManage);
	}

	public String DeleTemplateDefinitionHandling(UserId userId, TemplateName templateName) {
		Boolean templateExist = false;
		Boolean templateDefinitionLocal = false;
		String erroInfo = null;
		TemplateDefinition templateDefinition = null;
		if (tenantManage.getTempalteDefinition(userId) != null) {
			if (tenantManage.getTempalteDefinition(userId).containsKey(templateName)) {
				templateExist = true;
				templateDefinitionLocal = true;
				templateDefinition = tenantManage.getTempalteDefinition(userId).get(templateName);
				// tenantManage.getTempalteDefinition(userId).remove(templateName);
			}
		}

		if (tenantManage.getDefinitionDataStore(userId) != null) {
			if (tenantManage.getDefinitionDataStore(userId).containsKey(templateName)) {
				templateExist = true;
				templateDefinition = tenantManage.getDefinitionDataStore(userId).get(templateName);
				tenantManage.setUserDeleteIntent(userId, NEMOConstants.templateDefinition, templateName.getValue());
			}
		}

		if (!templateExist) {
			return "The template definition " + templateName.getValue() + " does not exist.";
		} else {
			List<String> abstNodeNameList = new LinkedList<String>();
			// Deleting: First level of abstraction
			// Get the names of all the abstractNodes to check later if it is a
			// nodemodel instantiated

			AbstractIntents abstractIntents = templateDefinition.getAbstractIntents();
			if (abstractIntents.getAbstractObjects() != null) {
				if (abstractIntents.getAbstractObjects().getAbstractNode() != null) {
					List<AbstractNode> nodeList = abstractIntents.getAbstractObjects().getAbstractNode();

					for (AbstractNode node : nodeList) {
						abstNodeNameList.add(node.getNodeName().getValue());
					}
				}
			}

			Map<TemplateInstanceId, TemplateInstance> templateInstanceMap = new HashMap<TemplateInstanceId, TemplateInstance>();
			templateInstanceMap = tenantManage.getTemplateInstance(userId);
			
			if (templateInstanceMap != null) {
				Map<String, TemplateInstanceId> deleteInstanceList = new HashMap<String, TemplateInstanceId>();
				//deleting: instance of each abstractNode
				for (String nodeName : abstNodeNameList) {
					for (TemplateInstance instance : templateInstanceMap.values()) {
						if (nodeName.equals(instance.getTemplateInstanceName().getValue())) {
							deleteInstanceList.put(instance.getTemplateInstanceName().getValue(),instance.getTemplateInstanceId());
						}
					}
				}

				for (String instanceName : deleteInstanceList.keySet()) {
					erroInfo = deleteTemplateInstance.DeleTemplateInstanceHandling(userId,deleteInstanceList.get(instanceName), instanceName);
					if (erroInfo != null){
						return erroInfo;
					}
				}
				templateInstanceMap.clear();
			}
			

			templateInstanceMap = tenantManage.getInstanceDataStore(userId);
			if (templateInstanceMap != null) {
				Map<String, TemplateInstanceId> deleteInstanceList = new HashMap<String, TemplateInstanceId>();
				for (String nodeName : abstNodeNameList) {
					for (TemplateInstance instance : templateInstanceMap.values()) {
						if (nodeName.equals(instance.getTemplateInstanceName().getValue())) {
							deleteInstanceList.put(instance.getTemplateInstanceName().getValue(),instance.getTemplateInstanceId());
						}
					}
				}
				
				for (String instanceName : deleteInstanceList.keySet()) {
					erroInfo = deleteTemplateInstance.DeleTemplateInstanceHandling(userId,deleteInstanceList.get(instanceName), instanceName);
					if (erroInfo != null){
						return erroInfo;
					}
				}
				
				
			}

			// Deleting: templateDefinition

			if (templateDefinitionLocal) {
				tenantManage.getTempalteDefinition(userId).remove(templateName);
			}

		}
		return null;
	}

}
    


