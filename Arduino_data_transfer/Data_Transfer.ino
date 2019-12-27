#include <SoftwareSerial.h>
#include <MsTimer2.h>

SoftwareSerial BTSerial(2,3);
char buffer[10], buf[5];
int Green = 12,Red = 10, tpot, nps, pos = 0, temp, i, j, k = 0;
int LedState = LOW, LedStart = LOW;
char led_flag = 0, state_flag = 0, index = 0;

void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600);
  pinMode(Green, OUTPUT);
  pinMode(Red, OUTPUT);
}

void loop() {
    if(state_flag == 0){
      while(BTSerial.available()){
        buffer[index] = BTSerial.read();
        index++;
      }
      pos = atoi(buffer);
      if(pos > 0 && pos < 600) {
        tpot = pos / 100;
        nps = pos % 100;
        index = 0;
      }
      else if(pos == 999){
        state_flag = 1;
      }
      delay(10);
      //memset(buffer, NULL, sizeof(buffer));
    }
    else if(state_flag == 1){
      if(led_flag == 0){
        for(i = 0; i< 10; ++i){
          LedStart = !LedStart;
          digitalWrite(Red, LedStart);
          digitalWrite(Green, LedStart);
          delay(500);
        }
        
        MsTimer2::set(tpot*500, ledISR); 
        MsTimer2::start();
        led_flag = 1;
      }
      temp =analogRead(A0);
      sprintf(buf, "%4d", temp);
      for(i=0; i<4; i++){
        BTSerial.write(buf[i]); 
      }
      Serial.println(temp);
      delay(100);
      index = 0;
      k++;
      if(k > nps * tpot * 10){
        MsTimer2::stop();
        digitalWrite(Red, LOW);
        digitalWrite(Green, LOW);
        led_flag = 0;
        k = 0;
        state_flag = 0;
        memset(buffer, NULL, sizeof(buffer));
      }
    }
}
void ledISR(){
      if(LedState == LOW)
        LedState = HIGH;
      else
        LedState = LOW;
        
      digitalWrite(Green, LedState);
      digitalWrite(Red, !LedState);
}
