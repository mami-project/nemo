/*
 * Copyright (c) 2015 Huawei, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.nemo.user.processingmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

public class VNFDGenerator {
	static final List<String> PARAMS = Arrays.asList("name", "description", "internal-connections",
			"external-connections", "VNFC");
	//individual YAMLs variables
	private String name_value = null;
	private String description_value = null;
	private List<Map<String, String>> extConnValue; // extracted from VNFD: external-connections
	private List<Map<String, Object>> int_conn_value; // extracted  from VNFD: internal-connections
	
	//Auxiliary variables
	private Map<String,Map<String, Map<String,String>>> nodeNameTypeConnPointMap; // <VNFDName, Map<localIfaceName, Map<"type",faceType// "vnfc", VNFC>>
	private Map<String, List<Map<String, Object>>> nodeVnfcMap; // <VNFDName, Map<VNFC Extracted  from VNFD part>
	private Map<String, String> nodeVnfdNameMap; // <nodeName, VNFDName>
	private Map<String, List<String>> nodeVnfcNameMap ; // <nodeName, VNFCName>
	private Map<String, List<String>> internalConnectionsMap; // <connectionName,<connPointsNames>>
	private Map<String, List<String>> externalConnectionsMap; // <connectionName, <connPointsNames>>
	private Map<String, List<Map<String, Object>>> nodeInternalConnMap;
	private Map<String, Map<String,String>> vmSubstituteMap; // <nodeName, Map<vnfcOldName, vnfcNewName>>
	
	//final yaml variables
	private List<Map<String, Object>> vnfc ;
	private List<Map<String, Object>> intConns;
	private List<Map<String, Object>> extConns;
	
	public VNFDGenerator() {
		extConnValue = new ArrayList<Map<String, String>>(); 
		int_conn_value = new ArrayList<Map<String, Object>>();
		nodeNameTypeConnPointMap = new HashMap<String,Map<String, Map<String,String>> >();
		nodeVnfcMap = new HashMap<String, List<Map<String, Object>>>();
		nodeVnfdNameMap = new HashMap<String, String>();
		nodeVnfcNameMap = new HashMap<String, List<String>>();
		internalConnectionsMap = new HashMap<String, List<String>>();
		externalConnectionsMap = new HashMap<String, List<String>>();
		nodeInternalConnMap = new HashMap<String, List<Map<String, Object>>>();
		vmSubstituteMap = new HashMap<String, Map<String,String>>();
		vnfc = new ArrayList<Map<String, Object>>();
		intConns = new LinkedList<Map<String,Object>>();
		extConns = new LinkedList<Map<String,Object>>();

	}

	public String readUrl(String vnfUri) throws IOException {

		URL website = new URL(vnfUri);
		String file = null;
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		file = "vnf.yaml";
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		return file;
	}

	//@SuppressWarnings("unchecked")
	public void readYAML(String file, Map<String, List<String>> nodeVnfdInterfacesMap, String nodeName)
			throws IOException {
		
		System.out.println("extConnValue: "+extConnValue);
		System.out.println("int_conn_value"+int_conn_value);
		System.out.println("nodeNameTypeConnPointMap"+nodeNameTypeConnPointMap);
		System.out.println("nodeVnfcMap"+nodeVnfcMap);
		System.out.println("nodeVnfdNameMap"+nodeVnfdNameMap);
		System.out.println("nodeVnfcNameMap)"+nodeVnfcNameMap);
		System.out.println("internalConnectionsMap"+internalConnectionsMap);
		System.out.println("externalConnectionsMap"+externalConnectionsMap);
		System.out.println("nodeInternalConnMap"+nodeInternalConnMap);
		System.out.println("vmSubstituteMap"+vmSubstituteMap);
		System.out.println("vnfc"+vnfc);
		System.out.println("intConns"+intConns);
		System.out.println("extConns"+extConns);

		Map<String, Map<String,String>> nameTypeMap = new HashMap<String, Map<String,String>>();
		InputStream input;
		try {
			input = new FileInputStream(new File(file));
			Yaml yaml = new Yaml();
			

			for (Object data : yaml.loadAll(input)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map = (Map<String, Object>) data;
				// testDumpWriter(map);
				Set<String> keys = map.keySet();
				for (String a : keys) {

					// System.out.println(a);
					Map<String, Object> vnf = (Map<String, Object>) map.get(a);
					for (String p : vnf.keySet()) {
						if (p.contentEquals(PARAMS.get(0))) {
							// System.out.println(vnf.get(p).getClass());
							// //java.lang.String
							name_value = (String) vnf.get(p);
							nodeVnfdNameMap.put(nodeName, name_value);
							System.out.println(name_value);
							System.out.println(nodeVnfdNameMap);
						}
						if (p.contentEquals(PARAMS.get(1))) {
							// System.out.println(vnf.get(p).getClass());//java.lang.String
							description_value = (String) vnf.get(p);
							System.out.println(description_value);
						}
						if (p.contentEquals(PARAMS.get(2))) {
							// System.out.println(vnf.get(p).getClass());
							// //class java.util.ArrayList
							int_conn_value = (List<Map<String, Object>>) vnf.get(p);
							nodeInternalConnMap.put(nodeName, int_conn_value);
							System.out.println("nodeInternalConnMap: " + nodeInternalConnMap);
						}
						if (p.contentEquals(PARAMS.get(3))) {
							// System.out.println(vnf.get(p).getClass());
							// //class java.util.ArrayList
							extConnValue = (List<Map<String, String>>) vnf.get(p);
							nameTypeMap = externalConnections(extConnValue, name_value, nodeVnfdInterfacesMap,
									nodeName);
							nodeNameTypeConnPointMap.put(nodeName, nameTypeMap);
							System.out.println("nodeNameTypeConnPointMap "+nodeNameTypeConnPointMap);
						}

						if (p.contentEquals(PARAMS.get(4))) {
							// System.out.println(vnf.get(p).getClass());
							// //class java.util.ArrayList
							List<Map<String, Object>> vnfcList = new ArrayList<Map<String, Object>>();
							Map<String, Object> aux = new HashMap<String, Object>();
							vnfcList = ((List<Map<String, Object>>) vnf.get(p));
							nodeVnfcMap.put(nodeName, vnfcList);
							System.out.println("nodeVnfcMap: " + nodeVnfcMap);
							Iterator iterator = vnfcList.iterator();
							List<String> vnfcNameList = new ArrayList<String>();
							while (iterator.hasNext()) {
								aux = (Map<String, Object>) iterator.next();
								vnfcNameList.add((String) aux.get("name"));
							}

							nodeVnfcNameMap.put(nodeName, vnfcNameList);
							System.out.println("nodeVnfcNameMap: " + nodeVnfcNameMap);

						}
					}

				}
			}

			input.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public void dumpWriter(Object data, String instanceName, String results_path) throws IOException {

	    
	    DumperOptions options = new DumperOptions();
	    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	    options.setExplicitStart(true);
		String path2 = System.getProperty("user.dir");
		System.out.println(path2);
	    Yaml yaml = new Yaml(options);
		StringWriter writer = new StringWriter();
		yaml.dump(data, writer);
		System.out.println(writer.toString());
		FileWriter fw = new FileWriter(new File(results_path, instanceName+".yaml"));
		fw.write(writer.toString());
		fw.close();
	
		
	}

	public Map<String, Map<String,String>> externalConnections(List<Map<String, String>> extConnValue, String vnfName,
			Map<String, List<String>> nodeVnfdInterfacesMap, String nodeName) {

		Map<String, Map<String,String>> nameTypeMap = new HashMap<String, Map<String,String>>();
		for (String node : nodeVnfdInterfacesMap.keySet()) {
			if (vnfName != null && nodeName.equals(node)) {
				// System.out.println("vnfName coincide"+vnfName);
				for (int i = 0; i < extConnValue.size(); i++) {
					for (String interfaceNode : nodeVnfdInterfacesMap.get(node)) {
						if (interfaceNode.split("\\.").length == 2) {
							if (extConnValue.get(i).get("local_iface_name").equals(interfaceNode.split("\\.")[0]) && extConnValue.get(i).get("VNFC").toLowerCase().equals(interfaceNode.split("\\.")[1].toLowerCase())) {

								Map<String, String> map = new HashMap<String, String>();
								map.put("type", extConnValue.get(i).get("type"));
								map.put("vnfc", extConnValue.get(i).get("VNFC"));

								nameTypeMap.put(interfaceNode, map);
							}
						}
							
						else {
							if (extConnValue.get(i).get("local_iface_name").equals(interfaceNode)) {

								Map<String, String> map = new HashMap<String, String>();
								map.put("type", extConnValue.get(i).get("type"));
								map.put("vnfc", extConnValue.get(i).get("VNFC"));

								nameTypeMap.put(interfaceNode, map);
							}
						}
					}
				}
			}
		}
		return nameTypeMap;
	}

/*
 * Get the maps which contains the external and internal connections that
 * had been defined in NodeModels
 */
	
	public void parseConnections(Map<String, LinkedList<String>> connectionConnPointMap, String instanceName) {
		System.out.println("connectionConnPointMap" + connectionConnPointMap);
		Map<String, List<String>> instanceConnectionsMap = new HashMap<String, List<String>>();

		for (String c : connectionConnPointMap.keySet()) {
			if (c.split("\\.")[0].equals(instanceName)) {
				instanceConnectionsMap.put(c, connectionConnPointMap.get(c));
			}
		}
		System.out.println("instanceConnectionsMap: " + instanceConnectionsMap);

		for (String connName : instanceConnectionsMap.keySet()) {
			List<String> endNodes = new ArrayList<String>();
			endNodes = instanceConnectionsMap.get(connName);
			Boolean external = false;
			for (String endNode : endNodes) {
				if (endNode.split("\\.")[0].equals(instanceName)) {
					externalConnectionsMap.put(connName, endNodes);
					external = true;

				}
			}
			if (!external) {
				internalConnectionsMap.put(connName, endNodes);
			}

		}
		// Internal connection management

		for (String connection : connectionConnPointMap.keySet()) {
			setFinalConnectionPoints(connection, internalConnectionsMap, instanceName, connectionConnPointMap);
		}

		// External connection management
		for (String connection : connectionConnPointMap.keySet()) {
			setFinalConnectionPoints(connection, externalConnectionsMap, instanceName, connectionConnPointMap);
		}
		System.out.println("externalConnectionsMap: " + externalConnectionsMap);
		System.out.println("internalConnectionsMap: " + internalConnectionsMap);
	}
	

	/* 
	 * Replacing the Nodemodel's connectionPoints with the final connectionPoints
	 */
	
	private void setFinalConnectionPoints(String connection, Map<String, List<String>> mapConnections,
			String instanceName, Map<String, LinkedList<String>> connectionConnPointMap) {
		for (String intConn : mapConnections.keySet()) {
			// System.out.println(intConn);
			// Comparing with the other nodeModels
			if (!connection.split("\\.")[0].equals(instanceName)) {
				// Use iterator to access to the contents of connections
				ListIterator<String> itr = mapConnections.get(intConn).listIterator();
				while (itr.hasNext()) {
					Object element = itr.next();
					String connFirst = connectionConnPointMap.get(connection).getFirst();
					String connLast = connectionConnPointMap.get(connection).getLast();
					if (element.equals(connFirst)) {
						itr.set(connLast);
					}

					if (element.equals(connLast)) {
						itr.set(connFirst);
					}

				}
			}
		}
	}
	
	/*
	 * Generate the VNFC section for the VNFD.yaml
	 */
	
	public void setVnfc(){
		
		// Rename the VirtualMachines
		int i = 1;
		for(String nodeName: nodeVnfcNameMap.keySet()){
			Map<String, String> aux = new HashMap<String, String>();
			for(String oldName: nodeVnfcNameMap.get(nodeName)){
				int index=+i;
				String newName= "VirtualMachine-"+index;
				aux.put(oldName,newName);
				i++;
			}
			vmSubstituteMap.put(nodeName, aux);	
		}
		System.out.println("vmSubstituteMap: "+vmSubstituteMap);
		
		//Obtain the final VNFC map, renaming the names of the VirtualMachines
		for(String nodeName:vmSubstituteMap.keySet() ){
			 List<Map<String, Object>> vnfcElementList= nodeVnfcMap.get(nodeName);
			 for(Map<String, Object> map: vnfcElementList){
				String vmOldName=null;
				vmOldName= (String) map.get("name");
				map.put("name", vmSubstituteMap.get(nodeName).get(vmOldName));
				
			 }
		}
		//System.out.println("nodeVnfcMap: "+nodeVnfcMap);
		
		//Adding all the vnfcs
		for(String nodeName: nodeVnfcMap.keySet()){
			vnfc.addAll(nodeVnfcMap.get(nodeName));
		}
		
		System.out.println("vnfc: "+vnfc);
	}
	
	
	/*
	 * Generating the Internal connections section for the VNFD.yaml
	 */
	public String setVnfdInternalConnections(Map<String, String> connPointVnfdInterfaceMap, String instanceName, String templateDefinitionName){
		String errorInfo = null;
		//First, replace VMName with the one allocated for the final VNFD
		for(String nodeName:vmSubstituteMap.keySet() ){
			Map<String, Map<String,String>> ifaceDetailsMap= nodeNameTypeConnPointMap.get(nodeName);
			for(Map<String,String> detailsMap: ifaceDetailsMap.values()){
				String oldName= detailsMap.get("vnfc");
				if (vmSubstituteMap.get(nodeName).get(oldName) != null){
					detailsMap.put("vnfc",vmSubstituteMap.get(nodeName).get(oldName));
				}
				
			}
			System.out.println( nodeInternalConnMap);
			System.out.println(nodeName);
			List<Map<String, Object>> internalConnFromVNFDs= nodeInternalConnMap.get(nodeName);
			System.out.println(internalConnFromVNFDs);
			if(internalConnFromVNFDs != null){
				Iterator<Map<String, Object>> itr = internalConnFromVNFDs.iterator();
				while (itr.hasNext()) {
					Map<String, Object> internalConnDescription = itr.next();
					List<Map<String, String>> elements = (List<Map<String, String>>) internalConnDescription.get("elements");
					for (Map<String, String> elementsMap : elements) {
						String oldName = elementsMap.get("VNFC");
						if (vmSubstituteMap.get(nodeName).get(oldName) != null) {
							elementsMap.put("VNFC", vmSubstituteMap.get(nodeName).get(oldName));
						}
					}
				}
				intConns.addAll(internalConnFromVNFDs);	
			}
			
			
		}
		System.out.println("nodeInternalConnMap"+nodeInternalConnMap);
		
		System.out.println(nodeNameTypeConnPointMap);
		
		//Obtaining the values for each of the params that a internal connection has.
		for (String c : internalConnectionsMap.keySet()) {

			List<Map<String, Object>> elements = new LinkedList<Map<String, Object>>();
			Iterator<String> itr = internalConnectionsMap.get(c).iterator();
			System.out.println("internalConnectionsMap" + internalConnectionsMap);
			LinkedList<String> types = new LinkedList<String>();
			while (itr.hasNext()) {
				String connectionPoint = null;
				connectionPoint = (String) itr.next();
				System.out.println(connectionPoint);
				String iface = null;
				iface = connPointVnfdInterfaceMap.get(connectionPoint);
				System.out.println(iface);
				Map<String, Object> elementDetail = new LinkedHashMap<String, Object>();
				Map<String, String> aux = new LinkedHashMap<String, String>();
			  if( nodeNameTypeConnPointMap.get(connectionPoint.split("\\.")[0]) != null){
				aux = nodeNameTypeConnPointMap.get(connectionPoint.split("\\.")[0]).get(iface);
				System.out.println("aux:" + aux);

				if (aux != null) {
					System.out.println("entrando dentro de aux!=null");
					types.add(aux.get("type"));
					System.out.println("type" + types);
					elementDetail.put("VNFC", aux.get("vnfc"));
					System.out.println("elementDetail" + elementDetail);
					if (iface.split("\\.").length == 2) {
						elementDetail.put("local_iface_name", iface.split("\\.")[0]);
					} else {
						elementDetail.put("local_iface_name", iface);
					}
					elements.add(elementDetail);
					System.out.println("elements" + elements);
				} else {
					errorInfo = "The vnfdInterface " + iface + " from node " + connectionPoint.split("\\.")[0]
							+ " does not match with any local_iface_name value.";
					return errorInfo;
				}
			} else{
					errorInfo = "The node "+connectionPoint.split("\\.")[0]+" does not exist.";
					return errorInfo;
				}
			}
			System.out.println(types + "" + elements);
			if (!types.getFirst().equals(types.getLast())) {
				errorInfo = "The types "+types+" of the interfaces do not match. Details about the interfaces: "+elements+" Connection Details: "+ internalConnectionsMap.get(c);
				System.out.println(errorInfo);
				return errorInfo;
			} else {

				intConns.add(generateInternalConnection(c, types.getFirst(), instanceName, templateDefinitionName,
						elements));

			}
			System.out.println("intConns " + intConns);
			// generateInternalConnection(c,)
		}
	
		
		return errorInfo;
		}
	
	/*
	 * Generating the structure of each internal connection
	 */
	private Map<String,Object> generateInternalConnection(String connName, String type, String instanceName, String templateDefinitionName, List<Map<String, Object>> elements){
		Map<String,Object> internalMap = new LinkedHashMap<String, Object>();
		String finalName = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		//System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
		String description = "\"Created by VIBNEMO translator from source "+instanceName+", template "+templateDefinitionName+" on "+ dateFormat.format(date)+"\"";
		finalName = connName.replace(".", "-");
		internalMap.put("name", finalName);
		internalMap.put("description", description);
		internalMap.put("type", type);
		internalMap.put("elements", elements);

		//System.out.println("internalMap "+internalMap);
		
		
		return internalMap;
	}

	
	/*
	 * Generating the Internal connections section for the VNFD.yaml
	 */
	public String setVnfdExternalConnections(Map<String, String> connPointVnfdInterfaceMap, String instanceName, String templateDefinitionName){
		String errorInfo = null;

		
		//Obtaining the values for each of the params that a internal connection has.
		for (String c : externalConnectionsMap.keySet()) {
			;
			String vnfc = null;
			String localIface = null;
			Iterator<String> itr = externalConnectionsMap.get(c).iterator();
			LinkedList<String> types = new LinkedList<String>();
			while (itr.hasNext()) {
				String connectionPoint = null;
				connectionPoint = (String) itr.next();
				if (!connectionPoint.split("\\.")[0].equals(instanceName)) {
					// System.out.println(connectionPoint.split("\\.")[0]);
					String iface = null;
					iface = connPointVnfdInterfaceMap.get(connectionPoint);
					System.out.println(iface);
					Map<String, String> aux = new LinkedHashMap<String, String>();
					// System.out.println(connectionPoint.split("\\.")[0]);
				  if( nodeNameTypeConnPointMap.get(connectionPoint.split("\\.")[0]) != null){
					aux = nodeNameTypeConnPointMap.get(connectionPoint.split("\\.")[0]).get(iface);
					if (aux != null){
						System.out.println(aux);
						types.add(aux.get("type"));
						vnfc = aux.get("vnfc");
						localIface = iface;
					}
					else{
						errorInfo = "The vnfdInterface " + iface + " from node " + connectionPoint.split("\\.")[0]+ " does not match with any local_iface_name value.";
						//System.out.println(errorInfo);
						return errorInfo;
					}
					}else{
						errorInfo = "The node "+connectionPoint.split("\\.")[0]+" does not exist.";
						return errorInfo;
					}
					
						
				}
			}

			extConns.add(generateExternalConnection(c, types.getFirst(), instanceName, templateDefinitionName, vnfc,
					localIface));

			// generateInternalConnection(c,)
		}
		
		System.out.println("extConns "+ extConns);
		return errorInfo;
	}
	
	
	/*
	 * Generating the structure of each external connection
	 */
	private Map<String,Object> generateExternalConnection(String connName, String type, String instanceName, String templateDefinitionName, String vnfc, String localIface){
		Map<String,Object> internalMap = new LinkedHashMap<String, Object>();
		String finalName = null;
	
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		//System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
		String description = "Created by VIBNEMO translator from source "+instanceName+", template "+templateDefinitionName+" on "+ dateFormat.format(date);
		finalName = connName.replace(".", "-");
		internalMap.put("name", finalName);
		internalMap.put("type", type);
		internalMap.put("VNFC", vnfc);
		if (localIface.split("\\.").length == 2){
			internalMap.put("local_iface_name", localIface.split("\\.")[0]);
		}else{
			internalMap.put("local_iface_name", localIface);
		}
		internalMap.put("description", description);
		
		
		//System.out.println("internalMap "+internalMap);
		
		return internalMap;
	}
	
	public String generateVNFD(String instanceName,String results_path){
		String errorInfo = null;
		Map<String, Object> vnf = new LinkedHashMap<String, Object>();
		Map<String, Object> vnfd = new LinkedHashMap<String, Object>();
		String finalName = null;
		finalName= instanceName.replace(".", "-");
		String description = "This is a complex VNFD generated by VIBNEMO";
		
		vnf.put("name", finalName);
		vnf.put("description", description);
		vnf.put("internal-connections", intConns);
		vnf.put("external-connections", extConns);
		vnf.put("VNFC", vnfc);
		
		vnfd.put("vnf", vnf);
		try {
			dumpWriter(vnfd, instanceName, results_path);
		} catch (IOException e) {
			errorInfo="The result path introduced does not exist";
			//e.printStackTrace();
		}
		return errorInfo;
	}

	public void clear_vnfdGenerator(){
		extConnValue.clear();
		int_conn_value.clear();
		nodeNameTypeConnPointMap.clear();
		nodeVnfcMap.clear();
		nodeVnfdNameMap.clear();
		nodeVnfcNameMap.clear();
		internalConnectionsMap.clear();
		externalConnectionsMap.clear();
		nodeInternalConnMap.clear();
		vmSubstituteMap.clear();
		vnfc.clear();
		intConns.clear();
		extConns.clear();
	}

}

