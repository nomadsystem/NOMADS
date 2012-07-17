//
//  Cursor.h
//  NomadsBindle_CC
//
//  Created by Paul Turowski on 7/16/12.
//  Copyright (c) 2012 University of Oregon. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MovableObject.h"

@interface Cursor : MovableObject {
    CCAnimation *swellAnim;
    CCAnimation *shrinkAnim;
//    BOOL beingMoved;
}

@property (nonatomic, retain) CCAnimation *swellAnim;
@property (nonatomic, retain) CCAnimation *shrinkAnim;
//@property (readwrite) BOOL beingMoved;

@end
