
#include "BlueToothLL.h"
#include "protocol.h"
#include "settings.h"
#include "config.h"
#include "leds.h"

char message[CONFIG_BLUETOOTH_MAX_COMMAND_LEN];

void setup() {
  settingsInit();
  ledsInit(); 
  BlueToothLLInit();
}

void loop() {
  uint32_t messageLen = BlueToothLLRead(message); 
  if (messageLen)    
  {
    if (ProtocolParser(message, messageLen) == PROTOCOL_OK)
    {
      BlueToothLLWrite("OK");
    }
    else
      BlueToothLLWrite("ERROR");
  }
  ledsProcess();
}
