#!/usr/bin/env python
# encoding: utf-8
"""
CCexec.py

This is a cluster scheduler which uses the sleep proxy plugin.
"""

import sys
import os
import socket
import cPickle
import optparse
import math
import threading
import logging
import common
import time

DEAD_WAIT_TIME  = 300 # seconds
MIN_CPU_AVAIL   = 1200  # Mhz
TIME_BTW_CONN   = 0.5 # seconds
TIME_BTW_NODE_CHECK = 0.1 # seconds
ACPI_MAX_WAKEUP_TIME = 10 # seconds
CONN_MAX_RETRY_COUNT = math.ceil(ACPI_MAX_WAKEUP_TIME / TIME_BTW_CONN)


def parse_options():
    parser = optparse.OptionParser()
    parser.add_option("-c", "--command", dest="command", help="Command name to run on each node")
    parser.add_option("-q", "--quiet", dest="verbose", action="store_false", default=True, help="Don't print debug logs to stdout")    
    parser.add_option("-n", "--num", dest="instances", help="Number of instances of this command to run")
    (options, args) = parser.parse_args()
    if not options.command or not options.instances:
        print parser.usage
        sys.exit(1)
    job = Job(options.command, int(options.instances))
    return job

        
class Job(object):
    """Represents a job to be run in the cluster"""
    def __init__(self, command, instances):
        super(Job, self).__init__()
        self.command = command
        self.instances = instances
        
    def get_repr(self):
        return "Job: " + str(self.instances) + " instances of '" + self.command + "'"
        
    def __repr__(self):
        return self.get_repr()
        
    def __str__(self):
        return self.get_repr()
        
class Node(threading.Thread):
    def __init__(self, name, ip, mac, cpu_avail):
        super(Node, self).__init__()
        self.name   = name
        self.ip     = ip
        self.mac    = mac
        self.retries = 0
        self.sock   = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.cpu_avail = cpu_avail
        self.connected = False
        self.failed = False
        self.started = False

    def get_repr(self):
        ret_str = "Node name: " + self.name + " IP: " + self.ip
        if self.mac != "":
            ret_str += " MAC: " + self.mac
        return ret_str
        
    def __str__(self):
        return self.get_repr()

    def __repr__(self):
        return self.get_repr()

    def wakeup(self):
        if not self.mac == "":
            logging.info ("Waking up Node " + self.name)
            subprocess.Popen('wakeonlan ' + self.mac)            
        else:
            logging.info ("Node " + self.name + " does not have an associated mac address. Cannot be woken up")
        
    def connect(self):
        self.retries += 1
        try:
            logging.debug("Attempting to connect to " + self.ip + ":" + str(common.CCD_EXEC_PORT))
            self.sock.connect((self.ip, common.CCD_EXEC_PORT))
            self.connected = True
            logging.info("Connected to " + self.ip + ":" + str(common.CCD_EXEC_PORT))
        except:
            logging.exception("Could not connect to " + self.ip + ":" + str(common.CCD_EXEC_PORT))
            raise
        
    def execute(self, job):
        """Try and execute a job on the node. Returns False if unsuccessful"""
        try:
            logging.info("Sending " + job.command + " to " + self.ip + ":" + str(common.CCD_EXEC_PORT))
            self.sock.send(job.command + '\n')
        except:
            logging.exception("Could not send job command")
            
    def __del__(self):
        if self.sock:
            try:
                self.sock.close()
            except:
                logging.exception("Could not close socket properly for some reason")
                pass
                
        super(Node, self).__del__()
                
    def run(self):
        """Best effort connect service"""
        logging.info("Got node connection request")
        while self.retries < CONN_MAX_RETRY_COUNT and not self.connected:
            try:
                logging.info("Connecting!")
                self.connect()
                break
            except:
                time.sleep(TIME_BTW_CONN)
        if self.connected == False:
            self.failed = True

def cpu_cmp(node1, node2):    
    if node1.cpu_avail > node2.cpu_avail:
        return 1
    elif node1.cpu_avail == node2.cpu_avail:
        return 0
    else:
        return -1
        
        
class StdinFeeder(threading.Thread):
    """docstring for StdinFeeder"""
    def __init__(self, sock_list):
        super(StdinFeeder, self).__init__()
        self.sock_list = sock_list
    
    def run(self):
        while True:
            line = raw_input("Insert input to program here: ")#sys.stdin.readline()
            for sock in sock_list:
                try:
                    logging.debug ("Sending " + line)
                    sock.send(line)                    
                except:
                    logging.exception("Can no longer send data over " + repr(sock))
                    sock_list.remove(sock)
            
            if len(sock_list) == 0:
                break        
        
class Scheduler(object):
    """This is the scheduler which will run and allocate jobs on the various nodes in the cluster"""
    active_nodes = []
    sleep_nodes = []
    dead_nodes = []  
    overloaded_nodes = []
    
    def __init__(self):
        super(Scheduler, self).__init__()
        self.cluster_state = self._retrieve_cluster_info()
        self._sort_nodes()
        self.active_node_ptr = 0
        self.sleep_node_ptr = 0
        self.overloaded_node_ptr = 0
        
    
    def _retrieve_cluster_info(self):
        """Retrieves information about hosts in the cluster from the local sleep proxy plugin daemon"""
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        buf = ''    
        try:
            # Connect to server and send data
            sock.connect(('localhost', common.CLUSTER_STATE_SERVER_PORT))
            while True:
                bytes = sock.recv(8192)
                if not bytes: # end of transfer
                    break            
                buf += bytes
        except:
            err_msg = "Could not receive cluster state from local sleep proxy"
            logging.exception(err_msg)
            raise
        finally:
            sock.close()

        return cPickle.loads(buf)   

    def _sort_nodes(self):
        for cluster in self.cluster_state:
            cluster = self.cluster_state[cluster]
            for host in cluster:
                host_name = host
                host = cluster[host]
                ip = host['IP']
                mac = ''
                if host.has_key('MACADDR'):
                    mac = host['MACADDR'][0]
                idle = float(host['cpu_idle'][0])
                cpu_num = int(host['cpu_num'][0])
                cpu_speed = int(host['cpu_speed'][0])
                cpu_avail = idle * cpu_num * cpu_speed
                node = Node(host_name, ip, mac, cpu_avail)                
                if time.time() - host['last_heard'] > DEAD_WAIT_TIME and (not host.has_key('SLEEP_INTENT') or host['SLEEP_INTENT'] != 'YES'):
                    self.dead_nodes.append(node)
                elif host.has_key('SLEEP_INTENT') and host['SLEEP_INTENT'] == 'YES':
                    self.sleep_nodes.append(node)
                elif node.cpu_avail < MIN_CPU_AVAIL:
                    self.overloaded_nodes.append(node)
                else:
                    self.active_nodes.append(node)
        
        self.active_nodes.sort(cpu_cmp)
    
    
    def _get_next_node(self):
        return_node = None
        
        if not self.active_node_ptr >= len(self.active_nodes): # nodes are still available in the active queue
            return_node = self.active_nodes[self.active_node_ptr]
            self.active_node_ptr += 1
            
        elif not self.sleep_node_ptr >= len(self.sleep_nodes): # nodes are available in the sleeping queue
            return_node = self.sleep_nodes[self.sleep_node_ptr]
            return_node.wakeup()
            self.sleep_node_ptr += 1
        
        elif not self.overloaded_node_ptr >= len(self.overloaded_nodes):
            return_node = self.overloaded_nodes[self.overloaded_node_ptr]
            self.overloaded_node_ptr += 1
        return return_node
        
    def _populate_node_list(self, total_nodes):
        logging.debug("Populating list with " + str(total_nodes) + " nodes")
        new_list = []
        while len(new_list) < total_nodes:
            new_node = self._get_next_node()
            if not new_node:
                logging.error ("No more nodes available for execution")
                sys.exit(1)
            new_list.append(new_node)
        return new_list
    
    def schedule(self, job):
        total_instances = job.instances
        if total_instances > (len(self.active_nodes) + len(self.sleep_nodes)):
            logging.error("Not enough nodes in cluster to execute job")
        
        selected_nodes= []
        while True:
            new_list = self._populate_node_list(total_instances - len(selected_nodes))
            selected_nodes += new_list
            logging.debug ("List of populated nodes: " + repr(selected_nodes))
            connected = 0
            for node in selected_nodes:
                if not node.connected and node.failed:
                    logging.info("Not using " + repr(node))
                    selected_nodes.remove(node)
                    no_change = False
                    continue
                elif not node.connected and not node.started:
                    logging.info("Starting " + repr(node))
                    node.started = True
                    node.start()
                    no_change = False
                elif node.connected:
                    connected += 1
            logging.debug(str(connected) + " connected nodes")
            if connected ==  total_instances:
                break
            
            time.sleep(TIME_BTW_NODE_CHECK)
        
        sock_list = []
        logging.info("Final list of selected nodes: " + repr(selected_nodes))
        for node in selected_nodes:
            node.execute(job)
            sock_list.append(node.sock)
        
        read_sock_list = sock_list[:]
        StdinFeeder(sock_list).start()
        
        while True:
            line = raw_input()
            reader, writer, errer = select.select([read_sock_list], [], [], 60)
            for sock in reader:
                data = ''
                while True:
                    buf = sock.recv(4096)
                    if not buf:
                        break
                    data += buf
                print '[From ' + sock.getsockname[0] + ':' + str(sock.getsockname[1]) + '] ', data
                

                        
def main():
    job = parse_options()
    logging.info("Received job " + repr(job))
    scheduler = Scheduler()
    logging.debug("Created instance of scheduler")
    scheduler.schedule(job)

if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG)
    main()

