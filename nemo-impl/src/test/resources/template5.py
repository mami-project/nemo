#!/usr/bin/python
#Filename:servicechain.py
import requests,json
import argparse, sys
from requests.auth import HTTPBasicAuth

USERNAME='admin'
PASSWORD='admin'

TRANSACTION_BEGIN="http://%s:8181/restconf/operations/nemo-intent:begin-transaction"
TRANSACTION_END="http://%s:8181/restconf/operations/nemo-intent:end-transaction"
REGISTER_USER="http://%s:8181/restconf/operations/nemo-intent:register-user"
LANGUAGE_INTENT="http://%s:8181/restconf/operations/nemo-intent:language-style-nemo-request"	


def register_admin(contHost):
	data={
			"input":{
					"user-id":"af4fc2be-e3f4-4388-a8ef-3aabae872f2b",
					"user-name":"admin",
					"user-password":"abcd",
					"user-role":"admin"
					}
		}
	post(REGISTER_USER % contHost, data)

def transaction_begin_admin(contHost):
	data={
			"input":{
					"user-id":"af4fc2be-e3f4-4388-a8ef-3aabae872f2b"				
					}
		}
	post(TRANSACTION_BEGIN % contHost, data)
	
def transaction_end_admin(contHost):
	data={
			"input":{
					"user-id":"af4fc2be-e3f4-4388-a8ef-3aabae872f2b"				
					}
		}
	post(TRANSACTION_END % contHost, data)
	
def register_template_definition(contHost):
	data={
			"input":{
			  "user-id": "af4fc2be-e3f4-4388-a8ef-3aabae872f2b",
			  "nemo-statement": 
				"CREATE NodeModel sample_vnf Property string : vnfd-interface VNFD file://github.com/nfvlabs/openmano/blob/master/openmano/scenarios/examples/complex; ConnectionPoint data_inside at VNFD:vnfd-interface; ConnectionPoint data_outside at VNFD:ge1;"
			}
		}
	post(LANGUAGE_INTENT % contHost, data)
	
def register_template_definition2(contHost):
	data={
			"input":{
			  "user-id": "af4fc2be-e3f4-4388-a8ef-3aabae872f2b",
			  "nemo-statement": 
				"CREATE NodeModel shaper_vnf VNFD file://github.com/nfvlabs/openmano/blob/master/openmano/scenarios/examples/complex ; ConnectionPoint a at VNFD:a1; ConnectionPoint b at VNFD:a2;"
			}
		}
	post(LANGUAGE_INTENT % contHost, data)
	
def register_user(contHost):
	data={
			"input":{
					"user-id":"14ce424a-3e50-4a2a-ad5c-b29845158c8b",
					"user-name":"user2",
					"user-password":"abc",
					"user-role":"tenant"
					}
		}
	post(REGISTER_USER % contHost, data)

def transaction_begin(contHost):
	data={
			"input":{
					"user-id":"14ce424a-3e50-4a2a-ad5c-b29845158c8b"			
					}
		}
	post(TRANSACTION_BEGIN % contHost, data)

def transaction_end(contHost):
	data={
			"input":{
					"user-id":"14ce424a-3e50-4a2a-ad5c-b29845158c8b"				
					}
		}
	post(TRANSACTION_END % contHost, data)

def add_vm2(contHost):
	data={		
			"input":{
					"user-id":"af4fc2be-e3f4-4388-a8ef-3aabae872f2b",
					"nemo-statement": "CREATE NodeModel sample_vnf Property string : vnfd-interface; ConnectionPoint data_inside at VNFD:vnfd-interface;"
					}	
	}
	post(LANGUAGE_INTENT % contHost,data)

def add_server1(contHost):
	data={		
			"input":{
					"user-id":"af4fc2be-e3f4-4388-a8ef-3aabae872f2b",
					"nemo-statement": "CREATE Node prueba Type sample_vnf;"
					}	
		}
	post(LANGUAGE_INTENT % contHost,data)

def add_dmz_node(contHost):
	data={
			"input":{				 
				  "user-id": "af4fc2be-e3f4-4388-a8ef-3aabae872f2b",
				  "nemo-statement":
					"CREATE NodeModel dmz; Node sa3 Type sample_vnf Property vnfd-interface : ge1; Node sha3 Type shaper_vnf; ConnectionPoint inside at VNFD:q1; ConnectionPoint output at VNFD:a1; Connection c1 Type p2p Endnodes inside, sa3.data_inside ; Connection c2 Type p2p Endnodes sa3.data_outside, sha3.a ; Connection c3 Type p2p Endnodes sha3.b, output;"
				}
		}
	post(LANGUAGE_INTENT % contHost,data)

def add_internet_node(contHost):
	data={
			"input":{				 
				  "user-id": "14ce424a-3e50-4a2a-ad5c-b29845158c8b",
				  "nemo-statement": "CREATE NodeModel internet Property string : vnfd-int VNFD file://github.com/nfvlabs/openmano/blob/master/openmano/scenarios/examples/complex.yaml; Node dmz1 Type dmz; Node sha4 Type shaper_vnf; ConnectionPoint int_1 at VNFD:w1; ConnectionPoint int_2 at VNFD:b1; Connection ca1 Type p2p Endnodes int_1, dmz1.inside ; Connection ca2 Type p2p Endnodes dmz1.output, sha4.a; Connection ca3 Type p2p Endnodes sha4.b, int_2;"
				}
		}
	post(LANGUAGE_INTENT % contHost,data)

def add_interior_node(contHost):
	data={
			"input":{				 
				  "user-id": "14ce424a-3e50-4a2a-ad5c-b29845158c8b",
				  "nemo-statement":
					"CREATE Node interior Type ext-group Property location: openflow:4:2, ac-info-network: layer3, ac-info-protocol: static, ip-prefix: 192.168.13.0/24;"
				}
	}
	post(LANGUAGE_INTENT % contHost,data)
	
def add_internet_dmz_connection(contHost):
	data={
			"input":{				 
				  "user-id": "14ce424a-3e50-4a2a-ad5c-b29845158c8b",
				  "nemo-statement":
					"CREATE Connection c4 Type p2p Endnodes vm1,vm2;"
				}
	}
	post(LANGUAGE_INTENT % contHost,data)
	
def add_dmz_interior_connection(contHost):
	data={
			"input":{				 
				  "user-id": "14ce424a-3e50-4a2a-ad5c-b29845158c8b",
				  "nemo-statement":
					"CREATE Connection c2 Type p2p Endnodes dmz1.n2,interior;"
				}
	}
	post(LANGUAGE_INTENT % contHost,data)

def post(url, data):
    headers = {'Content-type': 'application/yang.data+json',
               'Accept': 'application/yang.data+json'}
    print "POST %s" % url
    print json.dumps(data, indent=4, sort_keys=True)
    r = requests.post(url, data=json.dumps(data), headers=headers, auth=HTTPBasicAuth(USERNAME, PASSWORD))
    print r.text
    r.raise_for_status()
	
if __name__ == '__main__':

	parser = argparse.ArgumentParser()
	parser.add_argument('--controller', default='127.0.0.1', help='controller IP')
	args=parser.parse_args()
	
	register_admin(args.controller)
	transaction_begin_admin(args.controller)
	register_template_definition(args.controller)
	register_template_definition2(args.controller)
	#add_vm2(args.controller)
	#add_server1(args.controller)
	add_dmz_node(args.controller)
	#add_internet_node(args.controller)
	transaction_end_admin(args.controller)
	
	register_user(args.controller)
	transaction_begin(args.controller)
	
	#add_server1(args.controller)
	
	add_internet_node(args.controller)
	
	#add_interior_node(args.controller)
	
	#add_internet_dmz_connection(args.controller)
	#add_dmz_interior_connection(args.controller)
	transaction_end(args.controller)
