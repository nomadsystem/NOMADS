//
//  NGrain.h
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 6/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//
#ifndef NGRAIN
#define NGRAIN


#import <Foundation/Foundation.h>

@interface NGrain : NSObject
{
    @public Byte appID;
    @public Byte command;
    @public Byte dataType; 
    @public int dataLen;
    @public NSString *str;
    @public NSNumber *num;
}

- (void) setGrainElts_AppID:(Byte)a 
                       Command:(Byte)c 
                      DataType:(Byte)dT 
                       DataLen:(int)dL 
                         String:(NSString *)str; 

- (void) setWithGrainElts_AppID:(Byte)a 
                       Command:(Byte)c 
                      DataType:(Byte)dT 
                       DataLen:(int)dL 
                        Number:(NSString *)num; 

- (void) print;

@end

#endif