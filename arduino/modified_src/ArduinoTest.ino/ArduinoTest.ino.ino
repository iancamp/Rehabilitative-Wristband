long randnum;
String prefix;
String ack;
String value;
String output;
String input = "";

void setup()                    // run once, when the sketch starts
{
  Serial.begin(9600);           // set up Serial library at 9600 bps
  randomSeed(analogRead(0)); //Generate random seed
  prefix = String("arduts");
  ack = String("ack");
  
}

void loop()                       // run over and over again
{
  while (Serial.available() > 0){
    int inchar = Serial.read();
    if (isDigit(inchar)){
      input+= (char)inchar; //Read in the bytes of the number sent
    }
    if (inchar == '\n'){ //Find the null terminator
      int inint = input.toInt();
      Serial.println(ack + input); //Send across the data with the unique ack prefix
      input = ""; //Reset input String
    }
  }
  randnum = random(100);
  value = String(randnum);
  output = String(prefix + value); //Output in format ardutoysetxxxx where xxxx is the number generated.

  Serial.println(output);  // prints hello with ending line break 
  delay(250); //Wait 250 ms
}
