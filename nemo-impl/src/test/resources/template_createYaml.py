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
GENERATE_YAML="http://%s:8181/restconf/operations/nemo-intent:create-yaml"	


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
	
def register_template_definition_ids(contHost):
	data={
			"input":{
			  "user-id": "af4fc2be-e3f4-4388-a8ef-3aabae872f2b",
			  "nemo-statement": 
				"CREATE NodeModel ids_vnf VNFD file:///github.com/nfvlabs/openmano/blob/master/openmano/scenarios/examples/ids.yaml; ConnectionPoint ids_1 at VNFD:ge1; ConnectionPoint ids_2 at VNFD:ge2;"
			}
		}
	post(LANGUAGE_INTENT % contHost, data)
	
def register_template_definition_fw(contHost):
	data={
			"input":{
			  "user-id": "af4fc2be-e3f4-4388-a8ef-3aabae872f2b",
			  "nemo-statement": 
				"CREATE NodeModel fw_vnf VNFD file://github.com/nfvlabs/openmano/blob/master/openmano/scenarios/examples/fw.yaml ; ConnectionPoint fw1 at VNFD:a1; ConnectionPoint fw2 at VNFD:a2;"
			}
		}
	post(LANGUAGE_INTENT % contHost, data)


def register_template_definition_nat(contHost):
	data={
			"input":{
			  "user-id": "af4fc2be-e3f4-4388-a8ef-3aabae872f2b",
			  "nemo-statement": 
				"CREATE NodeModel nat_vnf VNFD file://github.com/nfvlabs/openmano/blob/master/openmano/scenarios/examples/nat.yaml ; ConnectionPoint nat1 at VNFD:a1; ConnectionPoint nat2 at VNFD:a2;"
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


def add_merge_nodemodel(contHost):
	data={
			"input":{				 
				  "user-id": "af4fc2be-e3f4-4388-a8ef-3aabae872f2b",
				  "nemo-statement":
					"CREATE NodeModel merge_vnf; Node ids Type ids_vnf; Node firewall Type fw_vnf; Node nat Type nat_vnf; ConnectionPoint merge1 at VNFD:q1; ConnectionPoint merge2 at VNFD:a1; Connection c1 Type p2p Endnodes merge1, ids.ids_1 ; Connection c2 Type p2p Endnodes ids.ids_2, firewall.fw1 ;  Connection c3 Type p2p Endnodes firewall.fw2, nat.nat1; Connection c4 Type p2p Endnodes nat.nat2, merge2; "
				}
		}
	post(LANGUAGE_INTENT % contHost,data)

def add_merge_node(contHost):
	data={
			"input":{				 
				  "user-id": "af4fc2be-e3f4-4388-a8ef-3aabae872f2b",
				  "nemo-statement":
					"CREATE Node merge Type merge_vnf;"
				}
	}
	post(LANGUAGE_INTENT % contHost,data)

def create_yaml(contHost):
	data={
			"input":{
					"user-id":"af4fc2be-e3f4-4388-a8ef-3aabae872f2b"				
					}
		}
	post(GENERATE_YAML % contHost, data)
	

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
	register_template_definition_ids(args.controller)
	register_template_definition_fw(args.controller)
	register_template_definition_nat(args.controller)
	add_merge_nodemodel(args.controller)
	add_merge_node(args.controller)
	create_yaml(args.controller)
	transaction_end_admin(args.controller)
	
	register_user(args.controller)
	transaction_begin(args.controller)
	
	transaction_end(args.controller)
