#include "HLW8012.h"


HLW8012 hlw8012;

float getEnergy() {
  float busvoltage = (float)hlw8012.getVoltage();
  float current_A = (float)hlw8012.getCurrent();
  float kwh = (busvoltage * current_A) / 1000;
  return kwh;
}

