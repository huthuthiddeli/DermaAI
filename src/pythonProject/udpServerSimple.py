import socket

def receive_udp_data():
    # IP-Adresse und Portnummer
    udp_ip = "localhost"
    udp_port = 5000
    
    # UDP-Socket erstellen und binden
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((udp_ip, udp_port))
    
    print(f"Warte auf Daten auf {udp_ip}:{udp_port}...")
    
    try:
        while True:
            # Daten empfangen
            data, addr = sock.recvfrom(1024)
            print (f"From: {addr}: {data.decode('utf-8')}")    

    except KeyboardInterrupt:
        print("Programm beendet.")
    finally:
        sock.close()

if __name__ == "__main__":
    receive_udp_data()
