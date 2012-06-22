//
//  NGrain.h
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 6/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NGrain : NSObject
{
    Byte appID;
    Byte command;
    Byte dataType; //For some reason, it wouldn't compile with "dataType" ***STK 6/15/12
    int dataLen;
    
    NSArray *bArray;
    NSArray *iArray;
    NSArray *fArray;
	NSArray *dArray;
}

- (id) initWithAppID:(Byte)a AndCommand:(Byte)c AndDataType:(Byte)dT AndDataLen:(int)dL AndByteArray:(NSArray *)bA; 
- (id) initWithAppID:(Byte)a AndCommand:(Byte)c AndDataType:(Byte)dT AndDataLen:(int)dL AndIntArray:(NSArray *)iA;
- (id) initWithAppID:(Byte)a AndCommand:(Byte)c AndDataType:(Byte)dT AndDataLen:(int)dL AndFloatArray:(NSArray *)fA;
- (id) initWithAppID:(Byte)a AndCommand:(Byte)c AndDataType:(Byte)dT AndDataLen:(int)dL AndDoubleArray:(NSArray *)dA;

- (void) print;

@end
