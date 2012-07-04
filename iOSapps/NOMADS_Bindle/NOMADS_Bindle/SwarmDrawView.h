//
//  SwarmDrawView.h
//
//  Created by Steven Kemper on 7/3/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SwarmDrawView : UIView
{

    CGPoint myFingerPoint;
}
@property (nonatomic) CGPoint myFingerPoint;
- (void)clearAll;
- (void)endTouches:(NSSet *)touches;

@end
