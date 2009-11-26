#!/usr/bin/env python

import socket

SLEEP_PROXY_PORT    =   9897

client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
client_socket.bind(("", SLEEP_PROXY_PORT))

while 1:
    data, address = client_socket.recvfrom(256)
    print "( " ,address[0], " " , address[1] , " ) said : ", data

client_socket.close()
