
#ifndef PROTOCOL_H
#define PROTOCOL_H

//SYSTEM FIELDS
#define PROTOCOL_SIGNATURE                 "DIGITART"
#define PROTOCOL_VER                       "1"
#define PROTOCOL_SEPARATOR_EXT             " # "
#define PROTOCOL_SEPARATOR_INT             ": "

//COMMANDS
#define PROTOCOL_COMMAND_SETTINGS "SETTINGS"

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