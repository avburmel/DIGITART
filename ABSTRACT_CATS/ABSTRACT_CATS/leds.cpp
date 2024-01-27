
#include "leds.h"
#include "config.h"
#include "settings.h"

Adafruit_NeoPixel strip(CONFIG_LED_NUMBER, CONFIG_LED_PIN, NEO_GRB + NEO_KHZ800);

hw_timer_t *timerGlobal = NULL;
uint32_t timerGlobalCnt = 0;
uint32_t timerGlobalCntPrev = timerGlobalCnt - 1;

Settings ledsProfile;

void IRAM_ATTR timerGlobalCB()
{
  timerGlobalCnt++;
}

void ledsSettingsUpdate(void)
{
  settingsGet(&ledsProfile);
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

static void ledsSetBright(uint8_t bright)
{
  strip.setBrightness(bright);
}

void ledsInit(void)
{
  ledsBegin();
  ledsClear();
  ledsSettingsUpdate();
  ledsStateShow();
  timerGlobal = timerBegin(0, 8000, true);
  timerAttachInterrupt(timerGlobal, &timerGlobalCB, true);
  timerAlarmWrite(timerGlobal, 100, true);
  timerAlarmEnable(timerGlobal);
}

static void ledsStateCalc(void)
{
  uint32_t quant, middlePoint, duty;
  int state = 0;
  float brightK;

  for(int i = 0; i < CONFIG_LED_NUMBER; i++)
  {
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
      if (ledsProfile.config[i].smooth == 0)
        strip.setPixelColor(i, strip.Color(ledsProfile.config[i].color[0], ledsProfile.config[i].color[1], ledsProfile.config[i].color[2]));
      else if (ledsProfile.config[i].smooth == 1)
        strip.setPixelColor(i, strip.Color(ledsProfile.config[i].color[0] * brightK, ledsProfile.config[i].color[1] * brightK, ledsProfile.config[i].color[2] * brightK));
      else if (ledsProfile.config[i].smooth == 2)
        strip.setPixelColor(i, strip.Color(ledsProfile.config[i].color[0] * (1.0 - brightK), ledsProfile.config[i].color[1] * (1.0 - brightK), ledsProfile.config[i].color[2] * (1.0 - brightK)));
      else
      {
        if (ledsProfile.config[i].smooth == 3)
        {
          if (quant >= ledsProfile.config[i].TSStart)
          {
            if ((quant - ledsProfile.config[i].TSStart) < (duty >> 1))
              strip.setPixelColor(i, strip.Color(ledsProfile.config[i].color[0] * brightK * 2, ledsProfile.config[i].color[1] * brightK * 2, ledsProfile.config[i].color[2] * brightK * 2));
            else
              strip.setPixelColor(i, strip.Color(ledsProfile.config[i].color[0] * ((1.0 - brightK) * 2), ledsProfile.config[i].color[1] * ((1.0 - brightK) * 2), ledsProfile.config[i].color[2] * ((1.0 - brightK) * 2)));            
          }
          else
          {
            if ((ledsProfile.config[i].TSEnd - quant) >= (duty >> 1))
              strip.setPixelColor(i, strip.Color(ledsProfile.config[i].color[0] * brightK * 2, ledsProfile.config[i].color[1] * brightK * 2, ledsProfile.config[i].color[2] * brightK * 2));
            else
              strip.setPixelColor(i, strip.Color(ledsProfile.config[i].color[0] * ((1.0 - brightK) * 2), ledsProfile.config[i].color[1] * ((1.0 - brightK) * 2), ledsProfile.config[i].color[2] * ((1.0 - brightK) * 2)));
          }
        }
        if (ledsProfile.config[i].smooth == 4)
        {
          if (quant >= ledsProfile.config[i].TSStart)
          {
            if ((quant - ledsProfile.config[i].TSStart) > (duty >> 1))
              strip.setPixelColor(i, strip.Color(ledsProfile.config[i].color[0] * (float)((brightK - 0.5) * 2), ledsProfile.config[i].color[1] * (float)((brightK - 0.5) * 2), ledsProfile.config[i].color[2] * (float)((brightK - 0.5) * 2)));
            else
              strip.setPixelColor(i, strip.Color(ledsProfile.config[i].color[0] * (float)(1.0 - brightK * 2), ledsProfile.config[i].color[1] * (float)(1.0 - brightK * 2), ledsProfile.config[i].color[2] * (float)(1.0 - brightK * 2)));            
          }
          else
          {
            if ((ledsProfile.config[i].TSEnd - quant) <= (duty >> 1))
              strip.setPixelColor(i, strip.Color(ledsProfile.config[i].color[0] * (float)((brightK - 0.5) * 2), ledsProfile.config[i].color[1] * (float)((brightK - 0.5) * 2), ledsProfile.config[i].color[2] * (float)((brightK - 0.5) * 2)));
            else
              strip.setPixelColor(i, strip.Color(ledsProfile.config[i].color[0] * (float)(1.0 - brightK * 2), ledsProfile.config[i].color[1] * (float)(1.0 - brightK * 2), ledsProfile.config[i].color[2] * (float)(1.0 - brightK * 2)));
          }
        }
      }
    }
    else
      strip.setPixelColor(i, strip.Color(0x00, 0x00, 0x00));
  }
}

void ledsProcess(void)
{ 
  if (timerGlobalCnt != timerGlobalCntPrev)
  {
    ledsStateCalc();
    ledsStateShow();
    timerGlobalCntPrev = timerGlobalCnt;
  }
}
