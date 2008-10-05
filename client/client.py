import sys
from socket import *

serverHost = "localhost"            # servername is localhost
serverPort = 5222                   # use arbitrary port > 1024

s = socket(AF_INET, SOCK_STREAM)    # create a TCP socket

s.connect((serverHost, serverPort)) # connect to server on the port
#s.send("Hello world")               # send the data

data = s.recv(1)
string = ""
while len(data):
  string = string + data
  data = s.recv(1024)

print string

s.close()