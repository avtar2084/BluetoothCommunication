import multiprocessing
import bluetooth
import time

server_sock = bluetooth.BluetoothSocket( bluetooth.RFCOMM )

def worker():
    try:
        client_sock, client_info = server_sock.accept()
    except ValueError as E:
        print(E)
    print("Accepted connection from", client_info)
    while True:
        data = client_sock.recv(1024)
        if not data:
            break
        print("Received", data)
    time.sleep(50)
    return

if __name__ == '__main__':
    jobs = []
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
    for i in range(2):
        p = multiprocessing.Process(target=worker)
        jobs.append(p)
        p.start()
        time.sleep(20)