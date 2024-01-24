
#ifndef BLUETOOTH_LL_H
#define BLUETOOTH_LL_H

#include <BluetoothSerial.h>

void BlueToothLLInit(void);
uint32_t  BlueToothLLRead(char* msg);
void BlueToothLLWrite(char* text);

#endif