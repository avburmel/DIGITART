
#include "rtc.h"
#include <ESP32Time.h>

ESP32Time rtc(0);

void rtcSet(uint8_t sec, uint8_t min, uint8_t hour)
{
  rtc.setTime(sec, min, hour, 1, 1, 1970);
}

void rtcGet(RTC_Time *time)
{
  time->sec = rtc.getSecond();
  time->min = rtc.getMinute();
  time->hour = rtc.getHour(false);
}
