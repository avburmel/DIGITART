
#ifndef RTC_H
#define RTC_H

#include <Arduino.h>

struct RTC_Time {
  uint8_t     sec;
  uint8_t     min;
  uint8_t     hour;
};

void rtcSet(uint32_t time);
void rtcGet(RTC_Time *time);
int  rtcTimeCmp(RTC_Time *time0, RTC_Time *time1);

#endif
