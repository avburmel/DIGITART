
#include "protocol.h"
#include "settings.h"
#include "leds.h"
#include <cstring>

static bool ProtocolSigIsOK(protocolData* data)
{
  if (memcmp(data->protocolSignature, PROTOCOL_SIGNATURE, sizeof(PROTOCOL_SIGNATURE) - 1) == 0)
    return true;
  else
    return false;
}

static bool ProtocolVerIsOK(protocolData* data)
{
  if (memcmp(data->protocolVer, PROTOCOL_VER, sizeof(PROTOCOL_VER) - 1) == 0)
    return true;
  else
    return false;
}

static int ProtocolGetNumber(char* data, int* number)
{
  int res = 0;
  char digit;
  int cnt = 0;
  while ((*data >= '0') && (*data <= '9'))
  {
    digit = *data;
    res = (res * 10) + (digit - 0x30);
    data++;
    cnt++;
  }
  *number = res;
  return cnt;
}

static protocol_ret ProtocolParseDataCommand(char* data, commands_t commandCode)
{
  //DIGITART # 1 # SETTINGS # num 0: smooth 3: color 16777215: period 400: TSStart 0: TSEnd 400
  //DIGITART # 1 # SETTINGS_FOR_CAT # num 0: smooth 3: color 16777215: period 400: TSStart 0: TSEnd 400
  //DIGITART # 1 # SETTINGS_FOR_ALL # num 0: smooth 3: color 16777215: period 400: TSStart 0: TSEnd 400
  //DIGITART # 1 # SETTINGS_BRIGHT # bright 128
  //DIGITART # 1 # SAVE # 
  //DIGITART # 1 # SYSTEM_TIME # time 0
  //DIGITART # 1 # TIME # time 0: time 50 on 1
  
  ledSettings config;
  int temp, ledPos;
  const char* time[7] = {"time ", "time ", "on "};
  const char* field[6] = {"num ", "smooth ", "color ", "period ", "TSStart ", "TSEnd "};
  int fieldVal[6];

  if (memcmp(data, PROTOCOL_SEPARATOR_EXT, sizeof(PROTOCOL_SEPARATOR_EXT) - 1) != 0)
    return PROTOCOL_ERR_FORMAT;
  data += sizeof(PROTOCOL_SEPARATOR_EXT) - 1;

  if (commandCode == SYSTEM_TIME)
  {
    temp = strlen(time[0]);
    if (memcmp(data, time[0], temp) != 0)
      return PROTOCOL_ERR_DATA;
    data += temp;
    temp = ProtocolGetNumber(data, &fieldVal[0]);
    if (temp == 0)
      return PROTOCOL_ERR_DATA;
    data += temp;
    rtcSet(fieldVal[0]);
    return PROTOCOL_OK;
  }
  else if (commandCode == TIME)
  {
    for (int i = 0; i < 3; i++)
    {
      temp = strlen(time[i]);
      if (memcmp(data, time[i], temp) != 0)
        return PROTOCOL_ERR_DATA;
      data += temp;
      temp = ProtocolGetNumber(data, &fieldVal[i]);
      if (temp == 0)
        return PROTOCOL_ERR_DATA;
      data += temp;
      if (i < 2)
      {
        if (memcmp(data, PROTOCOL_SEPARATOR_INT, sizeof(PROTOCOL_SEPARATOR_INT) - 1) != 0)
          return PROTOCOL_ERR_DATA;
        data += sizeof(PROTOCOL_SEPARATOR_INT) - 1;
      }
    }
    Time time;
    time.timeFrom.sec = fieldVal[0] & 0xFF;
    time.timeFrom.min = (fieldVal[0] >> 8) & 0xFF;
    time.timeFrom.hour = (fieldVal[0] >> 16) & 0xFF;
    time.timeTo.sec = fieldVal[1] & 0xFF;
    time.timeTo.min = (fieldVal[1] >> 8) & 0xFF;
    time.timeTo.hour = (fieldVal[1] >> 16) & 0xFF;
    time.isTimeMode = fieldVal[2];
    settingsTimeSet(&time);
    ledsSettingsUpdate();
    return PROTOCOL_OK;
  }
  else if (commandCode == SAVE)
  {
    settingsSave();
    return PROTOCOL_OK;
  }
  else if (commandCode == BRIGHT)
  {
    int brightVal;
    const char * bright = "bright ";
    temp = strlen("bright ");
    if (memcmp(data, bright, temp) != 0)
        return PROTOCOL_ERR_DATA;
    data += temp;
    temp = ProtocolGetNumber(data, &brightVal);
    if (temp == 0)
      return PROTOCOL_ERR_DATA;
    settingsBrightSet(brightVal);
    ledsSettingsUpdate();
    return PROTOCOL_OK;
  }
  else if ((commandCode >= SETTINGS_FOR_ALL) && (commandCode <= SETTINGS))
  {
    for (int i = 0; i < 6; i++)
    {
      temp = strlen(field[i]);
      if (memcmp(data, field[i], temp) != 0)
        return PROTOCOL_ERR_DATA;
      data += temp;
      temp = ProtocolGetNumber(data, &fieldVal[i]);
      if (temp == 0)
        return PROTOCOL_ERR_DATA;
      data += temp;
      if (i < 5)
      {
        if (memcmp(data, PROTOCOL_SEPARATOR_INT, sizeof(PROTOCOL_SEPARATOR_INT) - 1) != 0)
          return PROTOCOL_ERR_DATA;
        data += sizeof(PROTOCOL_SEPARATOR_INT) - 1;
      }
    }
  
    config.smooth = (led_mode_t)fieldVal[1];
    config.color[0] = (fieldVal[2] & 0xFF0000) >> 16;
    config.color[1] = (fieldVal[2] & 0xFF00) >> 8;
    config.color[2] = fieldVal[2] & 0xFF;
    config.period = fieldVal[3];
    config.TSStart = fieldVal[4];
    config.TSEnd = fieldVal[5];
    if (commandCode == SETTINGS_FOR_ALL)
    {
      for (int i = 0; i <= fieldVal[0]; i++)
        settingsLedSet(&config, i);
    }
    else if (commandCode == SETTINGS_FOR_CAT)
    {
      settingsLedSet(&config, (uint32_t)fieldVal[0] * 2);
      settingsLedSet(&config, (uint32_t)fieldVal[0] * 2 + 1);
    }
    else if (commandCode == SETTINGS)
    {
      settingsLedSet(&config, (uint32_t)fieldVal[0]);
    }
    ledsSettingsUpdate();
    return PROTOCOL_OK;
  }
  else
    return PROTOCOL_ERR_DATA;
}

static protocol_ret ProtocolParseData(char* data)
{
  if (memcmp(data, PROTOCOL_COMMAND_SETTINGS_FOR_ALL, sizeof(PROTOCOL_COMMAND_SETTINGS_FOR_ALL) - 1) == 0)
    return ProtocolParseDataCommand(data + sizeof(PROTOCOL_COMMAND_SETTINGS_FOR_ALL) - 1, SETTINGS_FOR_ALL);
  else if (memcmp(data, PROTOCOL_COMMAND_SETTINGS_FOR_CAT, sizeof(PROTOCOL_COMMAND_SETTINGS_FOR_CAT) - 1) == 0)
    return ProtocolParseDataCommand(data + sizeof(PROTOCOL_COMMAND_SETTINGS_FOR_CAT) - 1, SETTINGS_FOR_CAT);
  else if (memcmp(data, PROTOCOL_COMMAND_SETTINGS, sizeof(PROTOCOL_COMMAND_SETTINGS) - 1) == 0)
    return ProtocolParseDataCommand(data + sizeof(PROTOCOL_COMMAND_SETTINGS) - 1, SETTINGS); 
  else if (memcmp(data, PROTOCOL_COMMAND_BRIGHT, sizeof(PROTOCOL_COMMAND_BRIGHT) - 1) == 0)
    return ProtocolParseDataCommand(data + sizeof(PROTOCOL_COMMAND_BRIGHT) - 1, BRIGHT);
  else if (memcmp(data, PROTOCOL_COMMAND_SAVE, sizeof(PROTOCOL_COMMAND_SAVE) - 1) == 0)
    return ProtocolParseDataCommand(data + sizeof(PROTOCOL_COMMAND_SAVE) - 1, SAVE);
  else if (memcmp(data, PROTOCOL_COMMAND_SYSTEM_TIME, sizeof(PROTOCOL_COMMAND_SYSTEM_TIME) - 1) == 0)
    return ProtocolParseDataCommand(data + sizeof(PROTOCOL_COMMAND_SYSTEM_TIME) - 1, SYSTEM_TIME);
  else if (memcmp(data, PROTOCOL_COMMAND_TIME, sizeof(PROTOCOL_COMMAND_TIME) - 1) == 0)
    return ProtocolParseDataCommand(data + sizeof(PROTOCOL_COMMAND_TIME) - 1, TIME);
  return PROTOCOL_ERR_FORMAT;
}

static protocol_ret ProtocolMessageCorrectCheck(protocolData* data)
{
  if (!ProtocolSigIsOK(data))
    return PROTOCOL_ERR_SIG;
  if (!ProtocolVerIsOK(data))
    return PROTOCOL_ERR_VER;
  return ProtocolParseData(data->protocolData);
}

protocol_ret ProtocolParser(char* message, unsigned int size)
{
  protocolData msg;
  if (size < ((sizeof(PROTOCOL_SIGNATURE) - 1) + (sizeof(PROTOCOL_SEPARATOR_EXT) - 1) + (sizeof(PROTOCOL_VER) - 1) + (sizeof(PROTOCOL_SEPARATOR_EXT) - 1)))
    return PROTOCOL_ERR_SIZE;
  memset(&msg, 0x00, sizeof(protocolData));
  char* ptr = message;
  msg.protocolSignature = ptr;
  ptr += sizeof(PROTOCOL_SIGNATURE) - 1;
  if (memcmp(ptr, PROTOCOL_SEPARATOR_EXT, sizeof(PROTOCOL_SEPARATOR_EXT) - 1) != 0)
    return PROTOCOL_ERR_FORMAT;
  ptr += sizeof(PROTOCOL_SEPARATOR_EXT) - 1;
  msg.protocolVer = ptr;
  ptr += sizeof(PROTOCOL_VER) - 1;
  if (memcmp(ptr, PROTOCOL_SEPARATOR_EXT, sizeof(PROTOCOL_SEPARATOR_EXT) - 1) != 0)
    return PROTOCOL_ERR_FORMAT;
  ptr += sizeof(PROTOCOL_SEPARATOR_EXT) - 1;
  msg.protocolData = ptr;
  return ProtocolMessageCorrectCheck(&msg);
}
