#!/usr/bin/env python3
from typing import List

import bluetooth
import time
import matplotlib.pyplot as plt
import numpy as np

myFile = open( "Output1.txt", 'w' )
server_sock = bluetooth.BluetoothSocket( bluetooth.RFCOMM )
server_sock.bind( ("", bluetooth.PORT_ANY) )
server_sock.listen( 1 )

port = server_sock.getsockname()[1]

uuid = "00001101-0000-1000-8000-00805F9B34FB"

bluetooth.advertise_service( server_sock, "SampleServer", service_id=uuid,
                             service_classes=[uuid, bluetooth.SERIAL_PORT_CLASS],
                             profiles=[bluetooth.SERIAL_PORT_PROFILE],
                             # protocols=[bluetooth.OBEX_UUID]
                             )

print( "Waiting for connection on RFCOMM channel", port )

client_sock, client_info = server_sock.accept()
print( "Accepted connection from", client_info )

result: List[float] = []
try:
    for i in range(1000):
        temp = "Hello" + str( i )
        start = time.time()
        client_sock.send( str(temp) )
        print(temp)
        data = client_sock.recv( 1024 )
        if not data:
            break
        print( "Received", data )
        end = time.time()
        totalTime: float = end - start
        result.append( totalTime )

except OSError:
    pass
for item in result:
    myFile.write( "{}\n".format( item ) )

x_axis =np.arange(len(result))
plt.bar(x_axis,result)
plt.ylabel("Data Transfer Time")
plt.title("RFcomm Data Transfer")
plt.savefig('Rfcomm_bar1.png')
plt.show()
myFile.close()
print( "Disconnected." )

client_sock.close()
server_sock.close()
print( "All done." )
