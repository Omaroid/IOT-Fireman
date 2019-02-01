#include <SoftwareSerial.h>
#include "dht.h"

SoftwareSerial BTSerial(10, 11); // RX | TX
String a,b,c,op,ancien,nouveau;
int t=0;

// lowest and highest sensor readings:
const int sensorMin = 0;     // sensor minimum
const int sensorMax = 1024;  // sensor maximum

dht DHT;

void setup()
{
  pinMode(9, OUTPUT);  // this pin will pull the HC-05 pin 34 (key pin) HIGH to switch module to AT mode
  digitalWrite(9, HIGH);
  Serial.begin(9600);
  Serial.println("Enter AT commands:");
  BTSerial.begin(38400);  // HC-05 default speed in AT command more
}

void loop()
{ 
  DHT.read11(A1);
  
  float h=DHT.humidity;
  
  float t=DHT.temperature;

  // read the sensor on analog A0:
  int sensorReading = analogRead(A0);
  
  // map the sensor range (four options):
  int range = map(sensorReading, sensorMin, sensorMax, 0, 3);
  int fire;
  
  // range value:
    switch (range) {
    case 0:    // A fire closer than 1.5 feet away.
      fire=2;
      break;
    case 1:    // A fire between 1-3 feet away.
      fire=1;
      break;
    case 2:    // No fire detected.
      fire=0;
      break;
    }
    
  // Keep reading from HC-05 and send to Arduino Serial Monitor
  if (BTSerial.available() and t==0){
    t=1;
    a=BTSerial.readString();
    Serial.println(a);
    Serial.println(c);
    op = getValue(c,'#',0);

    if(a.startsWith("OK\r\nI")){
      BTSerial.print(String(fire)+"#"+String(h)+"#"+String(te)+"\n");
      t=10;
    }
    if(c.startsWith("M")){
        ancien = getValue(c,'#',1);
        nouveau = getValue(c,'#',2);
  
        Serial.println(ancien);
        Serial.println(nouveau);
        
        if(a.substring(6,10).equals(ancien)){
          BTSerial.print("AT+PSWD="+nouveau+"\r\n");
          t=2;
        }
    }
    else{
      t=0;
    }
  }

  if(BTSerial.available() and t==10){
    c=BTSerial.readString();
    if(c.startsWith("OK")){
      BTSerial.write("AT+INQ\r\n");
    }
  }

  if(t==10){
    BTSerial.write("AT+INQ\r\n");
    t=0;
  }

  if(BTSerial.available() and t==3){
    c=BTSerial.readString();
    Serial.println(c);
    if(c.startsWith("OK")){
      t=0;
    }
  }

  if(BTSerial.available() and t==4){
    c=BTSerial.readString();
    Serial.println(c);
    t=0;
  }

  if(BTSerial.available() and t==9){
    a=BTSerial.readString();
    Serial.print(a);
    t=0;
  }
  
  // Keep reading from Arduino Serial Monitor and send to HC-05
  if (Serial.available()){
    c=Serial.readString();
    if(c.startsWith("M")){
      BTSerial.write("AT+PSWD\r\n");
      t=0;
    }
    if(c.startsWith("P")){
      BTSerial.write("AT+ RMAAD\r\n");
      t=4;
    }
    if(c.startsWith("S")){
      BTSerial.write("AT+PSWD\r\n");
      t=9;
    }
    if(c.startsWith("R")){
      Serial.println(t);
      Serial.println(ancien);
      Serial.println(nouveau);
      Serial.println(a);
      Serial.println(b);
    }
  }
  
}

String getValue(String data, char separator, int index)
{
    int found = 0;
    int strIndex[] = { 0, -1 };
    int maxIndex = data.length() - 1;

    for (int i = 0; i <= maxIndex && found <= index; i++) {
        if (data.charAt(i) == separator || i == maxIndex) {
            found++;
            strIndex[0] = strIndex[1] + 1;
            strIndex[1] = (i == maxIndex) ? i+1 : i;
        }
    }
    return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}
