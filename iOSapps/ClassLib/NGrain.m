//
//  NGrain.m
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 6/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "NGrain.h"
#import "NGlobals.h"

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
                    Uint8:(uint8_t *)bA
{   
    appID = a;
    command = c;
    dataType = dT;
    dataLen = dL;
    bArray = bA;
}


- (void)setGrainElts_AppID:(Byte)a 
                      Command:(Byte)c 
                     DataType:(Byte)dT 
                      DataLen:(int)dL 
                       Int32:(int *)iA; 
{   
    appID = a;
    command = c;
    dataType = dT;
    dataLen = dL;
    iArray = iA;
}

- (void)setGrainElts_AppID:(Byte)a 
                   Command:(Byte)c 
                  DataType:(Byte)dT 
                   DataLen:(int)dL 
                   Float32:(float *)fA; 
{   
    appID = a;
    command = c;
    dataType = dT;
    dataLen = dL;
    fArray = fA;
}

// DT:  fix for different data types
- (void)print
{
    int i;
    NSLog(@"NGrain -> print()");
    NSLog(@"appID = %d",appID);
    NSLog(@"command = %d",command);
    NSLog(@"dataType = %d",dataType);
    NSLog(@"dataLen = %d",dataLen);
    if (dataType == CHAR) {
        NSLog(@"str = %@\n",str);
    }
    else if (dataType == UINT8) {
        NSLog(@"uint8:");
        for(i=0;i<dataLen;i++) {
            NSLog(@" %d ",bArray[i]);
        }
    }
    else if (dataType == INT32) {
        NSLog(@"int32:");
        for(i=0;i<dataLen;i++) {
            NSLog(@" %d ",iArray[i]);
        }
    }
    else if (dataType == FLOAT32) {
        NSLog(@"float32:");
        for(i=0;i<dataLen;i++) {
            NSLog(@" %f ",fArray[i]);
        }
    }
}
    
    
@end
