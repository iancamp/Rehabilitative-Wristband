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
  
  if (XBee.available() > 0)
  { // If data comes in from XBee, send it out to serial monitor
    int input = XBee.read(); 
    /*The data coming from the XBee is a Hexidecimal number corresponding
    to the 3, 1 or 4 being sent from the cuff
    */
    if(input == 51)//High Movement(3 from cuff)
    {
      analogWrite(motorPin, 255); //turns the motor on to maximum speed
      digitalWrite(musicPin, HIGH); //turns the music on
      highMoveCount = highMoveCount + 1; //increments the num of high movements
      totalDataCount = totalDataCount + 1; //increments the num of total movements
    }
    else if(input == 49)//Low Movement(1 from cuff)
    {
      analogWrite(motorPin, 200); //turns the motor on not at full speed
      lowMoveCount = lowMoveCount + 1; //increments the num of low movements
      totalDataCount = totalDataCount + 1; //increments the num of total movements
    }
    else if(input == 52)//No Movement(4 from cuff)
    {
      analogWrite(motorPin, 0); //turns the motor off
      digitalWrite(musicPin, LOW); //turns the music off
      noMoveCount= noMoveCount + 1; //increments the num of no movements
      totalDataCount = totalDataCount + 1; //increments the num of total movements
    }
    
  noMoveAvg = (noMoveCount/totalDataCount)*100; //gets avg of no movements through session
  lowMoveAvg = (lowMoveCount/totalDataCount)*100; //gets avg of low movements through session
  highMoveAvg = (highMoveCount/totalDataCount)*100; //gets avg of high movements through session
  
  //Prints in one line the avg of no, low and high movements to serial monitor
  //Format: No Movement: ##%  Low Movement: ##%  High Movement: ##%
  Serial.print("No Movement: ");
  Serial.print(noMoveAvg);
  Serial.print("%");
  Serial.print("\tLow Movement: ");
  Serial.print(lowMoveAvg);
  Serial.print("%");
  Serial.print("\tHigh Movement: ");
  Serial.print(highMoveAvg);
  Serial.println("%");
  }
  
}
