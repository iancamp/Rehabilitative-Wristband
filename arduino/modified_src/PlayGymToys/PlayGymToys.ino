/*
University of Delaware Senior Design Team F
Andrew Sieben, Kaitlyn Turek, Kristi Jackson, Amanda Morrison, Jie Ma
Last Updated: 12/9/2014 By Andrew Sieben
Baby cuff reads data from accelerometer and determines level of
movement then communicates the level of movement to the arduino
uno controlling the play gym toys
*/

// We'll use SoftwareSerial to communicate with the XBee:
#include <SoftwareSerial.h>
// XBee's DOUT (TX) is connected to pin 2 (Arduino's Software RX)
// XBee's DIN (RX) is connected to pin 3 (Arduino's Software TX)
SoftwareSerial XBee(2, 3); // RX, TX
const int motorPin = 9;  //Pin 9 controls the butterfly mobile
const int musicPin = 10;  //Pin 10 controls the music
const int ledPin = 8;  //Pin 8 controls the LEDs in the giraff
double noMoveCount = 0; //Total count of no movements recorded
double lowMoveCount = 0; //Total count of low movements recorded
double highMoveCount = 0;  //Total count of high movemnets recorded
double totalDataCount = 0;  //Total count of data points recorded
double noMoveAvg = 0;  //Avg of no movements through single session
double lowMoveAvg = 0;  //Avg of low movements through single session
double highMoveAvg = 0; //Avg of high movements through single session
String input = ""; //Input string to store the character bytes received so far.
String prefix = String("arduts"); //Unique prefix which is used by computer to identify the arduino.
int threshold = 9999; //Threshold at which the device currently spins and turns on. Device should never activate until learning phase, and so starting threshold is very high.
String ack = String("ack");

void setup()
{
  //Set music, motor, and led pins to outputs
  //Set both the XBee and serial ports at 9600 baud
  pinMode(musicPin, OUTPUT);
  pinMode(motorPin, OUTPUT);
  pinMode(ledPin, OUTPUT);
  XBee.begin(9600);
  Serial.begin(9600);
}

void loop()
{
  analogWrite(ledPin, 130); // always have leds turned on

  // If data comes in from XBee, send it out to serial monitor
  while (XBee.available() > 0) {
    int inchar = XBee.read();
    if (isDigit(inchar)) {
      input += (char)inchar; //Read in the bytes of the number sent
    }
    if (inchar == '\n') { //Find the null terminator
      int inint = input.toInt();
      Serial.println(prefix + input); //Send across the data with the unique prefix
      //Now must determine if the received value goes over the threshold.
      if (inint >= threshold) {
        analogWrite(motorPin, 255); //turns the motor on to maximum speed
        digitalWrite(musicPin, HIGH); //turns the music on
      }
      else {
        analogWrite(motorPin, 0); //turns the motor off
        digitalWrite(musicPin, LOW); //turns the music off
      }
      input = ""; //Reset input String
    }
  }
  
  while (Serial.available() > 0) {
    int inchar = Serial.read();
    if (isDigit(inchar) && inchar != '\n') {
      input += (char)inchar; //Read in the bytes of the number sent
    }
    if (inchar == '\n') { //Find the null terminator
      threshold = input.toInt();
      Serial.println(ack + input + '\n'); //Send across the data with the unique ack prefix
      input = ""; //Reset input String
    }
  }


}
