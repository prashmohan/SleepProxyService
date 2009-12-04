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

        
class Job(object):
    """Represents a job to be run in the cluster"""
    def __init__(self, command, instances):
        super(Job, self).__init__()
        self.command = command
        self.instances = instances


class Scheduler(object):
    """This is the scheduler which will run and allocate jobs on the various nodes in the cluster"""
    def __init__(self):
        super(Scheduler, self).__init__()
        self.cluster_state = self.retrieve_cluster_info()
    
    def retrieve_cluster_info():
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
            logging.error(err_msg)
            raise Exception(err_msg)
        finally:
            sock.close()

        return cPickle.loads(buf)
        
    def wake_up_machines(self, macs):
        for mac in macs:
            subprocess.Popen('wakeonlan ' + mac)        

    def sort_nodes(self):
        pass
        
    def schedule(self, job):
        pass
        
def main():
    pass


if __name__ == '__main__':
    main()

