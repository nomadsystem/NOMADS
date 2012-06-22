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
    @public Byte appID;
    @public Byte command;
    @public Byte dataType; 
    @public int dataLen;
    @public NSData *dataArray; //Use NSData as Byte Buffer
}

//@property Byte appID;
//@property Byte command;
//@property Byte dataType;
//@property int dataLen;
//@property NSData *dataArray;


- (id) initWithGrainElts_AppID:(Byte)a 
                       Command:(Byte)c 
                      DataType:(Byte)dT 
                       DataLen:(int)dL 
                         Array:(NSData *)dA; 


- (void) print;

@end
