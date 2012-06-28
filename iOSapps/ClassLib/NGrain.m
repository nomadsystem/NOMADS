//
//  NGrain.m
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 6/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "NGrain.h"

@implementation NGrain

- (id)init //initialization function
{
    //Perhaps unnecessary. . .****STK 6/22/12
    self = [super init]; //Here to get initialization from parent class first
    if (self) { //if we get that initialization, override it
    }
    return self;
}

- (void)setGrainElts_AppID:(Byte)a 
                      Command:(Byte)c 
                     DataType:(Byte)dT 
                      DataLen:(int)dL 
                       String:(NSString *)s
{   
    appID = a;
    command = c;
    dataType = dT;
    dataLen = dL;
    str = s;
}


- (void)setGrainElts_AppID:(Byte)a 
                      Command:(Byte)c 
                     DataType:(Byte)dT 
                      DataLen:(int)dL 
                       Number:(NSNumber *)n
{   
    appID = a;
    command = c;
    dataType = dT;
    dataLen = dL;
    num = n;
}


- (void)print
{
    NSLog(@"NGrain -> print()");
    NSLog(@"appID = %d",appID);
    NSLog(@"command = %d",command);
    NSLog(@"dataType = %d",dataType);
    NSLog(@"dataLen = %d",dataLen);
    NSLog(@"str = %@\n",str);
//    NSLog(@"dataArray = %@", dataArray); //This is temporary until we get other global variables classes made
}

@end
