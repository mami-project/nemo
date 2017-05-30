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
					"user-id":"af4fc2be-e3f4-4388-a8ef-3aabae872f2a",
					"user-name":"admin",
					"user-password":"abcd",
					"user-role":"admin"
					}
		}
	post(REGISTER_USER % contHost, data)

def transaction_begin_admin(contHost):
	data={
			"input":{
					"user-id":"af4fc2be-e3f4-4388-a8ef-3aabae872f2a"				
					}
		}
	post(TRANSACTION_BEGIN % contHost, data)
	
def transaction_end_admin(contHost):
	data={
			"input":{
					"user-id":"af4fc2be-e3f4-4388-a8ef-3aabae872f2a"				
					}
		}
	post(TRANSACTION_END % contHost, data)
	
def register_template_definition(contHost, intent):
	data={
			"input":{
			  "user-id": "af4fc2be-e3f4-4388-a8ef-3aabae872f2a",
			  "nemo-statement": intent
			}
		}
	post(LANGUAGE_INTENT % contHost, data)
	
	

def add_prop1_node(contHost):
	data={
			"input":{				 
				  "user-id": "af4fc2be-e3f4-4388-a8ef-3aabae872f2a",
				  "nemo-statement":
					"CREATE NodeModel prop1; Node aux1 Type vnf_property Property vnfd-interface : ge1; ConnectionPoint inside at VNFD:q1; Connection c1 Type p2p Endnodes inside, aux1.data_inside;"
				}
		}
	post(LANGUAGE_INTENT % contHost,data)

def add_node(contHost):
	data={
			"input":{				 
				  "user-id": "af4fc2be-e3f4-4388-a8ef-3aabae872f2a",
				  "nemo-statement":
					"CREATE Node merge Type prop1;"
	
				}
	}
	post(LANGUAGE_INTENT % contHost,data)


def create_yaml(contHost, instance, path):
	data={
			"input":{
					"user-id":"af4fc2be-e3f4-4388-a8ef-3aabae872f2a",
					"instance-name":instance,
					"results-path":path				
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
	try:
		path = raw_input("Introduce the path of your intent ")
		print path
		f = open(path, 'r')
		register_admin(args.controller)
		transaction_begin_admin(args.controller)
		for line in f:
			if(line != "\n"):
				register_template_definition(args.controller, line.split("\n")[0])
	except IOError: 
		sys.exit("The path introduced: "+path+" is incorrect")
		
	try:
		instance = raw_input("Introduce instance's name from which you want to generate the VNFD ")
		path = raw_input("Introduce the path where you want to save the result files ")
		create_yaml(args.controller, instance, path)
	except NameError: 
		sys.exit("The instance's name: "+instance+" is not valid")

	#register_template_definition(args.controller)
	#add_prop1_node(args.controller)
	#add_node(args.controller)
	#add_node(args.controller)
	transaction_end_admin(args.controller)
	
	#register_user(args.controller)
	#transaction_begin(args.controller)
	#transaction_end(args.controller)
