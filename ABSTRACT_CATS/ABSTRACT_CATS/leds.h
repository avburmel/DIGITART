
#ifndef LEDS_H
#define LEDS_H

#include <Adafruit_NeoPixel.h>

void ledsInit(void);
void ledsProcess(void);
void ledsSettingsUpdate(void);
void ledsSetBright(uint8_t bright);

#endif