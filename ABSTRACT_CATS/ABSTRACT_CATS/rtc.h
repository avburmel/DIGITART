
#ifndef RTC_H
#define RTC_H

#include <Arduino.h>

struct RTC_Time {
  uint8_t     sec;
  uint8_t     min;
  uint8_t     hour;
};

void rtcSet(uint8_t s, uint8_t min, uint8_t hour);
void rtcGet(RTC_Time *time);

#endif
