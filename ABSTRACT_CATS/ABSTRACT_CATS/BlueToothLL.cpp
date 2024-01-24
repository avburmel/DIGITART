
#include "BlueToothLL.h"

BluetoothSerial BL;

void BlueToothLLInit()
{
  BL.begin("DIGITART_ABSTRACT_CATS");
}

static bool BlueToothLLAvailable()
{
  return BL.available();
}

uint32_t BlueToothLLRead(char* msg)
{
  uint32_t messageLen = 0;
  while (BlueToothLLAvailable())
    msg[messageLen++] = BL.read();
  msg[messageLen] = 0;
  return messageLen;
}

void BlueToothLLWrite(char* text)
{
  BL.println(text);
}
