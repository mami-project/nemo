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
import org.opendaylight.nemo.user.vnspacemanager.structurestyle.deleteintent.DeleteNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.NodeName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.UserId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.*;

import java.util.*;
/**
 * Created by z00293636 on 2015/11/6.
 */
public class DeleteNodeLang {
    private TenantManage tenantManage;
    private DeleteNode deleteNode;

    public DeleteNodeLang(DataBroker dataBroker, TenantManage tenantManage){
        this.tenantManage = tenantManage;
        deleteNode = new DeleteNode(dataBroker,tenantManage);
    }

     public String DeleteNodeHandling(UserId userId, String nodename){
        if (tenantManage.getObjectId(userId,nodename)!=null){
            NodeId nodeId = new NodeId(tenantManage.getObjectId(userId,nodename));
            return deleteNode.DeleNodeHandling(userId,nodeId);
        }
        if (tenantManage.getNodeDataStore(userId) != null) {
        	Map<NodeId, Node>nodeDSMap = new  HashMap<NodeId, Node>();
        	nodeDSMap= tenantManage.getNodeDataStore(userId);
        	for (Node n: nodeDSMap.values()){
        		if(n.getNodeName().equals(new NodeName(nodename))){
        			return deleteNode.DeleNodeHandling(userId,n.getNodeId());
        		}
        	}
        }
         return "The node " + nodename + " does not exist.";
    }
}
