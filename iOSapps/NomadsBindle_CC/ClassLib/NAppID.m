//
//  NAppID.m
//
//  Initial version, DJT on 6/22/12.
//

#import "NAppID.h"

@implementation NAppID;
- (id)init
{
  self = [super init];
  if (self) {
     SERVER = 0;
     INSTRUCTOR_PANEL = 1;
     C_INSTRUCTOR_PANEL = 2;
     STUDENT_PANEL = 3;
     C_STUDENT_PANEL = 4;
     CENSOR = 5;
     MONITOR = 6;

     //DEBUG = 10;

     WEB_CHAT = 20;
     C_WEB_CHAT = 21;
     DISCUSS_PROMPT = 22;
     C_DISCUSS_PROMPT = 23;
     INSTRUCTOR_DISCUSS = 24;
     C_INSTRUCTOR_DISCUSS = 25;

     TEXT_CHAT = 30;
     C_TEXT_CHAT = 31;
    
     LOGIN = 40;
     C_LOGIN = 41;

     CLOUD_DISPLAY = 50;
     C_CLOUD_DISPLAY = 51;
     CLOUD_CHAT = 52;
     C_CLOUD_CHAT = 53;
     CLOUD_PROMPT = 54;
     C_CLOUD_PROMPT = 55;

     TEACHER_POLL = 60;
     C_TEACHER_POLL = 61;
     STUDENT_POLL = 62;
     C_STUDENT_POLL = 63;
     DISPLAY_POLL = 64;
     C_DISPLAY_POLL = 65;

     SNAKE_GAME = 70;
     C_SNAKE_GAME = 71;

     INSTRUCTOR_SEQUENCER = 80;
     C_INSTRUCTOR_SEQUENCER = 81;
     STUDENT_SEQUENCER = 82;
     C_STUDENT_SEQUENCER = 83;

     STUDENT_SAND_POINTER = 90;
     C_STUDENT_SAND_POINTER = 91;
     INSTRUCTOR_SAND_POINTER = 92;
     C_INSTRUCTOR_SAND_POINTER = 93;

     SOUND_SWARM = 100;
     C_SOUND_SWARM = 101;
     SPHERE_MOVER = 102;

     STUD_EMRG_SYNTH = 110;
     C_STUD_EMRG_SYNTH = 111;
     INSTRUCT_EMRG_SYNTH_PROMPT = 112;
     C_INSTRUCT_EMRG_SYNTH_PROMPT = 113;
  }
  return self;
}

@end
