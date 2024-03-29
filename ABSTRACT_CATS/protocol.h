
#ifndef PROTOCOL_H
#define PROTOCOL_H

//SYSTEM FIELDS
#define PROTOCOL_SIGNATURE                 "DIGITART"
#define PROTOCOL_VER                       "1"
#define PROTOCOL_SEPARATOR_EXT             " # "
#define PROTOCOL_SEPARATOR_INT             ": "

//COMMANDS
enum commands_t 
{
  SAVE = 0,
  SYSTEM_TIME,
  TIME,
  BRIGHT,
  SETTINGS_FOR_ALL = 32,
  SETTINGS_FOR_CAT,
  SETTINGS
};

#define PROTOCOL_COMMAND_SAVE                      "SAVE" 
#define PROTOCOL_COMMAND_BRIGHT                    "BRIGHT" 
#define PROTOCOL_COMMAND_SETTINGS_FOR_ALL          "SETTINGS_FOR_ALL" 
#define PROTOCOL_COMMAND_SETTINGS_FOR_CAT          "SETTINGS_FOR_CAT" 
#define PROTOCOL_COMMAND_SETTINGS                  "SETTINGS"
#define PROTOCOL_COMMAND_SYSTEM_TIME               "SYSTEM_TIME"
#define PROTOCOL_COMMAND_TIME                      "TIME"


enum protocol_ret 
{
  PROTOCOL_OK = 0,
  PROTOCOL_ERR_SIG,
  PROTOCOL_ERR_VER,
  PROTOCOL_ERR_FORMAT,
  PROTOCOL_ERR_SIZE,
  PROTOCOL_ERR_DATA
};

struct protocolData 
{
  char*    protocolSignature;
  char*    protocolVer;
  char*    protocolData;
};

protocol_ret ProtocolParser(char* message, unsigned int size);

#endif