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
import org.opendaylight.nemo.user.vnspacemanager.structurestyle.deleteintent.DeleteTemplateInstance;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.TemplateInstanceId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.TemplateInstanceName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.instances.TemplateInstance;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.UserId;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ebg on 2017/05/31.
 */
public class DeleteTemplateInstanceLang {
    private TenantManage tenantManage;
    private DeleteTemplateInstance deleteTemplateInstance;

    public DeleteTemplateInstanceLang(DataBroker dataBroker, TenantManage tenantManage){
        this.tenantManage = tenantManage;
        deleteTemplateInstance = new DeleteTemplateInstance(dataBroker,tenantManage);
    }

    public String DeleteTemplateInstanceHandling(UserId userId, String templateInstanceName){
        if (tenantManage.getObjectId(userId,templateInstanceName)!=null){
            TemplateInstanceId templateInstanceId = new TemplateInstanceId(tenantManage.getObjectId(userId,templateInstanceName));
             return deleteTemplateInstance.DeleTemplateInstanceHandling(userId, templateInstanceId, templateInstanceName);
        }
        if(tenantManage.getInstanceNameDataStore(userId) != null){
            if(tenantManage.getInstanceNameDataStore(userId).containsKey(new TemplateInstanceName(templateInstanceName))){
                TemplateInstance instance = tenantManage.getInstanceNameDataStore(userId).get(new TemplateInstanceName(templateInstanceName));
                TemplateInstanceId templateInstanceId = instance.getTemplateInstanceId();
                 return deleteTemplateInstance.DeleTemplateInstanceHandling(userId, templateInstanceId, templateInstanceName);
            }
        }

        if (tenantManage.getInstanceDataStore(userId) != null){
            Map<TemplateInstanceId, TemplateInstance> templateInstanceMap = new HashMap<TemplateInstanceId, TemplateInstance>();
            templateInstanceMap = tenantManage.getInstanceDataStore(userId);
            for (TemplateInstance i: templateInstanceMap.values()){
                if (i.getTemplateInstanceName().getValue().equals(templateInstanceName)){
                  	return deleteTemplateInstance.DeleTemplateInstanceHandling(userId, i.getTemplateInstanceId(), templateInstanceName);
                }
            }
        }
        
            return "Error|The instance" + templateInstanceName + " does not exist.";
        
    }
}
