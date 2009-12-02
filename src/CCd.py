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

class CCd(SocketServer.StreamRequestHandler):

    def handle(self):
        self.data = self.rfile.readline().strip()
        print "%s wrote:" % self.client_address[0]
        print self.data

def main():
    # Create the server, binding to localhost on port 8453
    server = SocketServer.TCPServer(('', common.CCD_PORT), CCd)

    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    server.serve_forever()
    
    # TODO: Run this as a daemon


if __name__ == '__main__':
    main()

