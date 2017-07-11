/*
 * Copyright (c) 2015 Huawei, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.nemo.user.vnspacemanager.structurestyle.deleteintent;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.nemo.user.tenantmanager.TenantManage;
import org.opendaylight.nemo.user.vnspacemanager.languagestyle.NEMOConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.*;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.UserId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.Connection;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.operations.Operation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.connection.instance.EndNode;
import java.util.*;


public class DeleteConnectionPoint {
	private TenantManage tenantManage;
	
	public DeleteConnectionPoint( TenantManage tenantManage){
        this.tenantManage = tenantManage;
    }

	
		public String DeleteConnectionPointHandling(UserId userId, ConnectionPointId connectionPointId) {
		 Boolean connPointExist = false;

	        if (tenantManage.getConnectionPoint(userId)!=null){
	            if (tenantManage.getConnectionPoint(userId).containsKey(connectionPointId)){
	                connPointExist = true;	
			//System.out.println("Deleting connPoint"+ connectionPointId);
	                tenantManage.getConnectionPoint(userId).remove(connectionPointId);
	                tenantManage.getUserNameIdMap(userId).remove(tenantManage.getName(userId,connectionPointId.getValue()));
			//System.out.println("Deleting connPoint: ConnectionPointMap"+ tenantManage.getConnectionPoint(userId));
	            }
	        }
	        if (tenantManage.getConnectionPointDataStore(userId)!=null){
	            if (tenantManage.getConnectionPointDataStore(userId).containsKey(connectionPointId)){
	                connPointExist = true;
			//System.out.println("Deleting connPointDS"+ connectionPointId);
	                tenantManage.setUserDeleteIntent(userId, NEMOConstants.connectionPoint,connectionPointId.getValue());
	                tenantManage.getUserNameIdMap(userId).remove(tenantManage.getName(userId,connectionPointId.getValue()));
			//System.out.println("Deleting connPoint: ConnectionPointMap"+ tenantManage.getConnectionPointDataStore(userId));
	            }
	        }
	        if (!connPointExist){
	            return "Error|The connectionPoint instance " +connectionPointId.getValue()+" does not exist.";
	        }
	        else {
			List<ConnectionId> deleteConnection = new ArrayList<ConnectionId>();
	            if (tenantManage.getConnection(userId)!=null){
	            	//System.out.println("Inside getConnection");
			
	                for (Connection connection : tenantManage.getConnection(userId).values()){
	                    List<EndNode> endNodeList = connection.getEndNode();
	                    for (EndNode endNode :endNodeList){
	                    	//System.out.println("EndnodeId: " +endNode.getNodeId());
	                    	//System.out.println("connectionPointId: " +connectionPointId);
	                        if (endNode.getNodeId().getValue().equals(connectionPointId.getValue())){
	                        	//System.out.println("Match nodeId connectionId");
					deleteConnection.add(connection.getConnectionId());
	                            break;
	                        }
	                    }
	                }
			Iterator<ConnectionId> itr = deleteConnection.iterator();
			while(itr.hasNext()){
				ConnectionId connId= itr.next();
	                        tenantManage.getConnection(userId).remove(connId);
	                        tenantManage.getUserNameIdMap(userId).remove(tenantManage.getName(userId,connId.getValue()));
			}
			deleteConnection.clear();
	            }
	            if (tenantManage.getConnectionDataStore(userId)!=null){
	                for (Connection connection : tenantManage.getConnectionDataStore(userId).values()){
	                    List<EndNode> endNodeList = connection.getEndNode();
	                    for (EndNode endNode :endNodeList){
	                    	//System.out.println("EndnodeIdDS: " +endNode.getNodeId());
	                    	//System.out.println("connectionPointIdDS: " +connectionPointId);
	                        if (endNode.getNodeId().getValue().equals(connectionPointId.getValue())){
	                        	//System.out.println("Match nodeId connectionId");
					deleteConnection.add(connection.getConnectionId());
	                            break;
	                        }
	                    }
	                }
			Iterator<ConnectionId> itr = deleteConnection.iterator();
			while(itr.hasNext()){
				ConnectionId connId= itr.next();
	                        tenantManage.setUserDeleteIntent(userId,NEMOConstants.connection,connId.getValue());
	                        tenantManage.getUserNameIdMap(userId).remove(tenantManage.getName(userId,connId.getValue()));
			}
				   
	            }
	        }
		
		return null;
	}

}

