//
//  NGrain.m
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 6/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "NGrain.h"



@implementation NGrain
//@synthesize appID;
//@synthesize command;
//@synthesize dataType;
//@synthesize dataLen;
//@synthesize dataArray;
//+ (id)init //initialization function
//{
//    //Perhaps unnecessary. . .****STK 6/22/12
//    self = [super init]; //Here to get initialization from parent class first
//    if (self) { //if we get that initialization, override it
//    }
//    return self;
//}

- (id)initWithGrainElts_AppID:(Byte)a 
            Command:(Byte)c 
           DataType:(Byte)dT 
            DataLen:(int)dL 
          DataArray:(NSData *)dA
{
    return [self initWithGrainElts_AppID:appID 
                                 Command:command 
                                DataType:dataType 
                                 DataLen:dataLen 
                               DataArray:dataArray];
}


- (void)print
{
    NSLog(@"NGrain -> print()");
    NSLog(@"appID = %@",appID);
    NSLog(@"command = %@",command);
    NSLog(@"dataType = %@",dataType);
    NSLog(@"dataLen = %@",dataLen);
    NSLog(@"dataArray = %@", dataArray); //This is temporary until we get other global variables classes made
}

@end
