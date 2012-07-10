//
//  SwarmCCViewController.h
//  NomadsBindle_CC
//
//  Created by Paul Turowski on 7/10/12.
//  Copyright (c) 2012 University of Oregon. All rights reserved.
//

#ifndef SWARMVIEWCONTROLLER
#define SWARMVIEWCONTROLLER

#import <UIKit/UIKit.h>
#import "NSand.h"
#import "NGrain.h"
#import "BindleAppDelegate.h"

@interface SwarmCCViewController : UIViewController
{
    NSand   *appSand;
    BindleAppDelegate *appDelegate;
    @public UITabBarItem __weak *tbi;
}

@property (strong, retain) NSand *appSand;
@property (strong, retain) BindleAppDelegate *appDelegate;
@property (unsafe_unretained, nonatomic) IBOutlet UITabBarItem *tbi;

- (void)dataReadyHandle:(NGrain *)inGrain;

@end
#endif