//
//  DiscussCloudAppDelegate.h
//  DiscussCloud
//
//  Created by Steven Kemper on 5/15/12.
//

#ifndef BINDLEAPPDELEGATE
#define BINDLEAPPDELEGATE

#import <UIKit/UIKit.h>
#import "NSand.h"


@interface BindleAppDelegate : UIResponder <SandDelegate, UIApplicationDelegate>  // INPUT: SandDelegate needed to receive signal

{
    @public NSand   *appSand;
    
}

@property (strong, retain) NSand *appSand;
@property (strong, nonatomic) UIWindow *window;

- (void)dataReadyHandle:(NGrain *)inGrain; // INPUT:  the function we use when WE get data from Sand

@end

#endif