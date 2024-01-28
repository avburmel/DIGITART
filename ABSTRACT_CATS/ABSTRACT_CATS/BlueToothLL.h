
#ifndef BLUETOOTH_LL_H
#define BLUETOOTH_LL_H

#include <BluetoothSerial.h>

#define CONFIG_BLUETOOTH_MAX_COMMAND_LEN  1024

void BlueToothLLInit(void);
uint32_t  BlueToothLLRead(char* msg);
void BlueToothLLWrite(char* text);

#endif