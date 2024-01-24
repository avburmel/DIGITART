
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

static protocol_ret ProtocolParseDataSettings(char* data)
{
  //DIGITART # 1 # SETTINGS # num 0: smooth 3: color 16777215: period 400: TSStart 0: TSEnd 400
  ledSettings config;
  int temp, ledPos;
  const char* field[6] = {"num ", "smooth ", "color ", "period ", "TSStart ", "TSEnd "};
  int fieldVal[6];

  if (memcmp(data, PROTOCOL_SEPARATOR_EXT, sizeof(PROTOCOL_SEPARATOR_EXT) - 1) != 0)
    return PROTOCOL_ERR_FORMAT;
  data += sizeof(PROTOCOL_SEPARATOR_EXT) - 1;

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
  
  config.smooth = fieldVal[1];
  config.color[0] = (fieldVal[2] & 0xFF0000) >> 16;
  config.color[1] = (fieldVal[2] & 0xFF00) >> 8;
  config.color[2] = fieldVal[2] & 0xFF;
  config.period = fieldVal[3];
  config.TSStart = fieldVal[4];
  config.TSEnd = fieldVal[5];
  settingsLedSet(&config, (uint32_t)fieldVal[0]);
  ledsSettingsUpdate();
  settingsSave();
  return PROTOCOL_OK;
}

static protocol_ret ProtocolParseData(char* data)
{
  if (memcmp(data, PROTOCOL_COMMAND_SETTINGS, sizeof(PROTOCOL_COMMAND_SETTINGS) - 1) == 0)
    return ProtocolParseDataSettings(data + sizeof(PROTOCOL_COMMAND_SETTINGS) - 1);
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
