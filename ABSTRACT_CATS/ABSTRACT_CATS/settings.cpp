
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
  {
    memcpy(&settings.config[num], config, sizeof(ledSettings));
    settings.crc = CRC16((uint8_t*)(&settings), sizeof(Settings) - sizeof(uint16_t));
  }  
}

static void raduga(void)
{
    for(int i = 2; i < CONFIG_LED_NUMBER; i+=4)
    {
      settings.config[i].color[0] = 128;
      settings.config[i].color[1] = 128;
      settings.config[i].color[2] = 128;
      settings.config[i + 1].color[0] = 128;
      settings.config[i + 1].color[1] = 128;
      settings.config[i + 1].color[2] = 128;
    }
    settings.config[0].color[0] = 255;
    settings.config[0].color[1] = 0;
    settings.config[0].color[2] = 0;
    settings.config[1].color[0] = 255;
    settings.config[1].color[1] = 0;
    settings.config[1].color[2] = 0;

    settings.config[4].color[0] = 255;
    settings.config[4].color[1] = 255;
    settings.config[4].color[2] = 0;
    settings.config[5].color[0] = 255;
    settings.config[5].color[1] = 255;
    settings.config[5].color[2] = 0;

    settings.config[8].color[0] = 252;
    settings.config[8].color[1] = 102;
    settings.config[8].color[2] = 0;
    settings.config[9].color[0] = 252;
    settings.config[9].color[1] = 102;
    settings.config[9].color[2] = 0;

    settings.config[12].color[0] = 0;
    settings.config[12].color[1] = 255;
    settings.config[12].color[2] = 0;
    settings.config[13].color[0] = 0;
    settings.config[13].color[1] = 255;
    settings.config[13].color[2] = 0;

    settings.config[16].color[0] = 0;
    settings.config[16].color[1] = 255;
    settings.config[16].color[2] = 255;
    settings.config[17].color[0] = 0;
    settings.config[17].color[1] = 255;
    settings.config[17].color[2] = 255;

    settings.config[20].color[0] = 0;
    settings.config[20].color[1] = 0;
    settings.config[20].color[2] = 255;
    settings.config[21].color[0] = 0;
    settings.config[21].color[1] = 0;
    settings.config[21].color[2] = 255;

    settings.config[22].color[0] = 255;
    settings.config[22].color[1] = 0;
    settings.config[22].color[2] = 255;
    settings.config[23].color[0] = 255;
    settings.config[23].color[1] = 0;
    settings.config[23].color[2] = 255;
}

static void settingsDefaultSave(void)
{
  for (int i = 0; i < CONFIG_LED_NUMBER; i++)
  {
    settings.config[i].smooth = 3;
    settings.config[i].period = 500;
    settings.config[i].TSStart = 0;
    settings.config[i].TSEnd = 500;
  }
  raduga();
  settings.bright = 128;
  settings.crc = CRC16((uint8_t*)(&settings), sizeof(Settings) - sizeof(uint16_t));
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
  memcpy(config, &settings, sizeof(Settings));
}

void settingsSave(void)
{
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
