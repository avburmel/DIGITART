
#include "rtc.h"
#include <ESP32Time.h>

ESP32Time rtc(0);

void rtcSet(uint32_t time)
{
  int sec = time & 0xFF;
  int min = (time >> 8) & 0xFF;
  int hour = (time >> 16) & 0xFF;
  rtc.setTime(sec, min, hour, 1, 1, 1970);
}

void rtcGet(RTC_Time *time)
{
  time->sec = rtc.getSecond();
  time->min = rtc.getMinute();
  time->hour = rtc.getHour(true);
}

int rtcTimeCmp(RTC_Time *time0, RTC_Time *time1)
{
  if (time0->hour > time1->hour)
    return 0;
  else if (time0->hour < time1->hour)
    return 1;
  else
  {
    if (time0->min > time1->min)
      return 0;
    else if (time0->min < time1->min)
      return 1;
    else
    {
      if (time0->sec > time1->sec)
        return 0;
      else
        return 1;
    }
  }
}
