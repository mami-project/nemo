/*
 * Copyright (c) 2015 Huawei, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.nemo.user.vnspacemanager.languagestyle.deleteintentlang;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.nemo.user.tenantmanager.TenantManage;
import org.opendaylight.nemo.user.vnspacemanager.languagestyle.NEMOConstants;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.NodeName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.TemplateName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.UserId;

import org.opendaylight.nemo.user.vnspacemanager.structurestyle.deleteintent.*;

public class DeleteTemplateDefinitionLang {
	
	private TenantManage tenantManage;
	private DeleteTemplateDefinition deleteTemplateDefinition;
	public DeleteTemplateDefinitionLang(DataBroker dataBroker, TenantManage tenantManage){
        this.tenantManage = tenantManage;
        deleteTemplateDefinition = new DeleteTemplateDefinition(dataBroker, tenantManage);
    }
	
	 public String DeleteTemplateDefinitionHandling(UserId userId, String definitionName){
		 if (tenantManage.getTempalteDefinition(userId)!=null){
	            if (tenantManage.getTempalteDefinition(userId).containsKey(new TemplateName(definitionName))){
	            	
	            	 return deleteTemplateDefinition.DeleTemplateDefinitionHandling(userId, new TemplateName(definitionName));
	                
	            }
	        }

	        if (tenantManage.getDefinitionDataStore(userId) !=null){
	            if (tenantManage.getDefinitionDataStore(userId).containsKey(new TemplateName(definitionName))){
	            	 return deleteTemplateDefinition.DeleTemplateDefinitionHandling(userId, new TemplateName(definitionName));
	         
	            }
	        }

	         return "Error|The templateDefinition " + definitionName + " does not exist.";
	    }

}

