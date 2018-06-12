#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include "settings.h"

WiFiClient espClient;
PubSubClient client(espClient);

void connectServer() {
 
  Serial.println();
 
  client.setServer(SERVER, PORT);
 
  while (!client.connected()) {
    Serial.println("Connecting to MQTT...");
 
    if (client.connect(DEVICE_ID)) {
 
      Serial.println("connected");
 
    } else {
 
      Serial.print("failed with state ");
      Serial.print(client.state());
      delay(2000);
 
    }
  }
 
}
 
boolean sendConsumptionData(float kwh, char* from, char* to) {

  connectServer();

  boolean res;
 
  StaticJsonBuffer<500> JSONbuffer;
  JsonObject& JSONencoder = JSONbuffer.createObject();
 
  JSONencoder["device"] = DEVICE_ID;
  JSONencoder["kwh"] = kwh;
  JSONencoder["from"] = from;
  JSONencoder["to"] = to;
  
  char JSONmessageBuffer[100];
  JSONencoder.printTo(JSONmessageBuffer, sizeof(JSONmessageBuffer));
  Serial.println("Sending message to MQTT topic..");
  Serial.println(JSONmessageBuffer);
 
  if (client.publish("consum", JSONmessageBuffer) == true) {
    Serial.println("Success sending message");
    res=true;
  } else {
    Serial.println("Error sending message");
    res=false;
  }
 
  client.loop();
  Serial.println("-------------");
 
  delay(10000);

  client.disconnect();

  return res;
}

void callback(char* topic, byte* payload, unsigned int length) {
  if ((char*)payload == "on") {
    Serial.println("encendido");
    digitalWrite(REL_PIN, HIGH);
    digitalWrite(LED_PIN, !digitalRead(LED_PIN));
    digitalWrite(REL_PIN, !digitalRead(REL_PIN));
  }
  else if ((char*)payload == "off") {
    Serial.println("apagado");
    digitalWrite(REL_PIN, LOW);
    digitalWrite(LED_PIN, !digitalRead(LED_PIN));
    digitalWrite(REL_PIN, !digitalRead(REL_PIN));
  }
}
