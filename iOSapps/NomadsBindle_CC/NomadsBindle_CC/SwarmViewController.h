//
//  SwarmViewController.h
//
//  Created by Steven Kemper on 7/3/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef SWARMVIEWCONTROLLER
#define SWARMVIEWCONTROLLER

#import <UIKit/UIKit.h>
#import "NSand.h"
#import "NGrain.h"
#import "BindleAppDelegate.h"

@interface SwarmViewController : UIViewController <SandDelegate>
{
    NSand   *appSand;
    BindleAppDelegate *appDelegate;
    @public UITabBarItem __weak *tbi;
}
@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;
@property (weak, nonatomic) IBOutlet UITabBarItem *tbi;

- (void)dataReadyHandle:(NGrain *)inGrain;

@end
#endif