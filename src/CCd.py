#!/usr/bin/env python
# encoding: utf-8
"""
CCd.py

This is the Clean Clusters daemon. This listens for activity on the node. If it
detects low activity on the node, it publishes a request to sleep. The masters 
nodes in the cluster (running the sleep proxy plugin) will respond with an 
acknowledgement. The node will then go to sleep.
"""
import SocketServer
import sys
import os
import common
import subprocess
import threading
import select


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
        
    
class CCd(SocketServer.StreamRequestHandler):
    def handle(self):
        self.data = self.rfile.readline().strip()
        print "%s wrote:" % self.client_address[0]
        print self.data

        
class ProcStdinFeeder (threading.Thread):
    """This class is responsible for receiving data from the network and 
    pipeing it into the process's  stdin"""
    
    def __init__(self, proc, sock):
        self.proc = proc
        self.sock = sock
        threading.Thread.__init__ ( self )
        
    def run(self):
        while True:
            try:
                read_byte = self.sock.read(1)
            except: # exception is raised when network socket is closed and process is dead
                break
            self.proc.stdin.write(read_byte)
        

class CCexecd(SocketServer.StreamRequestHandler):
    """This class receives the command to be executed along with stdin input 
    and pipes back stdout over the network"""
    
    def handle(self):
        command = self.rfile.readline().strip()
        proc = subprocess.Popen(command.split(), stdin=subprocess.PIPE, stdout=subprocess.PIPE, universal_newlines=True)
        ProcStdinFeeder(proc, self.rfile).start()
        while True:
            output = proc.stdout.read(100)
            if output == "":
                break
            self.wfile.write(output)
        
        self.wfile.flush()
    
    
def main():
    ServiceLauncher(common.CCD_PORT, CCd).start()
    ServiceLauncher(common.CCD_EXEC_PORT, CCexecd).start()
#    CCdStarter().start()
#    CCexecdStarter().start()


if __name__ == '__main__':
    main()

