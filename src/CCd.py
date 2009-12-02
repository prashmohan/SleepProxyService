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

class CCdStarter(threading.Thread):
    def run(self):
        # Create the server, binding to localhost on port 8453
        server = SocketServer.TCPServer(('', common.CCD_PORT), CCd)

        # Activate the server; this will keep running until you
        # interrupt the program with Ctrl-C
        server.serve_forever()

        
class CCd(SocketServer.StreamRequestHandler):
    def handle(self):
        self.data = self.rfile.readline().strip()
        print "%s wrote:" % self.client_address[0]
        print self.data

class ProcStdinFeeder (threading.Thread):
    def __init__(self, proc, sock):
        self.proc = proc
        self.sock = sock
        threading.Thread.__init__ ( self )
        
    def run(self):
        while True:
            try:
                read_byte = self.sock.read(1)
            except:
                break
            self.proc.stdin.write(read_byte)
        

class CCexecd(SocketServer.StreamRequestHandler):
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
    CCdStarter().start()
    ccexecd = SocketServer.TCPServer(('', common.CCD_EXEC_PORT), CCexecd)
    ccexecd.serve_forever()    


if __name__ == '__main__':
    main()

