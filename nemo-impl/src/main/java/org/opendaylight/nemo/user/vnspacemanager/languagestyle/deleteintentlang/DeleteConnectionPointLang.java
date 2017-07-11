/*
 * Copyright (c) 2015 Huawei, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.nemo.user.vnspacemanager.languagestyle.deleteintentlang;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.nemo.user.vnspacemanager.structurestyle.deleteintent.DeleteConnectionPoint;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.nemo.user.tenantmanager.TenantManage;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.*;

import org.opendaylight.nemo.user.vnspacemanager.languagestyle.NEMOConstants;;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.UserId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.Connection;


public class DeleteConnectionPointLang {
	
	private TenantManage tenantManage;
    private DeleteConnectionPoint deleteConnectionPoint;

    public DeleteConnectionPointLang (DataBroker dataBroker, TenantManage tenantManage){
        this.tenantManage = tenantManage;
        deleteConnectionPoint= new DeleteConnectionPoint(tenantManage);
    }

    public String DeleteConnectionPointHandling(UserId userId, String connPointName){
        if (tenantManage.getObjectId(userId,connPointName)!=null){
            ConnectionPointId connPointId = new ConnectionPointId(tenantManage.getObjectId(userId,connPointName));
            return deleteConnectionPoint.DeleteConnectionPointHandling(userId, connPointId);
        }
        if(tenantManage.getConnectionPointNameDataStore(userId) != null){
            if(tenantManage.getConnectionPointNameDataStore(userId).containsKey(new ConnectionPointName(connPointName))){
            	ConnectionPoint connPoint = tenantManage.getConnectionPointNameDataStore(userId).get(new ConnectionPointName(connPointName));
            	ConnectionPointId connPointId = connPoint.getConnectionPointId();
            	return deleteConnectionPoint.DeleteConnectionPointHandling(userId, connPointId);
            }
        }

        if (tenantManage. getConnectionPointDataStore(userId) != null){
            Map<ConnectionPointId, ConnectionPoint> connectionPointMap = new HashMap<ConnectionPointId, ConnectionPoint>();
            connectionPointMap = tenantManage.getConnectionPointDataStore(userId);
            for (ConnectionPoint i: connectionPointMap.values()){
                if (i.getConnectionPointName().getValue().equals(connPointName)){
                	return deleteConnectionPoint.DeleteConnectionPointHandling(userId, i.getConnectionPointId());
                }
            }
        }
        
        return "Error|The connectionPoint" + connPointName + " does not exist.";
       
    }

}

