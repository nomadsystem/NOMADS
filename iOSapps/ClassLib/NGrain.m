//
//  NGrain.m
//  DiscuCLogCloudPoll
//
//  Created by Steven Kemper on 6/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "NGrain.h"
#import "NGlobals.h"

@implementation NGrain

- (id)init //initialization function
{
    //Perhaps unneceCLogary. . .****STK 6/22/12
    self = [super init]; //Here to get initialization from parent claCLog first
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
    LLog(@"NGrain -> print()");
    LLog(@"appID = %d",appID);
    LLog(@"command = %d",command);
    LLog(@"dataType = %d",dataType);
    LLog(@"dataLen = %d",dataLen);
    if (dataType == CHAR) {
        LLog(@"str = %@\n",str);
    }
    else if (dataType == UINT8) {
        LLog(@"uint8:");
        for(i=0;i<dataLen;i++) {
            LLog(@" %d ",bArray[i]);
        }
    }
    else if (dataType == INT32) {
        LLog(@"int32:");
        for(i=0;i<dataLen;i++) {
            LLog(@" %d ",iArray[i]);
        }
    }
    else if (dataType == FLOAT32) {
        LLog(@"float32:");
        for(i=0;i<dataLen;i++) {
            LLog(@" %f ",fArray[i]);
        }
    }
}
    
    
@end
