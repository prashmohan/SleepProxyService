#!/usr/bin/env python

import socket
import logging
import logging.handlers
import select
import queue
import time
import thread

# Logging functionality
logger = logging.getLogger('MasterSleepProxyService')
logger.setLevel(logging.DEBUG)
logging.basicConfig(filename=LOG_FILENAME,level=logging.DEBUG)

class CommandTypes(object):
    """Command type enum"""
    self.ANNOUNCE_MASTER    =   1

class Command(object):
    """Class described various kinds of commands that can be enqueued for the MasterSleepProxy to perform"""
    def __init__(self, arg):
        super(Command, self).__init__()
        self.command_type = arg
        
    def __repr__(self):
        return "This is a command of type:", self.command_type


class Node(object):
    """Describes a Node in the cluster"""
    
    self.node_name  =   ""
    self.node_ip    =   ""
    self.last_heartbeat =   datetime.datetime()
    
    def __init__(self, name, ip)
        super(Node, self).__init__()
        self.node_name  =   name
        self.node_ip    =   ip
            
    def register_heartbeat(self):
        """A heartbeat request was received from the node and should be registered now"""
        self.last_heartbeat = datetime.datetime()
        
    def __repr__(self)   :
        

class NodeTable(object):
    """Maintains the set of nodes in the cluster"""
    
    node_table = {}
    
    def __init__(self):
        super(NodeTable, self).__init__()
        
    def register_node(self, node):
        pass
        
    def parse_message(self):
        """docstring for parse_message"""
        pass
        
    def get_sleeping_nodes(self):
        pass
    
    def get_active_nodes(self):
        pass
        
    def get_dead_nodes(self):
        pass
        

class MasterSleepProxy (object):
    self.SLEEP_PROXY_PORT   =   9897
    self.LOG_FILENAME       =   '/tmp/master-sleep-proxy-log.txt'
    self.MASTER_ANNOUNCE_INTVL  =   120   # in seconds
    self.HEARTBEAT_INTVL    =   60  # in seconds
    self.queued_commands    =   queue.Queue(0)

    def __init__(self):
        pass        

    def __init_network(self):
        """Initialize sockets for communication with clients"""
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.server_socket.bind(("", SLEEP_PROXY_PORT))
        logger.info("Initialized server communication socket")

        self.broadcast_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.broadcast_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.broadcast_socket.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
        logger.info("Initalized broadcast socket for asserting master status")
    
    def __announce_master(self):
        """Broadcast to the network as the master of the cluster"""
        data = "MasterNode" # TODO: To be replaced with PKI later
        self.server_socket.sendto(data, ("<broadcast>", SLEEP_PROXY_PORT))
        logger.debug("Asserted master node status")
        
    def __manage_master_announcements(self):
        """Function which manages the assertion of master node at regular intervals"""
        while True:
            command = Command(CommandTypes.ANNOUNCE_MASTER)
            self.queued_commands.put(command)
            time.sleep(MASTER_ANNOUNCE_INTVL)
            
    def __init(self):
        """Start threads for the service"""
        self.__init_network()
        # start master announcement thread here
    
    def start(self):
        """This is an infinite loop which will do all master activities"""
        self.__init()
        potential_readers = [self.server_socket]
        potential_writers = []
        potential_errers  = []
        timeout           = 30  # in seconds

        while True:
            ready_to_read, ready_to_write, in_error = \
                           select.select(
                                potential_readers,
                                potential_writers,
                                potential_errers,
                                timeout)
            if ready_to_read:
                data, address = server_socket.recvfrom(256)
                # service read
                pass
            if ready_to_write:
                self.send_
                pass
            if in_error:
                # handle error
                pass
            
            # Are there any commands to be processed?
            potential_writers = []
            if self.queued_commands.qsize() != 0:
                potential_writers = [self.server_socket]
