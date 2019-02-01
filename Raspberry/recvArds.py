from firebase import firebase
from bluetooth import *
import time
import sys
import os

port = 1
tab =[0,0,0]

firebase=firebase.FirebaseApplication('https://fireman-c4326.firebaseio.com/');
result = firebase.get('/users', None)


def get_data(addr):

    sock=BluetoothSocket(RFCOMM)
    sock.connect((addr, port))

    sock.send("I\r\n")

    rec=""
    rec_end=-1

    while rec_end==-1:
        rec+=sock.recv(1024)
        rec_end=rec.find('\n')

    data=rec[:rec_end]
    print(data)
    global tab
    tab=data.split("#")
    sock.close()

while True:
    #os.system("python firebase_update.py")
    for key,value in result.iteritems():
        if(key=='aZLDqiXzg9bWmBd1R7coAMKp9LL2'):
                for v in value:
                        for key1,value1 in value.iteritems():
                                for key2,value2 in value1.iteritems():
					print("--- Chambre "+value2["name"]+" ---")
                                        get_data(str(value2["addr"]))
					firebase.put('users/aZLDqiXzg9bWmBd1R7coAMKp9LL2/rooms/'+key2,"fire",int(tab[0]))
                                        print("Feu MAJ")
                                        firebase.put('users/aZLDqiXzg9bWmBd1R7coAMKp9LL2/rooms/'+key2,"humidity",float(tab[1]))
                                        print("Humidite MAJ")
                                        firebase.put('users/aZLDqiXzg9bWmBd1R7coAMKp9LL2/rooms/'+key2,"temperature",float(tab[2]))
                                        print("Temperature MAJ")
					time.sleep(2)
sys.exit()
