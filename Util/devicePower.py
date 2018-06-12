#!/usr/bin/env python3

from urllib import request
import json
import datetime
import sys
import random
import time

URLFORMAT = 'http://{}:{}/devicePower'
DATEFORMAT = '%Y%m%d%H%M%S'

MENOR = 0.0
MAYOR = 100.0

def post_data(url, deviceID, value, then, now):
    body = {'deviceID' : deviceID, 'value': value,
            'from': then.strftime(DATEFORMAT),
            'to': now.strftime(DATEFORMAT)}
    bodybytes = json.dumps(body).encode('utf-8')
    
    req = request.Request(url)
    req.add_header('Content-Type', 'application/json; charset=utf-8')
    req.add_header('Content-Length', len(bodybytes))
    res = request.urlopen(req, bodybytes)

def post_loop(url, deviceId, interval):
    diferencia = datetime.timedelta(seconds=interval)
    while True:
        now = datetime.datetime.now()
        then = now - diferencia
        value = random.uniform(MENOR, MAYOR)
        post_data(url, deviceId, value, then, now)
        time.sleep(interval)
        
    
def main(argv):
    if len(argv) != 5:
        print('Usage: {} <host> <port> <deviceId> <interval>'.format(argv[0]))
        return
    
    host = argv[1]
    port = argv[2]
    deviceId = int(argv[3])
    interval = int(argv[4])
    
    url = URLFORMAT.format(host, port)

    post_loop(url, deviceId, interval)

if __name__ == '__main__':
    main(sys.argv)

