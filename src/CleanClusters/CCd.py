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
import random
import time
import logging

LAST_JOB_FINISH             = time.time()
CURRENTLY_PROCESSING_JOB    = False
JOB_CHECK_SLEEP_INTVL       = 60    # 1 minute
IDLE_BEFORE_SLEEP_INTVL     = 30   # 4 minutes
SLEEP_REQ_ACCEPT_WAIT_INTVL = 240   # 4 minutes
monitor                     = None #CCMonitor()

class CCd(SocketServer.StreamRequestHandler):
    def handle(self):
        data = self.rfile.readline().strip()
        if data.startswith("SLEEP"):
            global monitor
            logging.debug('Received sleep confirmation')
            monitor.sleep()
        else:
            logging.error("CCd received malformed data! Check this: " + data)

        
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
                self.proc.stdin.write(read_byte)
            except: # exception is raised when network socket is closed and process is dead
                break

class CCexecd(SocketServer.StreamRequestHandler):
    """This class receives the command to be executed along with stdin input 
    and pipes back stdout over the network"""
    
    def handle(self):
        CURRENTLY_PROCESSING_JOB = True
        try:
            command = self.rfile.readline().strip()
            logging.info("Executing " + repr(command.split()))
            proc = subprocess.Popen(command.split(), stdin=subprocess.PIPE, stdout=subprocess.PIPE, universal_newlines=True)
            ProcStdinFeeder(proc, self.rfile).start()
            while True:
                output = proc.stdout.read(100)
                if output == "":
                    break
                self.wfile.write(output)
            self.wfile.flush()
        except:
            logging.exception("Could not complete execution of job")
            self.wfile.write("ERROR")
            self.wfile.flush()
        finally:
            CURRENTLY_PROCESSING_JOB = False
            LAST_JOB_FINISH = time.time()   # also log failed job, it was an attempt after all
    
class CCMonitor(object):
    """This class monitor's the system for any jobs currently being run and issues a request to sleep when idle"""
    GMETRIC_PATH = '/usr/bin/gmetric'
    def __init__(self):
        self.last_sleep_metric = False
        self.can_sleep = False
        macaddr = common.get_mac_addr()
        if macaddr == '':
            logging.error("Could not retrieve mac address of machine")
        else:
            logging.info ("Executing: " + self.GMETRIC_PATH + " -n \"MACADDR\" -v \"" + macaddr + "\" -t \"string\"")
            subprocess.Popen([self.GMETRIC_PATH, '-n', 'MACADDR', '-v', macaddr, '-t', 'string'])
            self.can_sleep = True

        
    def start(self):
        while True:
            if self.check_if_idle():
                self.request_sleep()
            else:
                self.identify_no_sleep()            
            time.sleep(JOB_CHECK_SLEEP_INTVL)  
            
    def check_if_idle(self):
        if not CURRENTLY_PROCESSING_JOB and time.time() - LAST_JOB_FINISH > IDLE_BEFORE_SLEEP_INTVL and self.can_sleep:
            return True
        return False  
    
    def request_sleep(self):
        """Inform gmond that this node wants to go to sleep"""
        logging.info("Executing: " + repr([self.GMETRIC_PATH, '-n', "SLEEP_INTENT", '-v', "YES", '-t', "string"]))
        subprocess.Popen([self.GMETRIC_PATH, '-n', "SLEEP_INTENT", '-v', "YES", '-t', "string"])
        self.last_sleep_metric = True
    
    def identify_no_sleep(self):
        """Inform gmond that this node does not need to go to sleep"""
        if self.last_sleep_metric:
            return
        logging.info("Executing: " + repr([self.GMETRIC_PATH, '-n', "SLEEP_INTENT", '-v', "NO", '-t', "string"]))
        subprocess.Popen([self.GMETRIC_PATH, '-n', "SLEEP_INTENT", '-v', "NO", '-t', "string"])
        self.last_sleep_metric = False
        
    def sleep(self):
        try:
            logging.debug('putting system to sleep')
            power_state = open('/sys/power/state', 'w')
            logging.debug('opened power state file')
#            if self.check_if_idle():
#                logging.debug("Node no longer idle")
#                power_state.close()
#                return
            logging.info('Going to sleep')
#            power_state.write('mem')
            power_state.close()
        except:
            logging.exception("Could not go to sleep!")
    
def main():
    common.ServiceLauncher(common.CCD_PORT, CCd).start()
    logging.debug("CCexecd listening on " + str(common.CCD_EXEC_PORT))
    common.ServiceLauncher(common.CCD_EXEC_PORT, CCexecd).start()
    global monitor
    monitor = CCMonitor()
    monitor.start()


if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG)
    main()

