long randnum;
String prefix;
String value;
String output;

void setup()                    // run once, when the sketch starts
{
  Serial.begin(9600);           // set up Serial library at 9600 bps
  randomSeed(analogRead(0)); //Generate random seed
  prefix = String("arduts");
  
}

void loop()                       // run over and over again
{
  randnum = random(100);
  value = String(randnum);
  output = String(prefix + value); //Output in format ardutoysetxxxx where xxxx is the number generated.

  Serial.println(output);  // prints hello with ending line break 
  delay(250); //Wait 250 ms
}
