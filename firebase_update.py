from firebase import firebase
import time
import subprocess
import bluetooth

firebase=firebase.FirebaseApplication('https://fireman-c4326.firebaseio.com/');
result = firebase.get('/users', None)

L1=[]
L2=[]

for key,value in result.iteritems():

	if(key=='aZLDqiXzg9bWmBd1R7coAMKp9LL2'):
		for v in value:
			for key1,value1 in value.iteritems():
				for key2,value2 in value1.iteritems():
					print(key2)
					for key in value2:
						if(key=='update'):
							if(value2[key]==1):
								#appairage
								L1.append(value2['addr'])
								L2.append(value2['pin'])
						
								print(L1)
								print(L2)

								firebase.put('users/aZLDqiXzg9bWmBd1R7coAMKp9LL2/rooms/'+key2,"update",0)
                                                                for i in range(len(L1)):
                                                                    subprocess.call("kill -9 `pidof bt-agent`",shell=True)
                                                                    status = subprocess.call("bt-agent " +L2[i]+" &",shell=True)
                                                                    try:
                                                                            s = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
                                                                            s.connect((L1[i],1))
                                                                    except bluetooth.btcommon.BluetoothError as err:
                                                                            # Error handler
                                                                            pass






