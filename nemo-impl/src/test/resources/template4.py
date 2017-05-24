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
GENERATE_YAML="http://%s:8181/restconf/operations/nemo-intent:create-vnfd"	


def register_admin(contHost):
	data={
			"input":{
					"user-id":"e6e1b898-05c0-488b-be7f-9af8b63b6db4",
					"user-name":"admin",
					"user-password":"abcd",
					"user-role":"admin"
					}
		}
	post(REGISTER_USER % contHost, data)

def transaction_begin_admin(contHost):
	data={
			"input":{
					"user-id":"e6e1b898-05c0-488b-be7f-9af8b63b6db4"				
					}
		}
	post(TRANSACTION_BEGIN % contHost, data)
	
def transaction_end_admin(contHost):
	data={
			"input":{
					"user-id":"e6e1b898-05c0-488b-be7f-9af8b63b6db4"				
					}
		}
	post(TRANSACTION_END % contHost, data)
	
def register_template_definition(contHost):
	data={
			"input":{
			  "user-id": "e6e1b898-05c0-488b-be7f-9af8b63b6db4",
			  "nemo-statement": 
				"CREATE NodeModel sample_vnf Property string : vnfd-interface VNFD file://github.com/nfvlabs/openmano/blob/master/openmano/scenarios/examples/complex; ConnectionPoint data_inside at VNFD:vnfd-interface; ConnectionPoint data_outside at VNFD:ge1;"
			}
		}
	post(LANGUAGE_INTENT % contHost, data)
	
def register_template_definition2(contHost):
	data={
			"input":{
			  "user-id": "e6e1b898-05c0-488b-be7f-9af8b63b6db4",
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


def add_dmz_node(contHost):
	data={
			"input":{				 
				  "user-id": "e6e1b898-05c0-488b-be7f-9af8b63b6db4",
				  "nemo-statement":
					"CREATE NodeModel dmzer; Node hola3 Type sample_vnf Property vnfd-interface :eth0-vnf_2vms-vm2; Node sha3 Type shaper_vnf; ConnectionPoint inside at VNFD:q1; ConnectionPoint output at VNFD:a1; Connection c1 Type p2p Endnodes inside, hola3.data_inside ; Connection c2 Type p2p Endnodes hola3.data_outside, sha3.a ; Connection c3 Type p2p Endnodes sh3.b, output;"
				}
		}
	post(LANGUAGE_INTENT % contHost,data)

def add_internet_node(contHost):
	data={
			"input":{				 
				  "user-id": "e6e1b898-05c0-488b-be7f-9af8b63b6db4",
				  "nemo-statement": "CREATE NodeModel internet VNFD file://github.com/nfvlabs/openmano/blob/master/openmano/scenarios/examples/complex.yaml; Node dmz1 Type dmzer; Node sha4 Type shaper_vnf; ConnectionPoint int_1 at VNFD:w1; ConnectionPoint int_2 at VNFD:b1; Connection ca1 Type p2p Endnodes int_1, dmz1.inside ; Connection ca2 Type p2p Endnodes dmz1.output, sha4.a; Connection ca3 Type p2p Endnodes sha4.b, int_2;"
				}
		}
	post(LANGUAGE_INTENT % contHost,data)

def add_internet1_node(contHost):
	data={
			"input":{				 
				  "user-id": "e6e1b898-05c0-488b-be7f-9af8b63b6db4",
				  "nemo-statement":
					"CREATE Node internet1 Type internet;"
				}
	}
	post(LANGUAGE_INTENT % contHost,data)

def create_yaml(contHost):
	data={
			"input":{
					"user-id":"e6e1b898-05c0-488b-be7f-9af8b63b6db4",
					"instance-name":"internet1"				
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
	register_template_definition(args.controller)
	register_template_definition2(args.controller)
	add_dmz_node(args.controller)
	add_internet_node(args.controller)
	add_internet1_node(args.controller)
	create_yaml(args.controller)
	transaction_end_admin(args.controller)
	
	#register_user(args.controller)
	#transaction_begin(args.controller)
	#transaction_end(args.controller)
