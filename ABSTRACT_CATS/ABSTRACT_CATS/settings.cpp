
#include "settings.h"
#include <EEPROM.h>

Settings settings;

unsigned short CRC16(const unsigned char* data_p, unsigned char length){
    unsigned char x;
    unsigned short crc = 0xFFFF;

    while (length--) {
        x = crc >> 8 ^ *data_p++;
        x ^= x>>4;
        crc = (crc << 8) ^ ((unsigned short)(x << 12)) ^ ((unsigned short)(x <<5)) ^ ((unsigned short)x);
    }
    return crc;
}

static bool settingsCRCIsCorrect(void)
{
  if (CRC16((uint8_t*)(&settings), sizeof(Settings) - sizeof(uint16_t)) == settings.crc)
    return true;
  else
    return false;
}

void settingsLedSet(ledSettings* config, uint32_t num)
{
  if (num < CONFIG_LED_NUMBER)
    memcpy(&settings.config[num], config, sizeof(ledSettings));
}

static void settingsDefaultSave(void)
{
  for (int i = 0; i < CONFIG_LED_NUMBER; i++)
  {
    settings.config[i].color[0] = 128;
    settings.config[i].color[1] = 0;
    settings.config[i].color[2] = 0;
    settings.config[i].smooth = 3;
    settings.config[i].period = 500;
    settings.config[i].TSStart = 0;
    settings.config[i].TSEnd = 500;
  }

  settings.time.isTimeMode = 0;
  
  settings.bright = 128;

  settingsSave();
}

void settingsInit(void)
{
  EEPROM.begin(sizeof(Settings));
  settingsRead();
  if (!settingsCRCIsCorrect())
    settingsDefaultSave();
}

void settingsGet(Settings* config)
{
  settings.crc = CRC16((uint8_t*)(&settings), sizeof(Settings) - sizeof(uint16_t));
  memcpy(config, &settings, sizeof(Settings));
}

void settingsSave(void)
{
  settings.crc = CRC16((uint8_t*)(&settings), sizeof(Settings) - sizeof(uint16_t));
  uint8_t* config = (uint8_t*)(&settings);
  for (int i = 0; i < sizeof(Settings); i++)
    EEPROM.write(i, config[i]);
  EEPROM.commit();
}

void settingsRead(void)
{
  uint8_t* config = (uint8_t*)(&settings);
  for (int i = 0; i < sizeof(Settings); i++)
    config[i] = EEPROM.read(i);
}

void settingsTimeSet(Time* time)
{
  memcpy(&settings.time, time, sizeof(Time));
}

void settingsBrightSet(uint8_t bright)
{
  settings.bright = bright;
}
