import socket

def send_udp_data():
    # IP-Adresse und Portnummer
    udp_ip = "localhost"
    udp_port = 5000
    
    # UDP-Socket erstellen
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    
    # bind ist optional. Wird es nicht aufgerufen, weißt das Betriebssystem selbst einen
    # frein Port zu.
    # Bitte ausprobieren
    sock.bind((udp_ip, 50001))
    try:
        while True:
            txt = input()

            # Daten senden
            sock.sendto(txt.encode('utf-8'), (udp_ip, udp_port))
           
    
    except KeyboardInterrupt:
        print("Programm beendet.")
    finally:
        sock.close()

if __name__ == "__main__":
    send_udp_data()
