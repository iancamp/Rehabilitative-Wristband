/*
University of Delaware Senior Design Team F
Andrew Sieben, Kaitlyn Turek, Kristi Jackson, Amanda Morrison, Jie Ma
Last Updated: 12/9/2014 By Andrew Sieben
Baby cuff reads data from accelerometer and determines level of
movement then communicates the level of movement to the arduino
uno controlling the play gym toys
*/

#include <SoftwareSerial.h>
#include <AcceleroMMA7361.h>

// XBee's DOUT (TX) is connected to pin 2 (Arduino's Software RX)
// XBee's DIN (RX) is connected to pin 3 (Arduino's Software TX)
SoftwareSerial XBee(2, 3); // RX, TX
AcceleroMMA7361 accelero;  
int x = 0;  //initial x acceleration
int y = 0;  //initial y acceleration
int z = 0;  //initial z acceleration
int xPrev;  //previous x acceleration
int yPrev;  //previous y acceleration
int zPrev;  //previous z acceleration
int magnitude = 0; //for holding current magnitude calculation

void setup()
{
  // Set up both ports at 9600 baud.
  Serial.begin(9600);
  Serial1.begin(9600);
  accelero.begin(14, 15, 16, 17, A2, A1, A0);  //sets pins used on Fio V3
  accelero.setARefVoltage(3.3);  //sets the AREF voltage to 3.3V
  accelero.setSensitivity(HIGH);  //sets the sensitivity to +/-6G
  accelero.calibrate();
}

void loop()
{
  xPrev = x;
  yPrev = y;
  zPrev = z;
  x = accelero.getXRaw();
  y = accelero.getYRaw();
  z = accelero.getZRaw();
  /*Reads raw data from accelerometer and determines the change in
  acceleration in each direction from the previous reading. Then sends the
  magnitude of the resulting vector over the serial*/

  magnitude = sqrt(sq(x) + sq(y) + sq(z)); //Raw magnitude calculation
  serial1.print(magnitude); //Send magnitude over serial to toy
  delay(250);
}
