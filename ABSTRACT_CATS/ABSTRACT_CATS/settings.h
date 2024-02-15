
#ifndef SETTINGS_H
#define SETTINGS_H

#include <Arduino.h>
#include "config.h"
#include "rtc.h"

enum led_mode_t 
{
  MODE_STABLE = 0,
  MODE_RISING,
  MODE_FALLING,
  MODE_RISING_FALLING,
  MODE_FALLING_RISING,
  MODE_RISING_INV,
  MODE_FALLING_INV,
  MODE_RISING_FALLING_INV,
  MODE_FALLING_RISING_INV
};

struct ledSettings {
  led_mode_t  smooth; //0 is stable bright, 1 is smooth bright from 0 to ledBright, 2 is smooth bright from ledBright to 0
  uint8_t  color[3]; //RGB bytes
  uint32_t period; //unit is quant in #define LED_TIME_QUANT
  uint32_t TSStart; //unit is quant in #define LED_TIME_QUANT
  uint32_t TSEnd; //unit is quant in #define LED_TIME_QUANT
};

struct Time {
  RTC_Time    timeFrom;
  RTC_Time    timeTo;
  uint8_t     isTimeMode;
};

struct Settings {
  ledSettings config[CONFIG_LED_NUMBER];
  uint8_t     bright;
  Time        time;
  uint16_t    crc;
};

void settingsInit(void);
void settingsSave(void);
void settingsRead(void);
void settingsGet(Settings* config);
void settingsLedGet(ledSettings* config, uint32_t num);
void settingsLedSet(ledSettings* config, uint32_t num);
void settingsSet(Settings* config);
void settingsTimeSet(Time* time);
void settingsBrightSet(uint8_t bright);

#endif
