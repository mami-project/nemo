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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.TemplateInstanceId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.UserId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.Connection;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.object.rev151010.connection.instance.EndNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.ConnectionPointId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.ConnectionId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.NodeName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.TemplateName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.UserId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.ConnectionPoint;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.definitions.TemplateDefinition;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.instances.TemplateInstance;

import org.opendaylight.nemo.user.vnspacemanager.structurestyle.deleteintent.*;

import java.util.*;


/**
 * Created by ebg on 2017/5/31.
 */
public class DeleteTemplateInstance {
   private TenantManage tenantManage;
	private DeleteNode deleteNode;
	private DeleteConnectionPoint deleteConnPoint;
	private DeleteConnection deleteConnection;

    public DeleteTemplateInstance(DataBroker dataBroker,TenantManage tenantManage){
        this.tenantManage = tenantManage;
        deleteNode = new DeleteNode(dataBroker, tenantManage);
	deleteConnPoint = new DeleteConnectionPoint(tenantManage);
	deleteConnection = new DeleteConnection(dataBroker, tenantManage);
  
    }

    public String DeleTemplateInstanceHandling(UserId userId, TemplateInstanceId templateInstanceId, String instanceName) {
        Boolean instanceExist = false;
        String erroInfo = null;
        if (tenantManage.getTemplateInstance(userId) != null){
            if (tenantManage.getTemplateInstance(userId).containsKey(templateInstanceId)){
                instanceExist = true;
                tenantManage.getTemplateInstance(userId).remove(templateInstanceId);
                tenantManage.getUserTemplateInstanceName(userId).remove(instanceName);
                tenantManage.getUserNameIdMap(userId).remove(tenantManage.getName(userId, templateInstanceId.getValue()));
            }
        }
        if (tenantManage.getInstanceDataStore(userId)!=null){
            if (tenantManage.getInstanceDataStore(userId).containsKey(templateInstanceId)){
                instanceExist = true;
                tenantManage.setUserDeleteIntent(userId, NEMOConstants.templateInstance,templateInstanceId.getValue());
                tenantManage.getUserNameIdMap(userId).remove(tenantManage.getName(userId,templateInstanceId.getValue()));
            }
        }
        if (!instanceExist){
            return "Error|The instance " +templateInstanceId.getValue()+" does not exist.";
        }else{
        	erroInfo = nodeInstanceHandling(userId, instanceName);
        	if (erroInfo != null){
        		return erroInfo;
        	}else{
        		erroInfo= connPointInstanceHandling(userId, instanceName);
		if(erroInfo != null){
        		return erroInfo;
        	}else{
        		erroInfo = connectionInstanceHandling(userId, instanceName);
        	}        	
	     }	
        }
        
        
       
        return erroInfo;
    }

    private String nodeInstanceHandling(UserId userId, String nodeName){
		String erroInfo = null;
		Map<NodeId, Node> nodeMap = new HashMap<NodeId, Node>();
		List<NodeId> deleteNodeList = new ArrayList<NodeId>();
		if(tenantManage.getNode(userId) != null){
			nodeMap = tenantManage.getNode(userId);
			for(Node n: nodeMap.values()){
				if(n.getNodeName().getValue().split("\\.")[0].equals(nodeName)){
					deleteNodeList.add(n.getNodeId());
				}
				
			}
			
			if(!deleteNodeList.isEmpty()){
				Iterator<NodeId> itr = deleteNodeList.iterator();
				while(itr.hasNext()){
					NodeId delete = itr.next();
					erroInfo =deleteNode.DeleNodeHandling(userId, delete);
					if(erroInfo != null){
						return erroInfo;
					}
				}
			}
		}
		deleteNodeList.clear();
		Map<NodeId, Node> nodeMapDS = new HashMap<NodeId, Node>();
		if(tenantManage.getNodeDataStore(userId) != null){
			nodeMapDS= tenantManage.getNodeDataStore(userId);
			for(Node n: nodeMapDS.values()){
				if(n.getNodeName().getValue().split("\\.")[0].equals(nodeName)){
					deleteNodeList.add(n.getNodeId());

				}
				
			}
			if(!deleteNodeList.isEmpty()){
				Iterator<NodeId> itr = deleteNodeList.iterator();
				while(itr.hasNext()){
					NodeId delete = itr.next();
					erroInfo =deleteNode.DeleNodeHandling(userId, delete);
					if(erroInfo != null){
						return erroInfo;
					}
				}
			}
			
		}
		return erroInfo;
		}
	
		private String connPointInstanceHandling(UserId userId, String nodeName){
		String erroInfo = null;
		List<ConnectionPointId> deleteCnnPointList = new ArrayList<ConnectionPointId>();
		Map<ConnectionPointId, ConnectionPoint> connPointMap = new HashMap<ConnectionPointId, ConnectionPoint>();
		if(tenantManage.getConnectionPoint(userId) != null){
			
			connPointMap = tenantManage.getConnectionPoint(userId);
			//System.out.println("[connPointInstanceHandling]connPointMap: "+connPointMap);
			for(ConnectionPoint n: connPointMap.values()){
				if(n.getConnectionPointName().getValue().split("\\.")[0].equals(nodeName)){
					//System.out.println("[connPointInstanceHandling]NodeName:"+nodeName);
					//System.out.println("[connPointInstanceHandling]ConnectionPointName:"+n.getConnectionPointName());
					deleteCnnPointList.add( n.getConnectionPointId());
				}
				
			}
			if(!deleteCnnPointList.isEmpty()){
				Iterator<ConnectionPointId> itr = deleteCnnPointList.iterator();
				while(itr.hasNext()){
					ConnectionPointId delete = itr.next();
					//System.out.println("[connPointInstanceHandling]ConnectionPointDelete:"+delete);
					erroInfo = deleteConnPoint.DeleteConnectionPointHandling(userId, delete);
					if(erroInfo != null){
						return erroInfo;
					}
				}
			}
		}
		deleteCnnPointList.clear();
		
		Map<ConnectionPointId, ConnectionPoint> connPointMapDS = new HashMap<ConnectionPointId, ConnectionPoint>();
		if(tenantManage.getConnectionPointDataStore(userId) != null){
			connPointMapDS= tenantManage.getConnectionPointDataStore(userId);
			for(ConnectionPoint c: connPointMapDS.values()){
				if(c.getConnectionPointName().getValue().split("\\.")[0].equals(nodeName)){
					//System.out.println("[connPointInstanceHandling]NodeName:"+nodeName);
					//System.out.println("[connPointInstanceHandling]ConnectionPointNameDS:"+c.getConnectionPointName());
					deleteCnnPointList.add( c.getConnectionPointId());
				}
				
			}
			if(!deleteCnnPointList.isEmpty()){
				Iterator<ConnectionPointId> itr = deleteCnnPointList.iterator();
				while(itr.hasNext()){
					ConnectionPointId delete = itr.next();
					//System.out.println("[connPointInstanceHandling]ConnectionPointDelete:"+delete);
					erroInfo = deleteConnPoint.DeleteConnectionPointHandling(userId, delete);
					if(erroInfo != null){
						return erroInfo;
					}
				}
			}
			
		}
		return erroInfo;
		}

private String connectionInstanceHandling(UserId userId, String nodeName){
		String erroInfo = null;
		List<ConnectionId> deleteConnectionList = new ArrayList<ConnectionId>();
		Map<ConnectionId, Connection> connectionMap = new HashMap<ConnectionId, Connection>();
		if(tenantManage.getConnection(userId) != null){
			
			connectionMap = tenantManage.getConnection(userId);
			//System.out.println("[connectionInstanceHandling]connectionMap: "+connectionMap);
			for(Connection n: connectionMap.values()){
				if(n.getConnectionName().getValue().split("\\.")[0].equals(nodeName)){
					//System.out.println("[connectionInstanceHandling]NodeName:"+nodeName);
					//System.out.println("[connectionInstanceHandling]ConnectionName:"+n.getConnectionName());
					deleteConnectionList.add( n.getConnectionId());
				}
				
			}
			if(!deleteConnectionList.isEmpty()){
				Iterator<ConnectionId> itr = deleteConnectionList.iterator();
				while(itr.hasNext()){
					ConnectionId delete = itr.next();
					//System.out.println("[connPointInstanceHandling]ConnectionDelete:"+delete);
					erroInfo = deleteConnection.DeleteConnectionHandling(userId, delete);
					if(erroInfo != null){
						return erroInfo;
					}
				}
			}
			deleteConnectionList.clear();
		}
		
		
		Map<ConnectionId, Connection> connectionMapDS = new HashMap<ConnectionId, Connection>();
		if(tenantManage.getConnectionDataStore(userId) != null){
			connectionMapDS= tenantManage.getConnectionDataStore(userId);
			for(Connection c: connectionMapDS.values()){
				if(c.getConnectionName().getValue().split("\\.")[0].equals(nodeName)){
					//System.out.println("[connectionInstanceHandling]NodeName:"+nodeName);
					//System.out.println("[connectionInstanceHandling]ConnectionNameDS:"+c.getConnectionName());
					deleteConnectionList.add( c.getConnectionId());
				}
				
			}
			if(!deleteConnectionList.isEmpty()){
				Iterator<ConnectionId> itr = deleteConnectionList.iterator();
				while(itr.hasNext()){
					ConnectionId delete = itr.next();
					//System.out.println("[connectionInstanceHandling]ConnectionDelete:"+delete);
					erroInfo = deleteConnection.DeleteConnectionHandling(userId, delete);
					if(erroInfo != null){
						return erroInfo;
					}
				}
			}
			
		}
		return erroInfo;
		}


	

}
