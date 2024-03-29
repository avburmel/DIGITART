
#include "leds.h"
#include "config.h"
#include "settings.h"

Adafruit_NeoPixel strip(CONFIG_LED_NUMBER, CONFIG_LED_PIN, NEO_GRB + NEO_KHZ800);

hw_timer_t *timerGlobal = NULL;
volatile uint32_t timerGlobalCnt = 0;
volatile uint32_t timerGlobalCntPrev = timerGlobalCnt - 1;

Settings ledsProfile;

void IRAM_ATTR timerGlobalCB()
{
  timerGlobalCnt++;
}

void ledsSettingsUpdate(void)
{
  settingsGet((Settings*)&ledsProfile);
}

static void ledsBegin(void)
{
  strip.begin();
}

static void ledsClear(void)
{
  strip.clear();
}

static void ledsStateShow(void)
{
  strip.show();
}

void ledsSetBright(uint8_t bright)
{
  strip.setBrightness(bright);
  ledsClear();
  ledsStateShow();
}

void ledsInit(void)
{
  ledsBegin();
  ledsSettingsUpdate();
  ledsSetBright(0);
  timerGlobal = timerBegin(0, 8000, true);
  timerAttachInterrupt(timerGlobal, &timerGlobalCB, true);
  timerAlarmWrite(timerGlobal, 100, true);
  timerAlarmEnable(timerGlobal);
}

static int ledsStateCalc(void)
{
  uint32_t quant, middlePoint, duty, state;
  float brightK;
  uint32_t colorsPrev[CONFIG_LED_NUMBER] = {0};

  for(int i = 0; i < CONFIG_LED_NUMBER; i++)
  {
    colorsPrev[i] = strip.getPixelColor(i) & 0xFFFFFF;
    state = 0;
    quant = timerGlobalCnt % ledsProfile.config[i].period;
    if (ledsProfile.config[i].TSStart < ledsProfile.config[i].TSEnd)
    {
      if ((quant >= ledsProfile.config[i].TSStart) && (quant <= ledsProfile.config[i].TSEnd))
      {
        duty = ledsProfile.config[i].TSEnd - ledsProfile.config[i].TSStart;
        brightK = (float)(quant - ledsProfile.config[i].TSStart) / (float)duty;
        state = 1;
      }       
    }
    else
    {
      duty = ledsProfile.config[i].TSEnd + (ledsProfile.config[i].period - ledsProfile.config[i].TSStart);
      if (quant <= ledsProfile.config[i].TSEnd)
      {    
        brightK = (float)(quant + (ledsProfile.config[i].period - ledsProfile.config[i].TSStart)) / (float)duty;
        state = 1;
      }
      else if (quant >= ledsProfile.config[i].TSStart)
      {
        brightK = (float)(quant - ledsProfile.config[i].TSStart) / (float)duty;
        state = 1;
      }
    }
    if (state)
    {
      if (ledsProfile.config[i].smooth == MODE_STABLE)
        strip.setPixelColor(i, ledsProfile.config[i].color[0], ledsProfile.config[i].color[1], ledsProfile.config[i].color[2]);
      else if ((ledsProfile.config[i].smooth == MODE_RISING) || (ledsProfile.config[i].smooth == MODE_RISING_INV))
        strip.setPixelColor(i, ledsProfile.config[i].color[0] * brightK, ledsProfile.config[i].color[1] * brightK, ledsProfile.config[i].color[2] * brightK);
      else if ((ledsProfile.config[i].smooth == MODE_FALLING) || (ledsProfile.config[i].smooth == MODE_FALLING_INV))
        strip.setPixelColor(i, ledsProfile.config[i].color[0] * (1.0 - brightK), ledsProfile.config[i].color[1] * (1.0 - brightK), ledsProfile.config[i].color[2] * (1.0 - brightK));
      else
      {
        if ((ledsProfile.config[i].smooth == MODE_RISING_FALLING) || (ledsProfile.config[i].smooth == MODE_RISING_FALLING_INV))
        {
          if (quant >= ledsProfile.config[i].TSStart)
          {
            if ((quant - ledsProfile.config[i].TSStart) < (duty >> 1))
              strip.setPixelColor(i, ledsProfile.config[i].color[0] * brightK * 2, ledsProfile.config[i].color[1] * brightK * 2, ledsProfile.config[i].color[2] * brightK * 2);
            else
              strip.setPixelColor(i, ledsProfile.config[i].color[0] * ((1.0 - brightK) * 2), ledsProfile.config[i].color[1] * ((1.0 - brightK) * 2), ledsProfile.config[i].color[2] * ((1.0 - brightK) * 2));            
          }
          else
          {
            if ((ledsProfile.config[i].TSEnd - quant) >= (duty >> 1))
              strip.setPixelColor(i, ledsProfile.config[i].color[0] * brightK * 2, ledsProfile.config[i].color[1] * brightK * 2, ledsProfile.config[i].color[2] * brightK * 2);
            else
              strip.setPixelColor(i, ledsProfile.config[i].color[0] * ((1.0 - brightK) * 2), ledsProfile.config[i].color[1] * ((1.0 - brightK) * 2), ledsProfile.config[i].color[2] * ((1.0 - brightK) * 2));
          }
        }
        if ((ledsProfile.config[i].smooth == MODE_FALLING_RISING) || (ledsProfile.config[i].smooth == MODE_FALLING_RISING_INV))
        {
          if (quant >= ledsProfile.config[i].TSStart)
          {
            if ((quant - ledsProfile.config[i].TSStart) > (duty >> 1))
              strip.setPixelColor(i, ledsProfile.config[i].color[0] * (float)((brightK - 0.5) * 2), ledsProfile.config[i].color[1] * (float)((brightK - 0.5) * 2), ledsProfile.config[i].color[2] * (float)((brightK - 0.5) * 2));
            else
              strip.setPixelColor(i, ledsProfile.config[i].color[0] * (float)(1.0 - brightK * 2), ledsProfile.config[i].color[1] * (float)(1.0 - brightK * 2), ledsProfile.config[i].color[2] * (float)(1.0 - brightK * 2));            
          }
          else
          {
            if ((ledsProfile.config[i].TSEnd - quant) <= (duty >> 1))
              strip.setPixelColor(i, ledsProfile.config[i].color[0] * (float)((brightK - 0.5) * 2), ledsProfile.config[i].color[1] * (float)((brightK - 0.5) * 2), ledsProfile.config[i].color[2] * (float)((brightK - 0.5) * 2));
            else
              strip.setPixelColor(i, ledsProfile.config[i].color[0] * (float)(1.0 - brightK * 2), ledsProfile.config[i].color[1] * (float)(1.0 - brightK * 2), ledsProfile.config[i].color[2] * (float)(1.0 - brightK * 2));
          }
        }
      }
    }
    else
    {
      if (ledsProfile.config[i].smooth > MODE_FALLING_RISING)
        strip.setPixelColor(i, ledsProfile.config[i].color[0], ledsProfile.config[i].color[1], ledsProfile.config[i].color[2]);
      else
        strip.setPixelColor(i, 0x00, 0x00, 0x00);
    }
      
    
  }

  //NEED TO UPD?
  for (int i = 0; i < CONFIG_LED_NUMBER; i++)
  {
    uint32_t color = strip.getPixelColor(i) & 0xFFFFFF;
    if (colorsPrev[i] != color)
      return 1;
  }
  return 0;
}

static uint8_t ledsBrightController(void)
{
  static uint8_t bright = 0;
  if (ledsProfile.time.isTimeMode == 0)
  {
    if (bright != ledsProfile.bright)
    {
      bright = ledsProfile.bright;
      ledsSetBright(bright);
    }
  }
  else
  {
    RTC_Time time;
    rtcGet(&time);
    if (rtcTimeCmp(&ledsProfile.time.timeFrom, &ledsProfile.time.timeTo) == 1)
    {
      if ((rtcTimeCmp(&ledsProfile.time.timeFrom, &time)) && rtcTimeCmp(&time, &ledsProfile.time.timeTo))
      {
        if (bright != ledsProfile.bright)
        {
          bright = ledsProfile.bright;
          ledsSetBright(bright);
        }
      }
      else
      {
        if (bright != 0)
        {
          bright = 0;
          ledsSetBright(bright);
        }
      }
    }
    else
    {
      if ((rtcTimeCmp(&ledsProfile.time.timeTo, &time) && rtcTimeCmp(&ledsProfile.time.timeFrom, &time)) || \
          (rtcTimeCmp(&time, &ledsProfile.time.timeTo) && rtcTimeCmp(&time, &ledsProfile.time.timeFrom)))
      {
        if (bright != ledsProfile.bright)
        {
          bright = ledsProfile.bright;
          ledsSetBright(bright);
        }
      }
      else
      {
        if (bright != 0)
        {
          bright = 0;
          ledsSetBright(bright);
        }
      }
    }
  }   
  return bright;
}

static int ledsGetPermissionToWork(void)
{
  static int state = 1;
  uint8_t bright = ledsBrightController();
  if (state)
  {
    if (bright == 0)
      state = 0;
  }
  else
  {
    if (bright != 0)
      state = 1;
  }
  return state;
}

void ledsProcess(void)
{ 
  if (timerGlobalCnt != timerGlobalCntPrev)
  {
    if (ledsGetPermissionToWork())
    {
      if (ledsStateCalc())
        ledsStateShow();
      timerGlobalCntPrev = timerGlobalCnt;
    }
  }
    
}