#include "settings.h"
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <MQTT.h>
#include <uMQTTBroker.h>


WiFiClient espClient;
PubSubClient client(espClient);

boolean sendConsumptionData(float kwh, char* from, char* to) {
  boolean res;

  StaticJsonBuffer<500> JSONbuffer;
  JsonObject& JSONencoder = JSONbuffer.createObject();

  JSONencoder["deviceId"] = deviceId;
  JSONencoder["kwh"] = kwh;
  JSONencoder["from"] = from;
  JSONencoder["to"] = to;

  char JSONmessageBuffer[100];
  JSONencoder.printTo(JSONmessageBuffer, sizeof(JSONmessageBuffer));
  Serial.println("Sending message HTTP server..");
  Serial.println(JSONmessageBuffer);

  HTTPClient http;    //Declare object of class HTTPClient

  http.begin("http://alexperal.com:8888/devicePower");      //Specify request destination
  http.addHeader("Content-Type", "application/json");  //Specify content-type header

  int httpCode = http.POST(JSONmessageBuffer);   //Send the request
  String payload = http.getString();                  //Get the response payload

  Serial.println(httpCode);   //Print HTTP return code
  Serial.println(payload);    //Print request response payload

  http.end();
  return res;
}

void callback(uint32_t *client, const char* topic, uint32_t topic_len, const char *data, uint32_t lengh) {
  char topic_str[topic_len + 1];
  os_memcpy(topic_str, topic, topic_len);
  topic_str[topic_len] = '\0';

  char data_str[lengh + 1];
  os_memcpy(data_str, data, lengh);
  data_str[lengh] = '\0';

  if (!strcmp(topic_str, "REGISTER")) {
    StaticJsonBuffer<500> jsonStringBuffer;
    JsonObject& jsonString = jsonStringBuffer.parseObject(data_str);


    const char* ssid = jsonString["ssid"];
    const char* ssid_password = jsonString["password"];

    Serial.println(ssid);
    Serial.println(ssid_password);

    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid, ssid_password);

    NETWORK = jsonString.get<String>("ssid");
    SSID_PASSWORD = jsonString.get<String>("password");
    userId = jsonString.get<String>("userId");
    name = jsonString.get<String>("name");
    latitude = jsonString.get<String>("latitude");
    longitude = jsonString.get<String>("longitude");
    type = 1;
    state = false;
    reg = false;


    if (jsonString.success()) {
      Serial.println("exito");
    } else {
      Serial.println("error");
    }
  } else if (!strcmp(topic_str, "SWITCH")) {
    if (!strcmp(data_str, "ON")) {
      Serial.println("encendido");
      digitalWrite(REL_PIN, HIGH);
      //      digitalWrite(LED_PIN, !digitalRead(LED_PIN));
      //      digitalWrite(REL_PIN, !digitalRead(REL_PIN));
    }
    else if (!strcmp(data_str, "OFF")) {
      Serial.println("apagado");
      digitalWrite(REL_PIN, LOW);
      //      digitalWrite(LED_PIN, !digitalRead(LED_PIN));
      //      digitalWrite(REL_PIN, !digitalRead(REL_PIN));
    }
  } else if (!strcmp(topic_str, "ALIVE")) {

    MQTT_local_publish((unsigned char *)"ALIVE/ACK", (unsigned char *)"alive", 5, 0, 0);
    Serial.println("Sending alive");
  }
}

void sendRegistration() {

  Serial.println("registrado");
  StaticJsonBuffer<500> jsonEncoderBuffer;
  JsonObject& JSONencoder = jsonEncoderBuffer.createObject();

  JSONencoder["userId"] = userId;
  JSONencoder["name"] = name;
  JSONencoder["latitude"] = latitude;
  JSONencoder["longitude"] = longitude;
  JSONencoder["type"] = type;
  JSONencoder["state"] = state;
  JSONencoder["ip"] = WiFi.localIP().toString();


  char JSONmessageBuffer[1000];
  JSONencoder.printTo(JSONmessageBuffer, sizeof(JSONmessageBuffer));
  Serial.println("Sending message HTTP server..");
  Serial.println(JSONmessageBuffer);

  HTTPClient http;    //Declare object of class HTTPClient

  http.begin("http://alexperal.com:8888/device");      //Specify request destination
  http.addHeader("Content-Type", "application/json");  //Specify content-type header
  Serial.println("chega aqui");

  int httpCode = http.POST(JSONmessageBuffer);   //Send the request
  String payload = http.getString();                  //Get the response payload
  Serial.println("chega aqui tamen");

  Serial.println(httpCode);   //Print HTTP return code
  Serial.println(payload);    //Print request response payload

  http.end();

  StaticJsonBuffer<1000> jsonResponseBuffer;
  JsonObject& jsonResponse = jsonResponseBuffer.parseObject(payload);

  deviceId = jsonResponse["deviceId"];

  Serial.println(deviceId);
}

