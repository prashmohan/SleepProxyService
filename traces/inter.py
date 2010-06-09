#!/usr/bin/env python
# encoding: utf-8
"""
inter.py

Created by Prashanth Mohan on 2010-01-12.
Copyright (c) 2010 __MyCompanyName__. All rights reserved.
"""

import sys
import os
import logging

def main():
    a = open(sys.argv[1], 'r')
    line = a.readline()
    start = int(line.split()[0])
    for line in a.xreadlines():
        parts = line.split()
        print int(parts[0]) - start + 1, 1
        if int(parts[1]) != 1:
            print 1, parts[1]
        start = int(parts[0])
        
if __name__ == '__main__':
    main()

