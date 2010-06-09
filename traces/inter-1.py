#!/usr/bin/env python
# encoding: utf-8
"""
inter-1.py

Created by Prashanth Mohan on 2010-01-12.
Copyright (c) 2010 __MyCompanyName__. All rights reserved.
"""

import sys
import os


def main():
    a = open(sys.argv[1], 'r')
    line = a.readline()
    last = int(line.split()[0])
    tot = int(line.split()[1])
    for line in a.xreadlines():
        parts = line.split()
        if last != int(parts[0]):
            print last, tot
            last = int(parts[0])
            tot = 0
        tot += int(parts[1])
    print last, tot

if __name__ == '__main__':
    main()

