#!/usr/bin/env python
# encoding: utf-8
"""
sleep_proxy_plugin.py

This is the Sleep Proxy Plugin to the python implementation of GMetad. This 
takes the parsed ganglia messages that gmond sends over the network and 
maintains the state of the cluster. This state is provided to schedulers which
request the same over a TCP socket. The Sleep proxy plugin also communicates 
with the CCd running on the individual nodes and automatically asks them to go
to sleep if they are idle. The plugin also provides an interface to wake up 
individual nodes if needed.
"""
import os
import logging
import socket
import datetime
import time
import SocketServer
import mutex
import cPickle
import common

from Gmetad.gmetad_plugin import GmetadPlugin
from Gmetad.gmetad_config import getConfig, GmetadConfig

lock = mutex.mutex()
clusterState = {}        

class ClusterStateServer(SocketServer.StreamRequestHandler):
    def handle(self):
        logging.debug("Received request for cluster state server")
        lock.testandset()
        logging.debug("Received lock on cluster state")
        global clusterState
        pick_bytes = cPickle.dumps(clusterState,2)
        lock.unlock()
        logging.debug("Unlocking cluster state")
        self.request.send(pick_bytes)
        logging.debug("Sending " + str(len(pick_bytes)) + " bytes of data ")


def get_plugin():
    ''' All plugins are required to implement this method.  It is used as the factory
        function that instanciates a new plugin instance. '''
    # The plugin configuration ID that is passed in must match the section name 
    #  in the configuration file.
    return SleepPlugin('sleep')

class SleepPlugin(GmetadPlugin):
    ''' This class implements the RRD plugin that stores metric data to RRD files.'''
    
    def __init__(self, cfgid):
        # The call to the parent class __init__ must be last
        GmetadPlugin.__init__(self, cfgid)
        logging.debug ("Starting cluster state server")
        self.stateThread = common.ServiceLauncher(common.CLUSTER_STATE_SERVER_PORT, ClusterStateServer, address='192.168.0.1')
        self.stateThread.start()
        logging.debug("Cluster state server has started")

    
    # def _parseConfig(self, cfgdata):
    #     '''This method overrides the plugin base class method.  It is used to
    #         parse the plugin specific configuration directives.'''
    #     for kw,args in cfgdata:
    #         if self.kwHandlers.has_key(kw):
    #             self.kwHandlers[kw](args)

    def start(self):
        '''Called by the engine during initialization to get the plugin going.'''
        pass
    
    def stop(self):
        '''Called by the engine during shutdown to allow the plugin to shutdown.'''        
        pass

    def notify(self, clusterNode):
        '''Called by the engine when the internal data source has changed.'''
        try:
            lock.testandset()
            self._populateState(clusterNode)
        except:
            logging.error("Could not populate cluster state properly!")
        finally:
            lock.unlock()
        
    def _populateState(self, clusterNode):
        
        # clusterState [clusterName] [hostName] [metricName] = [val, sum, num]
        clusterName = str(clusterNode.getAttr('name'))
        global clusterState
        if not clusterState.has_key(clusterName):
            logging.debug("SLEEP: Found cluster " + clusterName)
            clusterState[clusterName] = {}

        currentCluster = clusterState[clusterName]
        
        for hostNode in clusterNode:
            hostName = str(hostNode.getAttr('name'))
            
            if not currentCluster.has_key(hostName):
                logging.debug("SLEEP: Found node " + hostName + " in " + clusterName)
                currentCluster[hostName] = {}
                
            currentHost = currentCluster[hostName]
            currentHost['last_heard'] = int(hostNode.getAttr('reported'))
            currentHost['IP'] = str(hostNode.getAttr('ip'))
            # Update metrics for each host
            for metricNode in hostNode:
                metricName = str(metricNode.getAttr('name'))
                metricVal = ["","",""]
                try:
                    metricVal[0] = str(metricNode.getAttr('val'))
                except:
                    pass
                try:
                    metricVal[1] = str(metricNode.getAttr('sum'))
                except:
                    pass

                try:
                    metricVal[2] = str(metricNode.getAttr('num'))
                except:
                    pass
                
                if metricName == "SLEEP_INTENT" and \
                    metricVal[0] == "YES" and \
                    (not currentHost.has_key(metricName) or \
                        currentHost[metricName][0] != "YES"):
                    # Handle calling to CCd and informing receipt of SLEEP INTENT
                    # Create a socket (SOCK_STREAM means a TCP socket)
                    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    
                    try:
                        # Connect to server and send data
                        logging.info("Connecting to (" + hostNode.getAttr('ip') + ":" + str(common.CCD_PORT) + ") to grant sleep permission")
                        sock.connect((hostName, common.CCD_PORT))
                        sock.send("SLEEP RECEIVED\n")
                        sock.close()
                    except:
                        logging.error("Could not connect to host who wants to sleep. Probably already slept")                    
                currentHost[metricName] = metricVal


if __name__ == '__main__':
    print "This is not a stand alone program. This should be executed only as part of the python gmetad program as a plugin!"
    sys.exit(1)
