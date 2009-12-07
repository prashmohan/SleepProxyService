#!usr/bin/env python

import sys
import threading
import subprocess
import SocketServer
import logging

CCD_PORT                    =   8453
CLUSTER_STATE_SERVER_PORT   =   8454
CCD_EXEC_PORT               =   8455

class ServiceLauncher(threading.Thread):
    """This is the launching class for various TCP services"""
    
    def __init__(self, port, launchingClass, address=''):
        """launchingClass is the class of the TCP service to be launched into 
        a separate thread"""
        logging.debug("Creating a Service Launcher for " + address + ":" + str(port))
        self.launchingClass =   launchingClass
        self.port           =   port
        self.address        =   address
        threading.Thread.__init__(self)
        
    def run(self):
        # create the server. Bind it to localhost and the service
        try:
            server = SocketServer.TCPServer((self.address, self.port), self.launchingClass)
        except:
            logging.exception("Could not bind to " + self.address + ":" + str(self.port))
            raise

        # Activate the server; this will keep running until you
        # interrupt the program with Ctrl-C
        server.serve_forever()
        

def get_mac_addr():
    proc = subprocess.Popen(['/sbin/ifconfig', 'eth0'], stdout=subprocess.PIPE)
    time.sleep(1)
    output = proc.stdout.read()
    
    for x in output.splitlines():
        if x.find('HWaddr') != -1 and x.find('eth0') != -1:
            return x.split()[4]
    return ''

if __name__ == "__main__":
    sys.stderr.write("This is a library module. Should not be run as a standalone program!")
    sys.exit(1)
