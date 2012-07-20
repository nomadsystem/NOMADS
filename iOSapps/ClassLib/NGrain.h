//
//  NGrain.h
//  DiscussCloudPoll
//
//  Created by Steven Kemper on 6/22/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//
#ifndef NGRAIN_H
#define NGRAIN_H


#import <Foundation/Foundation.h>

@interface NGrain : NSObject
{
    @public Byte appID;
    @public Byte command;
    @public Byte dataType; 
    @public int dataLen;
    @public uint8_t *uArray;
    @public NSString *str;
    @public int *iArray;
    @public float *fArray;
    
}

- (void) setGrainElts_AppID:(Byte)a 
                    Command:(Byte)c 
                   DataType:(Byte)dT 
                    DataLen:(int)dL 
                      Uint8:(Byte *)bA; 

- (void) setGrainElts_AppID:(Byte)a 
                       Command:(Byte)c 
                      DataType:(Byte)dT 
                       DataLen:(int)dL 
                         String:(NSString *)s; 

- (void)setGrainElts_AppID:(Byte)a 
                   Command:(Byte)c 
                  DataType:(Byte)dT 
                   DataLen:(int)dL 
                   Int32:(int *)iA; 

- (void)setGrainElts_AppID:(Byte)a 
                   Command:(Byte)c 
                  DataType:(Byte)dT 
                   DataLen:(int)dL 
                  Float32:(float *)fA; 

- (void) print;

@end

#endif