/*
 * Copyright (c) 2015 Huawei, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.nemo.user.processingmanager;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.nemo.user.vnspacemanager.languagestyle.NEMOConstants;
import org.opendaylight.nemo.user.tenantmanager.TenantManage;
import org.opendaylight.nemo.user.tenantmanager.AAA;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.common.rev151010.ConnectionPointId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.RegisterUserInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.Users;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.CreateVnfdInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.Objects;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.Operations;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.TemplateDefinitions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.TemplateInstances;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.objects.ConnectionPoint;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.definitions.TemplateDefinition;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.definitions.TemplateDefinitionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.instances.TemplateInstance;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.nemo.intent.rev151010.user.intent.template.instances.TemplateInstanceKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import org.opendaylight.nemo.user.processingmanager.VNFDOperations;
import org.opendaylight.nemo.user.processingmanager.VNFDGenerator;
import java.util.*;
import java.io.IOException;


/**
 * Created by ebg on 2017/04/5.
 */
public class VNFDManager {
    
    private TenantManage tenantManage;
    private VNFDOperations vnfdOperations;
    private VNFDGenerator vnfdGenerator;
    public VNFDManager(DataBroker dataBroker, TenantManage tenantManage){
        this.tenantManage = tenantManage;
    vnfdOperations = new VNFDOperations();
    vnfdGenerator = new VNFDGenerator();

    }

    public String generateVNFD(AAA aaa, CreateVnfdInput createVnfdInput) throws IOException{
        String erroInfo = null;
        TemplateInstanceName instance=null;
        String results_path=null;
        Map<NodeId, Node> nodeMap = new HashMap<NodeId, Node>();
        Map<NodeId, Node> nodeDSMap = new HashMap<NodeId, Node>();
        Map<ConnectionId, Connection> connectionMap = new HashMap<ConnectionId, Connection>();
        Map<ConnectionId, Connection> connectionDSMap = new HashMap<ConnectionId, Connection>();
        Map<TemplateName, TemplateDefinition> templateDefinitionMap = new HashMap<TemplateName, TemplateDefinition>(); 
        Map<TemplateName, TemplateDefinition> templateDefinitionDSMap  = new HashMap<TemplateName, TemplateDefinition>();
        Map<TemplateInstanceId, TemplateInstance> templateInstanceMap = new HashMap<TemplateInstanceId, TemplateInstance>();
        Map<TemplateInstanceName, TemplateInstance> templateInstanceNameMap = new HashMap<TemplateInstanceName, TemplateInstance>();
        Map<TemplateInstanceName, TemplateInstance> templateInstanceNameDSMap = new HashMap<TemplateInstanceName, TemplateInstance>();
        Map<TemplateInstanceId, TemplateInstance> templateInstanceDSMap = new HashMap<TemplateInstanceId, TemplateInstance>();
        Map<ConnectionPointId, ConnectionPoint> connectionPointMap = new HashMap<ConnectionPointId, ConnectionPoint>();
        Map<ConnectionPointId, ConnectionPoint> connectionPointDSMap = new HashMap<ConnectionPointId, ConnectionPoint>();
            
        erroInfo = aaa.checkUser(createVnfdInput.getUserId());

        if (erroInfo != null){
            return erroInfo;
        }
        else{

            
            instance = createVnfdInput.getInstanceName();
            results_path = createVnfdInput.getResultsPath();
            if(tenantManage.getNode(createVnfdInput.getUserId()) != null){
                nodeMap=tenantManage.getNode(createVnfdInput.getUserId());
            }

            if(tenantManage.getConnection(createVnfdInput.getUserId()) != null){
                connectionMap = tenantManage.getConnection(createVnfdInput.getUserId());
            }

            if(tenantManage.getConnectionPoint(createVnfdInput.getUserId()) != null){
                connectionPointMap = tenantManage.getConnectionPoint(createVnfdInput.getUserId());
            }

            if(tenantManage.getTempalteDefinition(createVnfdInput.getUserId()) != null){
                templateDefinitionMap = tenantManage.getTempalteDefinition(createVnfdInput.getUserId());
            }

            if(tenantManage.getTemplateInstance(createVnfdInput.getUserId()) != null){  
                templateInstanceMap = tenantManage.getTemplateInstance(createVnfdInput.getUserId());

            }

            if(tenantManage.getUserTemplateInstanceName(createVnfdInput.getUserId()) != null){
                templateInstanceNameMap = tenantManage.getUserTemplateInstanceName(createVnfdInput.getUserId());
            }

            if(tenantManage.getNodeDataStore(createVnfdInput.getUserId()) != null ){
                nodeDSMap=tenantManage.getNodeDataStore(createVnfdInput.getUserId());
            }

            if(tenantManage.getConnectionDataStore(createVnfdInput.getUserId()) != null ){
                connectionDSMap=tenantManage.getConnectionDataStore(createVnfdInput.getUserId());
            }

            if(tenantManage.getConnectionPointDataStore(createVnfdInput.getUserId())!= null){
                connectionPointDSMap=tenantManage.getConnectionPointDataStore(createVnfdInput.getUserId());
            }

            if(tenantManage.getDefinitionDataStore(createVnfdInput.getUserId()) != null ){
                templateDefinitionDSMap=tenantManage.getDefinitionDataStore(createVnfdInput.getUserId());
            }
            
            if(tenantManage. getInstanceDataStore(createVnfdInput.getUserId()) != null){
                templateInstanceDSMap=tenantManage. getInstanceDataStore(createVnfdInput.getUserId());
            }

            if(tenantManage.getInstanceNameDataStore(createVnfdInput.getUserId()) != null){
                templateInstanceNameDSMap = tenantManage.getInstanceNameDataStore(createVnfdInput.getUserId());
            }

        //erroInfo = nodeDSMap+"\n"+connectionDSMap+"\n"+connectionPointDSMap+"\n"+templateDefinitionDSMap+"\n"+templateInstanceDSMap+"\n"+tenantManage.getUsers()+"\nTenant manage \n"+nodeMap+"\n"+connectionMap+"\n"+connectionPointMap+"\n"+templateDefinitionMap+"\n"+templateInstanceMap+"\n"+templateInstanceName; 
            vnfdOperations.clear_vnfdOperations();
	    vnfdGenerator.clear_vnfdGenerator();
	    String templateDefinitionName=null;
            TemplateInstance templateInstance =templateInstanceNameMap.get(instance);
            TemplateInstance templateInstanceDS = templateInstanceNameDSMap.get(instance);
            if(templateInstance != null){
            templateDefinitionName= templateInstance.getTemplateName().getValue();
            }else if (templateInstanceDS != null){
                    templateDefinitionName= templateInstanceDS.getTemplateName().getValue();
            }else{
                erroInfo = "The instance name has not been defined";
                return erroInfo;
            }
            vnfdOperations.setInstanceNodes(instance, nodeMap, nodeDSMap);
            if (vnfdOperations.getInstanceNodes() != null) {
                vnfdOperations.setNodeVnfUri(templateDefinitionMap, templateDefinitionDSMap,
                vnfdOperations.getInstanceNodes(), instance.getValue());
            }
        
            vnfdOperations.setConnectionConnPointsName(connectionMap, connectionDSMap, connectionPointMap, connectionPointDSMap);

            vnfdOperations.setNodeVnfdInterfaces( connectionPointMap,connectionPointDSMap, instance);
        Map<String, Map<String, String>> instanceNodeMap = new LinkedHashMap<String, Map<String,String>>();
            instanceNodeMap = vnfdOperations.getInstanceNodes();
            Map<String, String> nodeNameTypeMap = new HashMap<String, String>();
            if (instanceNodeMap != null){
                nodeNameTypeMap= instanceNodeMap.get(instance);
            }
            Map<String, String> nodeVnfUriMap = vnfdOperations.getNodeVnfUriMap();
            if (nodeVnfUriMap != null){
                for (String nodeName: nodeVnfUriMap.keySet()){
                    String fileName = null;
                    fileName = vnfdGenerator.readUrl(nodeVnfUriMap.get(nodeName));
                    if (fileName != null){
                        System.out.println("nodeName: "+nodeName);
                        vnfdGenerator.readYAML(fileName, vnfdOperations.getNodeVnfdInterfaces(), nodeName);
                    } 
                }
            } else{        
                erroInfo= "There are not URIs defined in the NodeModels";
                return erroInfo;
            }
            vnfdOperations.setConnPointVnfd(connectionPointMap,connectionPointDSMap);
            if(vnfdOperations.getConnectionConnPointsName() != null){
                vnfdGenerator.parseConnections(vnfdOperations.getConnectionConnPointsName(),  instance.getValue());
            }else{
                erroInfo="There has been a problem while matching the connections with their respective connectionPoints";
            }
            vnfdGenerator.setVnfc();
            
            
            if (vnfdOperations.getConnPointVnfdInterface() != null) {
                erroInfo = vnfdGenerator.setVnfdInternalConnections(vnfdOperations.getConnPointVnfdInterface(), instance.getValue(), templateDefinitionName);
                if (erroInfo != null) {
                     return erroInfo;
                    //System.out.println(erroInfo);
                } else {
                    erroInfo = vnfdGenerator.setVnfdExternalConnections(vnfdOperations.getConnPointVnfdInterface(),
                            instance.getValue(), templateDefinitionName);
                    if (erroInfo != null) {
                         return erroInfo;
                        //System.out.println(erroInfo);
                    } else {
                        if(results_path != null){
                            erroInfo = vnfdGenerator.generateVNFD(instance.getValue(), results_path);
                        }
                        else{
                             erroInfo = "The results path has to be introduced";
                            
                        }
                    }
            
                }
            }else{
                erroInfo="There has been a problem while matching the connectionPoints with their respective vnfd interfaces";
            }
           

                    
        return erroInfo;
        }
    }
}
