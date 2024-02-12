
#include "BlueToothLL.h"

BluetoothSerial BL;

void BlueToothLLInit()
{
  BL.begin("DIGITART_CATS_TREE");
}

static bool BlueToothLLAvailable()
{
  return BL.available();
}

uint32_t BlueToothLLRead(char* msg)
{
  static uint32_t messageLen = 0;
  while (BlueToothLLAvailable())
  {
    if (messageLen >= CONFIG_BLUETOOTH_MAX_COMMAND_LEN)
      messageLen = 0;
    msg[messageLen++] = BL.read();
    if (msg[messageLen - 1] == 0)
    {
      int len = messageLen;
      messageLen = 0;
      return len;
    }      
  } 
  return 0;
}

void BlueToothLLWrite(char* text)
{
  BL.println(text);
  BL.write(0);
}
