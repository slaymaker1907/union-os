import boto3
import json
import socket
import struct
import pickle

class Orchestrator:
    def __init__(self, imageid, itype, keyname, sec_group):
        self.ec2 = boto3.resource('ec2')
        self.imageid = imageid
        self.itype = itype
        self.keyname = keyname
        self.sec_group = sec_group

    def create_vm(self):
        return self.ec2.create_instances(ImageId=self.imageid, MinCount=1, MaxCount=1, 
            InstanceType='m4.large', KeyName=self.keyname, SecurityGroups=[self.sec_group])[0]

class VM:
    def __init__(self, inst):
        self.cpu = 0
        self.mem = 0
        self.inst = inst
    
    def logcpu(self, usage):
        self.cpu = usage

    def logmem(self, usage):
        self.mem = usage

class VMTracker:
    def __init__(self):
        self.vms = []

async def server(reader, writer):
    size = await reader.read(8)
    size = struct.unpack('Q', size)
    request = await reader.read(size)
    program = pickle.load