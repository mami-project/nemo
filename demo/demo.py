#!/usr/bin/python3
import requests,json
import argparse, sys
import os
from requests.auth import HTTPBasicAuth


USERNAME = 'admin'
PASSWORD = 'admin'
USER_ID = 'af4fc2be-e3f4-4388-a8ef-3aabae872f2a'

API = '/restconf/operations/nemo-intent:'
controller = ""
port = ""

TRANSACTION_BEGIN = 'begin-transaction'
TRANSACTION_END = 'end-transaction'
REGISTER_USER = 'register-user'
LANGUAGE_INTENT = 'language-style-nemo-request'
GENERATE_YAML = 'create-vnfd'      


def register_admin():
    data={
        "input":{
            "user-id": USER_ID,
            "user-name": USERNAME,
            "user-password": PASSWORD,
            "user-role": "admin"
        }
    }
    post(REGISTER_USER, data)

def transaction_begin_admin():
    data={
        "input":{
            "user-id": USER_ID                                
        }
    }
    post(TRANSACTION_BEGIN, data)
        
def transaction_end_admin():
    data={
        "input":{
            "user-id": USER_ID                                
        }
    }
    post(TRANSACTION_END, data)
        
def register_template_definition(intent):
    data={
        "input":{
            "user-id": USER_ID,
            "nemo-statement": intent
        }
    }
    post(LANGUAGE_INTENT, data)
        
        
def create_yaml(instance, path, style):
    data={
        "input":{
            "user-id": USER_ID,
            "instance-name": instance,
            "results-path": path,
            "vnfd-style": style                      
        }
    }
    post(GENERATE_YAML, data)

def post(endpoint, data):
    headers = { 'Content-type': 'application/yang.data+json',
                'Accept': 'application/yang.data+json'}
    print("POST %s", get_url(endpoint))
    print(json.dumps(data, indent=4, sort_keys=True))
    r = requests.post(get_url(endpoint), data=json.dumps(data), headers=headers, auth=HTTPBasicAuth(USERNAME, PASSWORD))
    print(r.text)
    r.raise_for_status()

def get_url(endpoint):
    return 'http://' + controller + ':' + port + API + endpoint
    
        
if __name__ == '__main__':

    path_result_default=os.getcwd()
    parser = argparse.ArgumentParser()
    parser.add_argument('--intent', dest='intent', help='Path of your intent', required=True)
    parser.add_argument('--instance', dest='instance', help='Instance\'s name from which you want to generate the VNFD', required=True)
    parser.add_argument('--style', dest='style', choices={'osm', 'openmano'}, help='Choose VNFD style between osm and openmano', required=True)
    parser.add_argument('--path', dest='path', default=path_result_default, help='Destination path where the VNF Descriptor will be saved')
    parser.add_argument('--controller', dest='controller', default='127.0.0.1', help='Controller IP')
    parser.add_argument('--port', dest='port', default='8181', help='Controller port')
    args=parser.parse_args()

    controller = args.controller
    port = args.port

    try:
        register_admin()
    except Exception:
        sys.exit('Register Admin ERROR')

    try:
        transaction_begin_admin()
    except Exception:
        sys.exit('Transaction Begin ERROR')

    try:
        f = open(args.intent, 'r')
        for line in f:
            if(line != "\n"):
                register_template_definition(line.split("\n")[0])
    except IOError: 
        transaction_end_admin()
        sys.exit("The path introduced: " + args.intent + " is incorrect")

    try:
        create_yaml(args.instance, args.path, args.style)
    except NameError: 
        transaction_end_admin()
        sys.exit("The instance's name: " + args.instance + " is not valid")
        
    transaction_end_admin()
