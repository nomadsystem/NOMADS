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

- (id)initWithAppID:(Byte)a AndCommand:(Byte)c AndDataType:(Byte)dT AndDataLen:(int)dL AndByteArray:(NSArray *)bA
{
    return [self initWithAppID:appID AndCommand:command AndDataType:dataType AndDataLen:dataLen AndByteArray:bArray];
}

- (id)initWithAppID:(Byte)a AndCommand:(Byte)c AndDataType:(Byte)dT AndDataLen:(int)dL AndIntArray:(NSArray *)iA
{
    return [self initWithAppID:appID AndCommand:command AndDataType:dataType AndDataLen:dataLen AndIntArray:iArray];
}

- (id)initWithAppID:(Byte)a AndCommand:(Byte)c AndDataType:(Byte)dT AndDataLen:(int)dL AndFloatArray:(NSArray *)fA
{
    return [self initWithAppID:appID AndCommand:command AndDataType:dataType AndDataLen:dataLen AndFloatArray:fArray];
}

- (id)initWithAppID:(Byte)a AndCommand:(Byte)c AndDataType:(Byte)dT AndDataLen:(int)dL AndDoubleArray:(NSArray *)dA
{
    return [self initWithAppID:appID AndCommand:command AndDataType:dataType AndDataLen:dataLen AndDoubleArray:dArray];
}

- (void)print
{
    NSLog(@"NGrain -> print()");
    NSLog(@"appID = %@",appID);
    NSLog(@"command = %@",command);
    NSLog(@"dataType = %@",dataType);
    NSLog(@"dataLen = %@",dataLen);
    NSLog(@"Array = %@", bArray); //This is temporary until we get other global variables classes made
}

@end
