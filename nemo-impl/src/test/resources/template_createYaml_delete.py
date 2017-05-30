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
STRUCTURE_DELETE_USERS="http://%s:8181/restconf/operations/nemo-intent:structure-style-nemo-delete"	


def register_admin(contHost):
	data={
			"input":{
					"user-id":"14ce424a-3e50-4a2a-ad5c-b29845158c8b",
					"user-name":"admin",
					"user-password":"abcd",
					"user-role":"admin"
					}
		}
	post(REGISTER_USER % contHost, data)

def transaction_begin_admin(contHost):
	data={
			"input":{
					"user-id":"14ce424a-3e50-4a2a-ad5c-b29845158c8b"				
					}
		}
	post(TRANSACTION_BEGIN % contHost, data)
	
def transaction_end_admin(contHost):
	data={
			"input":{
					"user-id":"14ce424a-3e50-4a2a-ad5c-b29845158c8b"				
					}
		}
	post(TRANSACTION_END % contHost, data)
	

	
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




def delete_intent(contHost):
	data={
			"input":{				
				  "user-id": "14ce424a-3e50-4a2a-ad5c-b29845158c8b",
				  "objects":{
						"node":[
								{
									"node-name":"0bce4e35-a5d3-4692-a7e7-e0c62f1340a1"
								}

							]
						}
					}
		}
	post(STRUCTURE_DELETE_USERS % contHost, data)


def create_yaml(contHost):
	data={
			"input":{
					"user-id":"14ce424a-3e50-4a2a-ad5c-b29845158c8b",
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
	delete_intent(args.controller)
	create_yaml(args.controller)
	transaction_end_admin(args.controller)
	
	#register_user(args.controller)
	#transaction_begin(args.controller)
	#transaction_end(args.controller)
