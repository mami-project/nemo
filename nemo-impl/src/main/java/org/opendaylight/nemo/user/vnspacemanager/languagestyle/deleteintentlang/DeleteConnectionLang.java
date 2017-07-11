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
import org.opendaylight.nemo.user.vnspacemanager.structurestyle.deleteintent.DeleteConnection;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.ConnectionId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.UserId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.*;


import java.util.HashMap;
import java.util.Map;




/**
 * Created by z00293636 on 2015/11/6.
 * Updated by ebg on 2017/06/2.
 */
public class DeleteConnectionLang {
    private TenantManage tenantManage;
    private DeleteConnection deleteConnection;

    public DeleteConnectionLang(DataBroker dataBroker, TenantManage tenantManage){
        this.tenantManage = tenantManage;
        deleteConnection = new DeleteConnection(dataBroker, tenantManage);
    }

    public String DeleteConnectionHandling(UserId userId, String connectionName){
        if (tenantManage.getObjectId(userId,connectionName)!=null){
            ConnectionId connectionId = new ConnectionId(tenantManage.getObjectId(userId,connectionName));
            return deleteConnection.DeleteConnectionHandling(userId,connectionId);
        }
        if (tenantManage. getConnectionDataStore(userId) != null){
            Map<ConnectionId, Connection> connectionMap = new HashMap<ConnectionId, Connection>();
            connectionMap = tenantManage.getConnectionDataStore(userId);
            for (Connection i: connectionMap.values()){ 
                if (i.getConnectionName().getValue().equals(connectionName)){
                	return deleteConnection.DeleteConnectionHandling(userId, i.getConnectionId());
                }
            }
        }
            return "TError|he connection " + connectionName + " does not exist.";
        
    }
}
