//
//  TouchDrawView.h
//  TouchTracker
//
//  Created by Steven Kemper on 7/3/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Line.h"

@interface TouchDrawView : UIView
{
    NSMutableDictionary *linesInProcess;
    NSMutableArray *completeLines;
    CGPoint *myFingerPoint;
    Line *newLine;
}
@property (nonatomic) Line *myLine;
- (void)clearAll;
- (void)endTouches:(NSSet *)touches;

@end
