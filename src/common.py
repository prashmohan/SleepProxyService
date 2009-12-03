#!usr/bin/env python

import sys
import threading
import SocketServer

CCD_PORT                    =   8453
CLUSTER_STATE_SERVER_PORT   =   8454
CCD_EXEC_PORT               =   8455

class ServiceLauncher(threading.Thread):
    """This is the launching class for various TCP services"""
    
    def __init__(self, port, launchingClass):
        """launchingClass is the class of the TCP service to be launched into 
        a separate thread"""
        
        self.launchingClass =   launchingClass
        self.port           =   port
        threading.Thread.__init__(self)
        
    def run(self):
        # create the server. Bind it to localhost and the service
        server = SocketServer.TCPServer(('', self.port), self.launchingClass)

        # Activate the server; this will keep running until you
        # interrupt the program with Ctrl-C
        server.serve_forever()
        

if __name__ == "__main__":
    sys.stderr.write("This is a library module. Should not be run as a standalone program!")
    sys.exit(1)