#include <TimeLib.h>
#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <NTPClient.h>
#include <ArduinoJson.h>
#include <PubSubClient.h>
#include <Time.h>
#include <Timezone.h>
#include "settings.h"

int16_t utc = 1;

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);

TimeChangeRule CEST = {"CEST", Last, Sun, Mar, 2, 120};
TimeChangeRule CET = {"CET", Last, Sun, Oct, 3, 60};
Timezone CE(CEST, CET);

unsigned long lastReport;

float kwh = 0.0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);

  Serial.println("cheguei");

  WiFi.mode(WIFI_STA);

  WiFi.begin(NETWORK, SSID_PASSWORD);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println("Connecting to WiFi..");
  }

  Serial.println("Connected to the WiFi network");

  pinMode(LED_PIN, OUTPUT);
  pinMode(REL_PIN, OUTPUT);
  pinMode(BUTTON, INPUT);

  digitalWrite(LED_PIN, 1);
  digitalWrite(REL_PIN, 0);


  timeClient.begin();
  timeClient.update();

  lastReport = timeClient.getEpochTime();

  client.subscribe("turn");
  client.setCallback(callback);

  hlw8012.begin(CF_PIN, CF1_PIN, SEL_PIN, CURRENT_MODE, false, 500000);
}

void loop() {
  checkButton();
  Serial.println(WiFi.localIP());

  if (checkInterval()) {
    sendConsumption();
  }
  //  if (!digitalRead(BUTTON))
  //  {
  //    Serial.println("cheguei");
  //    digitalWrite(LED_PIN, !digitalRead(LED_PIN));
  //    digitalWrite(REL_PIN, !digitalRead(REL_PIN));
  //    delay(100);
  //    while (!digitalRead(BUTTON));
  //    sendConsumption();
  //  }
  kwh = kwh + getEnergy();

  Serial.println(kwh);

  delay(500);

}


void checkButton() {
  if (digitalRead(BUTTON)) {
    WiFi.beginSmartConfig();
  }
}

boolean checkInterval() {
  if (timeClient.getEpochTime() - lastReport >= INTERVAL) {
    return true;
  }
  return false;
}

void sendConsumption() {
  unsigned long utc = timeClient.getEpochTime();
  TimeChangeRule *tcr, *tcr2;
  time_t t, lr;
  t = utc;
  t = CE.toLocal(t, &tcr);
  lr = lastReport;
  lr = CE.toLocal(lr, &tcr2);
  char from[15];
  char to[15];
  sprintf(to, "%d%02d%02d%02d%02d%02d", year(t), month(t), day(t), hour(t), minute(t), second(t));
  sprintf(from, "%d%02d%02d%02d%02d%02d", year(lr), month(lr), day(lr), hour(lr), minute(lr), second(lr));
//  Serial.println(from);
//  Serial.println(to);
  delay(1000);
  if (sendConsumptionData(kwh, from, to)) {
    lastReport = utc;
    kwh = 0;
  }
}

