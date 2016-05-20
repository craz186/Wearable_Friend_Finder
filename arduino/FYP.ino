// Test code for Adafruit Flora GPS modules
//
// This code shows how to listen to the GPS module in an interrupt
// which allows the program to have more 'freedom' - just parse
// when a new NMEA sentence is available! Then access data when
// desired.
//
// Tested and works great with the Adafruit Flora GPS module
// ------> http://adafruit.com/products/1059
// Pick one up today at the Adafruit electronics shop
// and help support open source hardware & software! -ada

#include <Adafruit_GPS.h>
#include <Adafruit_NeoPixel.h>
#include <SoftwareSerial.h>
#include <Time.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_LSM303_U.h>
#include <stdio.h>
#include <stdlib.h>

#include "Adafruit_BLE.h"
#include "Adafruit_BluefruitLE_SPI.h"
#include "Adafruit_BluefruitLE_UART.h"
#include "BluefruitConfig.h"

/* Assign a unique ID to this sensor at the same time */
Adafruit_LSM303_Mag_Unified mag = Adafruit_LSM303_Mag_Unified(12345);

// Set GPSECHO to 'false' to turn off echoing the GPS data to the Serial console
// Set to 'true' if you want to debug and listen to the raw GPS sentences

// this keeps track of whether we're using the interrupt
// off by default!
boolean usingInterrupt = false;

//--------------------------------------------------|
//                    WAYPOINT                      |
//--------------------------------------------------|
//Please enter the latitude and longitude of your   |
//desired destination:                              |
#define FACTORYRESET_ENABLE         1

//--------------------------------------------------|
//Your NeoPixel ring may not line up with ours.     |
//Enter which NeoPixel led is your top LED (0-15).  |
#define TOP_LED                1
//--------------------------------------------------|
//Your compass module may not line up with ours.    |
//Once you run compass mode, compare to a separate  |
//compass (like one found on your smartphone).      |
//Point your TOP_LED north, then count clockwise    |
//how many LEDs away from TOP_LED the lit LED is    |
#define LED_OFFSET             0
//--------------------------------------------------|

// Navigation location
float targetLat = 0.0;
float targetLon = 0.0;

// Trip distance
float tripDistance;

Adafruit_NeoPixel strip = Adafruit_NeoPixel(16, 6, NEO_GRB + NEO_KHZ800);
Adafruit_BluefruitLE_UART ble(Serial1, BLUEFRUIT_UART_MODE_PIN);

// Offset hours from gps time (UTC)
//const int offset = 1;   // Central European Time
//const int offset = -4;  // Eastern Daylight Time (USA)
const int offset = -5;  // Central Daylight Time (USA)
//const int offset = -8;  // Pacific Standard Time (USA)
//const int offset = -7;  // Pacific Daylight Time (USA)

int topLED = TOP_LED;
int compassOffset = LED_OFFSET;

int lastMin = 16;
int lastHour = 16;
int startLED = 0;
int startLEDlast = 16;
int lastCombined = 0;
int start = 1;
int mode = 1;
int lastDir = 16;
int dirLED_r = 0;
int dirLED_g = 0;
int dirLED_b = 255;
int compassReading;

// Calibration offsets
float magxOffset = 2.55;
float magyOffset = 27.95;

// Pushbutton setup
int buttonPin = 10;             // the number of the pushbutton pin
int buttonState;               // the current reading from the input pin
int lastButtonState = HIGH;    // the previous reading from the input pin
long buttonHoldTime = 0;         // the last time the output pin was toggled
long buttonHoldDelay = 2500;      // how long to hold the button down

// the following variables are long's because the time, measured in miliseconds,
// will quickly become a bigger number than can be stored in an int.
long lastDebounceTime = 0;     // the last time the output pin was toggled
long debounceDelay = 50;       // the debounce time; increase if the output flickers
long menuDelay = 2500;
long menuTime;

float sLat = 0.0;
float sLon = 0.0;

int next = 0;
float latitude[50];
float longitude[50];

int expectGps = 1;
int current = 0;
int eof = 0; 
void setup()
{
  // connect at 115200 so we can read the GPS fast enough and echo without dropping chars
  // also spit it out
  Serial.begin(115200);

   Serial.println(F("Adafruit Bluefruit Command Mode Example"));
  Serial.println(F("---------------------------------------"));

  /* Initialise the module */
  Serial.print(F("Initialising the Bluefruit LE module: "));

  if ( !ble.begin(VERBOSE_MODE) )
  {
    Serial.println(F("Couldn't find Bluefruit, make sure it's in CoMmanD mode & check wiring?"));
  }
  Serial.println( F("OK!") );

  if ( FACTORYRESET_ENABLE )
  {
    /* Perform a factory reset to make sure everything is in a known state */
    Serial.println(F("Performing a factory reset: "));
    if ( ! ble.factoryReset() ){
      Serial.println(F("Couldn't factory reset"));
    }
  }

  /* Disable command echo from Bluefruit */
  ble.echo(false);

  Serial.println("Requesting Bluefruit info:");
  /* Print Bluefruit information */
  ble.info();

  Serial.println(F("Please use Adafruit Bluefruit LE app to connect in UART mode"));
  Serial.println(F("Then Enter characters to send to Bluefruit"));
  Serial.println();

  ble.verbose(false);  // debug info is a little annoying after this point!

  /* Wait for connection */
  while (! ble.isConnected()) {
      delay(500);
  }
  

  /* Initialise the sensor */
  if(!mag.begin())
  {
    /* There was a problem detecting the LSM303 ... check your connections */
    Serial.println("Ooops, no LSM303 detected ... Check your wiring!");
    while(1);
  }
  // Ask for firmware version
  Serial1.println(PMTK_Q_RELEASE);

  strip.begin();
  strip.show(); // Initialize all pixels to 'off'
  Serial.println("Setup complete");

}

uint32_t gpsTimer = millis();
uint32_t startupTimer = millis();
uint32_t compassTimer = millis();

void loop() // run over and over again
{
  ble.println("AT+BLEUARTRX");
  ble.readline();
  if (strcmp(ble.buffer, "OK") == 0) {
    return;
  }
 
  else if(strcmp(ble.buffer, "ERROR")==0) {
    // no data
    Serial.println("ERROR");
    Serial.println(eof);
    return;
  } 
  // Some data was found, its in the buffer
  Serial.print(F("[Recv] ")); 
  char* temp = ble.buffer;
  Serial.println(temp);

//read in all steps.

//
  if(expectGps) {
      sLat =  atof(strtok(temp, ","));
      sLon =  atof(strtok(NULL, ","));
      expectGps = 0;
  }
  else if(strcmp(ble.buffer, "EOF") == 0) {
      eof = 1;
  }
  else {
    latitude[next] = atof(strtok(temp, ","));
    longitude[next] = atof(strtok(NULL, ","));
    next++;
  }
  
  ble.waitForOK();

  float testNum = 12.345667890;
  Serial.print("test: ");
  Serial.println(testNum);

  compassCheck();
//supply target here
//supply current here
  if(eof && !expectGps) {

    targetLat = latitude[current];
    targetLon = longitude[current];
    tripDistance = (double)calc_dist(sLat, sLon, targetLat, targetLon);

    Serial.println(tripDistance);
    start = 1;
    //gets gps lat and long
    
    
    Serial.print("Latitude: ");
    Serial.println(sLat);
    Serial.print("Longitude: ");
    Serial.println(sLon);

    Serial.print("Target Lat: ");
    Serial.println(targetLat);
    Serial.print("Target Long: ");
    Serial.println(targetLon);
  
    navMode();
    //wait 
    Serial.println("Sent");
    ble.print("AT+BLEUARTTX=");
    ble.println("1");
    ble.waitForOK();
    expectGps = 1;
  }
}


void navMode() {
  compassCheck();

  headingDistance((double)calc_dist(sLat, sLon, targetLat, targetLon));

  if ((calc_bearing(sLat, sLon, targetLat, targetLon) - compassReading) > 0) {
    compassDirection(calc_bearing(sLat, sLon, targetLat, targetLon)-compassReading);
  } 
  else {
    compassDirection(calc_bearing(sLat, sLon, targetLat, targetLon)-compassReading+360);
  }

}

int calc_bearing(float flat1, float flon1, float flat2, float flon2)
{
  float calc;
  float bear_calc;

  float x = 69.1 * (flat2 - flat1); 
  float y = 69.1 * (flon2 - flon1) * cos(flat1/57.3);

  calc=atan2(y,x);

  bear_calc= degrees(calc);

  if(bear_calc<=1){
    bear_calc=360+bear_calc; 
  }
  return bear_calc;
}
void headingDistance(double fDist)
{
  Serial.println("Distance Left: ");
  Serial.println(fDist);
  //Use this part of the code to determine how far you are away from the destination.
  //The total trip distance (from where you started) is divided into five trip segments.
  float tripSegment = tripDistance/5;

  if (fDist >= (tripSegment*4)) {
    dirLED_r = 255;
    dirLED_g = 0;
    dirLED_b = 0;
  }

  if ((fDist >= (tripSegment*3))&&(fDist < (tripSegment*4))) {
    dirLED_r = 255;
    dirLED_g = 0;
    dirLED_b = 0;
  }

  if ((fDist >= (tripSegment*2))&&(fDist < (tripSegment*3))) {
    dirLED_r = 255;
    dirLED_g = 255;
    dirLED_b = 0;
  }

  if ((fDist >= tripSegment)&&(fDist < (tripSegment*2))) {
    dirLED_r = 255;
    dirLED_g = 255;
    dirLED_b = 0;
  }

  if ((fDist >= 5)&&(fDist < tripSegment)) {
    dirLED_r = 255;
    dirLED_g = 255;
    dirLED_b = 0;
  }

  if ((fDist < 5)) { // You are now within 5 meters of your destination.
    Serial.println("Arrived at destination!");
    //within five meters go to next location
    if(current < next) {
      current++;
    }
    dirLED_r = 0;
    dirLED_g = 255;
    dirLED_b = 0;
  }
}


unsigned long calc_dist(float flat1, float flon1, float flat2, float flon2)
{
  float dist_calc=0;
  float dist_calc2=0;
  float diflat=0;
  float diflon=0;

  diflat=radians(flat2-flat1);
  flat1=radians(flat1);
  flat2=radians(flat2);
  diflon=radians((flon2)-(flon1));

  dist_calc = (sin(diflat/2.0)*sin(diflat/2.0));
  dist_calc2= cos(flat1);
  dist_calc2*=cos(flat2);
  dist_calc2*=sin(diflon/2.0);
  dist_calc2*=sin(diflon/2.0);
  dist_calc +=dist_calc2;

  dist_calc=(2*atan2(sqrt(dist_calc),sqrt(1.0-dist_calc)));

  dist_calc*=6371000.0; //Converting to meters
  return dist_calc;
}


void compassCheck() {
  // if millis() or timer wraps around, we'll just reset it
  if (compassTimer > millis()) compassTimer = millis();

  // approximately every 10 seconds or so, update time
  if (millis() - compassTimer > 50) {
    /* Get a new sensor event */
    sensors_event_t event; 
    mag.getEvent(&event);

    float Pi = 3.14159;

    compassTimer = millis(); // reset the timer

    // Calculate the angle of the vector y,x
    float heading = (atan2(event.magnetic.y + magyOffset,event.magnetic.x + magxOffset) * 180) / Pi;

    // Normalize to 0-360
    if (heading < 0)
    {
      heading = 360 + heading;
    }
    compassReading = heading; 
  }  
}  

void compassDirection(int compassHeading) 
{
  Serial.print("Compass Direction: ");
  Serial.println(compassHeading);

  unsigned int ledDir = 2;
  int tempDir = 0;
  //Use this part of the code to determine which way you need to go.
  //Remember: this is not the direction you are heading, it is the direction to the destination (north = forward).

  if ((compassHeading > 348.75)||(compassHeading < 11.25)) {
      tempDir = topLED;
  }
  for(int i = 1; i < 16; i++){
    float pieSliceCenter = 45/2*i;
    float pieSliceMin = pieSliceCenter - 11.25;
    float pieSliceMax = pieSliceCenter + 11.25;
    if ((compassHeading >= pieSliceMin)&&(compassHeading < pieSliceMax)) {
      tempDir = topLED + i;
      
    }
  }

  if (tempDir > 15) {
    ledDir = tempDir - 16;
  }

  else if (tempDir < 0) {
    ledDir = tempDir + 16;
  } 
  else {
    ledDir = tempDir;
  }

  ledDir = ledDir + compassOffset;
  if (ledDir > 15) {
    ledDir = ledDir - 16;
  }


  if (lastDir != ledDir) {
    Serial.print("LED DIR: ");
    Serial.println(ledDir);
    strip.setPixelColor(lastDir, strip.Color(0, 0, 0));
    strip.setPixelColor(ledDir, strip.Color(dirLED_r, dirLED_g, dirLED_b));
    strip.show();
    lastDir = ledDir;
    Serial.println("Displayed direction");
  }
}
