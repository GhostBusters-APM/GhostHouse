
#include <TimeLib.h>
#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <NTPClient.h>
#include <ArduinoJson.h>
#include <PubSubClient.h>
#include <Time.h>
#include <Timezone.h>
#include <MQTT.h>
#include <uMQTTBroker.h>


int16_t utc = 1;

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);

TimeChangeRule CEST = {"CEST", Last, Sun, Mar, 2, 120};
TimeChangeRule CET = {"CET", Last, Sun, Oct, 3, 60};
Timezone CE(CEST, CET);

IPAddress apLocalIP(192, 168, 0, 1);
IPAddress subnet(255, 255, 255, 0);

unsigned long lastReport;

float kwh = 0.0;

void checkButton() {
  if (!digitalRead(BUTTON)) {
    WiFi.softAPConfig(apLocalIP, apLocalIP, subnet);
    WiFi.mode(WIFI_AP);
    WiFi.softAP("SONOFF001");
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
  Serial.println(from);
  Serial.println(to);
  delay(1000);
  if (sendConsumptionData(kwh, from, to)) {
    lastReport = utc;
    kwh = 0;
  }
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);

  Serial.println("cheguei");

  if (NETWORK != "") {
    WiFi.mode(WIFI_STA);

    char ssid[100];
    char password[100];

    NETWORK.toCharArray(ssid, 100);
    SSID_PASSWORD.toCharArray(password, 100);
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.println("Connecting to WiFi..");

      Serial.println(NETWORK);
    }
    Serial.println(WiFi.status());
    Serial.println(WiFi.localIP());
    delay(500);
    Serial.println("Connected to the WiFi network");
  } else {
    WiFi.softAPConfig(apLocalIP, apLocalIP, subnet);
    WiFi.mode(WIFI_AP);
    WiFi.softAP("SONOFF001");
  }
  pinMode(LED_PIN, OUTPUT);
  pinMode(REL_PIN, OUTPUT);
  pinMode(BUTTON, INPUT);

  timeClient.begin();

  if (WiFi.status() == WL_CONNECTED) {
    while (!timeClient.update()) {

      Serial.println("Updating time");
    }
  }
  //  delay(5000);

  lastReport = timeClient.getEpochTime();

  hlw8012.begin(CF_PIN, CF1_PIN, SEL_PIN, CURRENT_MODE, false, 500000);

  MQTT_server_onData(callback);
  while (!MQTT_server_start(1883, 30, 30)) {
    Serial.println("Iniciando Broker MQTT");;
  }
  MQTT_local_subscribe((unsigned char *)"#", 0);

  digitalWrite(LED_PIN, 1);
  digitalWrite(REL_PIN, 0);
}

void loop() {
  checkButton();
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println("Connecting to WiFi..");

    Serial.println(NETWORK);
  }
  if (!reg) {
    sendRegistration();
    if (WiFi.status() == WL_CONNECTED) {
      while (!timeClient.update()) {

        Serial.println("Updating time");
      }
    }
    lastReport = timeClient.getEpochTime();
    kwh = 0;
    MQTT_server_onData(callback);
    while (!MQTT_server_start(1883, 30, 30)) {
      Serial.println("Iniciando Broker MQTT");;
    }
    MQTT_local_subscribe((unsigned char *)"#", 0);
    reg = true;
  }
  Serial.println("Connected to the WiFi network");
  Serial.println(WiFi.status());
  Serial.println(WiFi.localIP());
  Serial.println(NETWORK);
  Serial.println(SSID_PASSWORD);
  Serial.println(deviceId);
  Serial.println(name);
  Serial.println(latitude);
  Serial.println(longitude);
  TimeChangeRule *tcr;
  time_t t;
  t = lastReport;
  t = CE.toLocal(t, &tcr);
  Serial.printf("%d%02d%02d%02d%02d%02d\n", year(t), month(t), day(t), hour(t), minute(t), second(t));



  if (checkInterval()) {
    sendConsumption();
  }
  //    if (!digitalRead(BUTTON))
  //    {
  //      digitalWrite(LED_PIN, !digitalRead(LED_PIN));
  //      digitalWrite(REL_PIN, !digitalRead(REL_PIN));
  //      delay(100);
  //      while (!digitalRead(BUTTON));
  //      sendConsumption();
  //    }
  kwh = kwh + getEnergy();

  Serial.println(kwh);

  delay(500);

}


